package com.cmall.productcenter.webfunc;

import java.math.BigDecimal;
import java.util.Date;


import com.cmall.productcenter.common.DateUtil;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductService;
import com.cmall.productcenter.service.ProductSkuPriceThread;
import com.cmall.systemcenter.model.ScFlowMain;
import com.cmall.systemcenter.service.FlowService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncModSkuiPriceStocknfoForCf extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		mAddMaps.put("start_date", mDataMap.get("start_date"));
		mAddMaps.put("end_date", mDataMap.get("end_date"));
		if (mAddMaps.get("start_date") == null || "".equals(mAddMaps.get("start_date"))) {
			mAddMaps.replace("start_date", DateUtil.getSysDateString());
			mAddMaps.put("do_type", "1");
		}else{
			mAddMaps.put("do_type", "2");
		}
		if (mAddMaps.get("end_date") == null || "".equals(mAddMaps.get("end_date"))) {
			mAddMaps.replace("end_date", "2099-12-31");
		}
		ProductService service = new ProductService();
		MUserInfo sys = UserFactory.INSTANCE.create();
		if (sys == null) {
			mResult.inErrorMessage(941901061, bInfo(941901064));
		} else if (!"".equals(verifyFormData(mAddMaps))) {
			// 验证表单数据是否正确
			mResult.inErrorMessage(941901061, verifyFormData(mAddMaps));
		} else {
			MDataMap skuMap = DbUp.upTable("pc_skuinfo").one("uid", mAddMaps.get("uid"));
			String productCode = skuMap.get("product_code");

			PcProductinfo pro = service.getProduct(productCode);
			ProductSkuInfo sku = null;
			BigDecimal old_seller_price = null;
			for (int i = 0; i < pro.getProductSkuInfoList().size(); i++) {
				ProductSkuInfo skuInfo = pro.getProductSkuInfoList().get(i);
				if (skuMap.get("sku_code").equals(skuInfo.getSkuCode())) {
					sku = skuInfo;
					old_seller_price = skuInfo.getSellPrice();
					pro.getProductSkuInfoList().get(i).setSellPrice(new BigDecimal(mAddMaps.get("sell_price")));
					break;
				}
			}
			if (sku != null) {
				sku.setSellPrice(old_seller_price);
				createFlow(sku, mAddMaps);
				Date startDate = DateUtil.toDate(mAddMaps.get("start_date"));
				Date nowTime = DateUtil.toDate(DateUtil.getSysDateString());
				/*
				 * 如果当前日期与开始日期相同，直接更新价格
				 */
				if (startDate.compareTo(nowTime) == 0) {
					// new ProductSkuPriceService().updateSkupriceTimeScope();
					Thread thread = new Thread(new ProductSkuPriceThread());
					thread.start();
				}
				mResult.setResultMessage(bInfo(941901060));
			} else {
				mResult.inErrorMessage(941901061, "sku商品不存在");
			}
		}

		return mResult;
	}

	/**
	 * 
	 * 方法: createFlow <br>
	 * 描述: 创建商品价格审批流程 <br>
	 * 作者: 张海宇 zhanghaiyu@huijiayou.cn<br>
	 * 时间: 2016年7月5日 上午9:12:17
	 * 
	 * @param sku
	 * @param updateMap
	 * @return
	 */
	public boolean createFlow(ProductSkuInfo sku, MDataMap updateMap) {
		boolean flag = false;
		ScFlowMain flow = new ScFlowMain();
		// 流程类型：449717230013 商品价格审批
		flow.setFlowType("449717230013");
		// 创建人
		flow.setCreator(UserFactory.INSTANCE.create().getUserCode());
		// 更新人
		flow.setUpdator(UserFactory.INSTANCE.create().getUserCode());
		// 创建时间
		flow.setCreateTime(DateUtil.getSysDateTimeString());
		// 更新时间
		flow.setUpdateTime(DateUtil.getSysDateTimeString());
		// 外部单据号，此处存储sku_code
		flow.setOuterCode(sku.getSkuCode());
		// 外部标题
		flow.setFlowTitle("商品修改价格");
		// 流程备注
		flow.setFlowRemark("在指定日期范围内商品修改价格生效");
		// 是否结束
		flow.setFlowIsend(1);
		// 当前状态，设置审批状态为审批通过
		flow.setCurrentStatus("4497172300130002");
		/* 创建工作流 */
		RootResult rrFlow = new FlowService().CreateFlow(flow);
		if (rrFlow != null) {
			String flowCode = rrFlow.getResultMessage();
			if (flowCode != null && !"".equals(flowCode)) {
				// 添加价格变更信息到pc_skuprice_change_flow
				MDataMap changeFlow = new MDataMap();
				// 流程编号
				changeFlow.put("flow_code", flowCode);
				// 商品编号
				changeFlow.put("product_code", sku.getProductCode());
				// sku编码
				changeFlow.put("sku_code", sku.getSkuCode());
				// 成本价（旧）
				changeFlow.put("cost_price_old", sku.getCostPrice().toString());
				// 成本价
				changeFlow.put("cost_price", sku.getCostPrice().toString());
				// 销售价（旧）
				changeFlow.put("sell_price_old", sku.getSellPrice().toString());
				// 销售价
				changeFlow.put("sell_price", updateMap.get("sell_price"));
				// 开始日期
				changeFlow.put("start_time", updateMap.get("start_date") != null ? updateMap.get("start_date") : "");
				// 结束日期
				changeFlow.put("end_time", updateMap.get("end_date") != null ? updateMap.get("end_date") : "");
				// 是否还原:0未还原,1已还原
				changeFlow.put("is_delete", "0");
				// 状态
				changeFlow.put("status", "4497172300130002");
				// 价格变更类型
				changeFlow.put("do_type", updateMap.get("do_type"));
				DbUp.upTable("pc_skuprice_change_flow").dataInsert(changeFlow);
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 
	 * 方法: verifyFormData <br>
	 * 描述: 验证验证表单数据是否正确 <br>
	 * 作者: 张海宇 zhanghaiyu@huijiayou.cn<br>
	 * 时间: 2016年7月5日 下午1:55:55
	 * 
	 * @param map
	 * @return
	 */
	public static String verifyFormData(MDataMap map) {
		String error = "";
		String start_date = map.get("start_date");
		String end_date = map.get("end_date");
		String sell_price = map.get("sell_price");
		String regex = "^(-?[1-9]\\d*\\.?\\d*)|(-?0\\.\\d*[1-9])|(-?[0])|(-?[0]\\.\\d*)$";
		if (sell_price == null || "".equals(sell_price)) {
			error = "销售价格不能为空";
		} else if (!sell_price.matches(regex)) {
			error = "销售价格只能是数字";
		}
		// else if (start_date == null || "".equals(start_date)) {
		// error = "开始日期不能为空";
		// } else if (end_date == null || "".equals(end_date)) {
		// error = "结束日期不能为空";
		// }
		else {
			Date startDate = DateUtil.toDate(start_date);
			Date endDate = DateUtil.toDate(end_date);
			Date nowTime = DateUtil.toDate(DateUtil.getSysDateString());
			if (startDate.compareTo(nowTime) < 0) {
				error = "开始日期必须大于等于当前日期";
			} else if (endDate.compareTo(nowTime) < 0) {
				error = "结束日期必须大于等于当前日期";
			} else if (endDate.compareTo(startDate) < 0) {
				error = "开始日期必须小于或等于结束日期";
			}
		}
		return error;
	}
}
