package com.cmall.ordercenter.webfunc;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.common.DateUtil;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductService;
import com.cmall.productcenter.service.ProductSkuPriceService;
import com.cmall.productcenter.service.ProductSkuPriceThread;
import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.model.ScFlowMain;
import com.cmall.systemcenter.service.FlowService;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebField;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 修改产品售价
 * @author zb
 *
 */
public class FuncChangeSkuPriceService {

	public void funcDo(List<Map<String,Object>> listMap) {
		MWebResult mResult = new MWebResult();
		
		boolean runSkuPriceChange = false;
        for (Map<String, Object> mAddMaps : listMap) {   	
    		ProductService service = new ProductService();
    		MUserInfo sys = UserFactory.INSTANCE.create();
    		if (sys == null) {
    			mResult.inErrorMessage(941901061, bInfo(941901064));
    		} else {                             
    			String productCode = mAddMaps.get("product_code").toString();
    			MDataMap productInfoMap = DbUp.upTable("pc_productinfo").one("product_code", productCode);
    			String seller_type = WebHelper.getSellerType(productInfoMap.get("small_seller_code"));
    			if (StringUtils.isNotBlank(seller_type)
    					&& !productInfoMap.get("small_seller_code").equals("SF03KJT")
    					&& AppConst.MANAGE_CODE_HOMEHAS.equals(productInfoMap.get("seller_code"))) {
    				productCode = productCode + "_1";
    			}
    			PcProductinfo pro = service.getProduct(productCode);
    			ProductSkuInfo sku = null;
    			BigDecimal old_seller_price = null;
    			for (int i = 0; i < pro.getProductSkuInfoList().size(); i++) {
    				ProductSkuInfo skuInfo = pro.getProductSkuInfoList().get(i);
    				if (mAddMaps.get("sku_code").toString().equals(skuInfo.getSkuCode())) {
    					sku = skuInfo;
    					old_seller_price = skuInfo.getSellPrice();
    					pro.getProductSkuInfoList().get(i).setSellPrice(new BigDecimal(mAddMaps.get("sell_price").toString()));
    					break;
    				}
    			}
    			if (sku != null) {
    				sku.setSellPrice(old_seller_price);
    				createFlow(sku, mAddMaps);
    				Date startDate = DateUtil.toDate(mAddMaps.get("start_date").toString());
    				Date nowTime = DateUtil.toDate(DateUtil.getSysDateString());
    				/*
    				 * 如果当前日期与开始日期相同，直接更新价格
    				 */
    				if (startDate.compareTo(nowTime) == 0) {
    					// new ProductSkuPriceService().updateSkupriceTimeScope();
    					runSkuPriceChange = true;
    				}
    				mResult.setResultMessage(bInfo(941901060));
    			} else {
    				mResult.inErrorMessage(941901061, "sku商品不存在");
    			}
    		}
		}
        
        // 最后统一执行价格变更逻辑
        if(runSkuPriceChange) {
			Thread thread = new Thread(new ProductSkuPriceThread());
			thread.start();
        }
	}

	/**
	 * 
	 * 方法: createFlow <br>
	 * 描述: 创建商品价格审批流程 <br>
	 * @param sku
	 * @param updateMap
	 * @return
	 */
	public boolean createFlow(ProductSkuInfo sku, Map<String,Object> updateMap) {
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
				changeFlow.put("sell_price", updateMap.get("sell_price").toString());
				// 开始日期
				changeFlow.put("start_time", updateMap.get("start_date").toString() != null ? updateMap.get("start_date").toString() : "");
				// 结束日期
				changeFlow.put("end_time", updateMap.get("end_date").toString() != null ? updateMap.get("end_date").toString() : "");
				// 是否还原:0未还原,1已还原
				changeFlow.put("is_delete", "0");
				// 状态
				changeFlow.put("status", "4497172300130002");
				DbUp.upTable("pc_skuprice_change_flow").dataInsert(changeFlow);
				flag = true;
			}
		}
		return flag;
	}

	
	
	/**
	 * @param lInfoId
	 *            文本编号
	 * @param sParms
	 *            拼接字符串
	 * @return
	 */
	public String bInfo(long iInfoCode, Object... sParms) {

		return FormatHelper.formatString(TopUp.upInfo(iInfoCode), sParms);
	}
}
