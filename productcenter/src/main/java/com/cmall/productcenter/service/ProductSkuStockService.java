package com.cmall.productcenter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.cmall.productcenter.common.Constants;
import com.cmall.productcenter.common.SkuCommon;
import com.cmall.systemcenter.common.DateUtil;
import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.websupport.UserSupport;

/**
 * 商品库存审批
 * 
 * @author pang_jhui
 *
 */
public class ProductSkuStockService extends BaseClass {

	/**
	 * 获取库存更新列表
	 * 
	 * @param flowCode
	 *            流程编号
	 * @param productCode
	 *            产品编号
	 * @param status
	 *            状态
	 * @return 商品价格更新信息
	 */
	public List<Map<String, Object>> getSkuStockFlowList(String flowCode, String productCode, String status) {

		return DbUp.upTable("sc_skunum_change").listByWhere("flow_code", flowCode, "product_code", productCode,
				"deal_status", status);

	}

	/**
	 * 更新商品库存审批过程信息
	 * 
	 * @param mDataMap
	 */
	public void update(MDataMap mDataMap) {

		DbUp.upTable("sc_skunum_change").update(mDataMap);

	}

	/**
	 * 更新状态
	 * 
	 * @param flowCode
	 *            流程编号
	 * @param productCode
	 *            商品编号
	 * @param fromStatus
	 *            起点状态
	 * @param toStatus
	 *            终点状态
	 */
	public void updateStatus(String flowCode, String productCode) {

		List<Map<String, Object>> dataMaps = getSkuStockFlowList(flowCode, productCode,
				Constants.SKU_STOCK_CHANGE_STATUS_WAIT);

		if (dataMaps != null) {

			for (Map<String, Object> dataMap : dataMaps) {

				MDataMap updateDataMap = new MDataMap(dataMap);

				updateDataMap.put("status", Constants.SKU_STOCK_CHANGE_STATUS_REJECT);

				update(updateDataMap);

			}

		}

	}

	/**
	 * 更新sku价格且更新过程状态
	 * 
	 * @param flowCode
	 *            流程编号
	 * @param productCode
	 *            商品编号
	 * @param fromStatus
	 *            起点状态
	 * @param toStatus
	 *            终点状态
	 * @throws Exception
	 */
	public void updateSkuStockAndStatus(String flowCode, String productCode) throws Exception {

		List<Map<String, Object>> dataMaps = getSkuStockFlowList(flowCode, productCode,
				Constants.SKU_STOCK_CHANGE_STATUS_WAIT);

		if (dataMaps != null) {

			List<String> skuCodeList = new ArrayList<String>();

			for (Map<String, Object> dataMap : dataMaps) {

				MDataMap updateDataMap = new MDataMap(dataMap);

				String skuCode = updateDataMap.get("sku_code");

				String oper_type = updateDataMap.get("operate_type");

				int changeNum = Integer.parseInt(updateDataMap.get("change_num"));

				int stock = isSkuNumSuff(skuCode, oper_type, changeNum);

				if (stock < 0) {

					skuCodeList.add(updateDataMap.get("sku_code"));

				}

			}

			if (skuCodeList.size() > 0) {

				String skuCodes = StringUtils.join(skuCodeList.toArray(), ",");

				throw new Exception(bInfo(941901130, skuCodes));

			} else {

				for (Map<String, Object> dataMap : dataMaps) {

					MDataMap updateDataMap = new MDataMap(dataMap);

					updateDataMap.put("deal_status", Constants.SKU_STOCK_CHANGE_STATUS_FINISH);

					updateSkuStock(updateDataMap);

					update(updateDataMap);

				}

			}

			PlusHelperNotice.onChangeProductInfo(productCode);

			new ProductJmsSupport().onChangeForProductChangeAll(productCode);

		}

	}

	/**
	 * 更新库存信息
	 * 
	 * @param mDataMap
	 */
	public int updateSkuStock(MDataMap mDataMap) {

		String oper_type = mDataMap.get("operate_type");

		int changeNum = Integer.parseInt(mDataMap.get("change_num"));

		MDataMap queryDataMap = DbUp.upTable("pc_skuinfo").one("product_code", mDataMap.get("product_code"), "sku_code",
				mDataMap.get("sku_code"));

		int stockNumOld = Integer.parseInt(queryDataMap.get("stock_num"));

		stockNumOld = calStockNum(oper_type, stockNumOld, changeNum);

		if (stockNumOld >= 0) {

			queryDataMap.put("stock_num", Integer.toString(stockNumOld));

			DbUp.upTable("pc_skuinfo").dataUpdate(queryDataMap, "", "product_code,sku_code");

		}

		stockNumOld = updateScStoreSkuNum(mDataMap.get("sku_code"), oper_type, changeNum);

		return stockNumOld;

	}

	/**
	 * 更新sku库存
	 * 
	 * @param skuCode
	 *            变更库存
	 * @param changeNum
	 */
	public int updateScStoreSkuNum(String skuCode, String oper_type, int changeNum) {

		MDataMap mDataMap = DbUp.upTable("sc_store_skunum").one("sku_code", skuCode);

		int oldStockNum = 0;

		if (mDataMap != null) {

			String oldStockNumStr = mDataMap.get("stock_num");

			if (StringUtils.isNotBlank(oldStockNumStr)) {

				oldStockNum = Integer.parseInt(oldStockNumStr);

			}

			oldStockNum = calStockNum(oper_type, oldStockNum, changeNum);

			if (oldStockNum >= 0) {

				mDataMap.put("stock_num", Integer.toString(oldStockNum));

				DbUp.upTable("sc_store_skunum").update(mDataMap);

				recordStockLog(skuCode, changeNum, oldStockNum, Integer.parseInt(oldStockNumStr));

			}
		}

		return oldStockNum;

	}

	/**
	 * 计算库存
	 * 
	 * @param oper_type
	 *            操作类型
	 * @param stockNumOld
	 *            前库存
	 * @param changeNum
	 *            改变数
	 * @return 计算后的库存
	 */
	public int calStockNum(String oper_type, int stockNumOld, int changeNum) {

		if (StringUtils.equals(oper_type, Constants.STOCK_OPERATE_TYPE_ADD)) {
			// 由增加更改为增加到 2016-07-26 zhy
			// stockNumOld = stockNumOld+changeNum;
			stockNumOld = changeNum;

		}

		if (StringUtils.equals(oper_type, Constants.STOCK_OPERATE_TYPE_DECREASE)) {
			// 由减少更改为减少到 2016-07-26 zhy
			// stockNumOld = stockNumOld-changeNum;
			stockNumOld = changeNum;

		}

		return stockNumOld;

	}

	/**
	 * 记录库存日志
	 * 
	 * @param skuCode
	 *            sku编号
	 * @param changeNum
	 *            变更数量
	 * @param nowStock
	 *            目前库存
	 * @param oldStock
	 *            变更之前的库存
	 */
	public void recordStockLog(String skuCode, int changeNum, int nowStock, int oldStock) {

		String createTime = DateUtil.getSysDateTimeString();

		UserSupport support = new UserSupport();

		String operator = support.getUserInfo().getUserCode();

		MDataMap mDataMap = new MDataMap();

		mDataMap.put("code", skuCode);

		mDataMap.put("create_time", createTime);

		mDataMap.put("create_user", operator);

		mDataMap.put("change_stock", Integer.toString(changeNum));

		mDataMap.put("old_stock", Integer.toString(oldStock));

		mDataMap.put("now_stock", Integer.toString(nowStock));

		mDataMap.put("change_type", SkuCommon.SkuStockChangeTypeChangeProduct);

		DbUp.upTable("lc_stockchange").dataInsert(mDataMap);

	}

	/**
	 * 判断库存是否充足
	 * 
	 * @param skuCode
	 *            变更库存
	 * @param changeNum
	 */
	public int isSkuNumSuff(String skuCode, String oper_type, int changeNum) {

		MDataMap mDataMap = DbUp.upTable("sc_store_skunum").one("sku_code", skuCode);

		int oldStockNum = 0;

		if (mDataMap != null) {

			String oldStockNumStr = mDataMap.get("stock_num");

			if (StringUtils.isNotBlank(oldStockNumStr)) {

				oldStockNum = Integer.parseInt(oldStockNumStr);

			}

			oldStockNum = calStockNum(oper_type, oldStockNum, changeNum);

		}

		return oldStockNum;

	}

}
