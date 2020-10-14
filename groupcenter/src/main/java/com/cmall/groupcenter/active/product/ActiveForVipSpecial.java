package com.cmall.groupcenter.active.product;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.active.ActiveConst;
import com.cmall.groupcenter.active.ActiveType;
import com.cmall.groupcenter.active.BaseActive;
import com.cmall.groupcenter.active.IActiveForProduct;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootResultWeb;


/**
 * 特定渠道特定价
 * 
 * @author ligj
 * 
 */
public class ActiveForVipSpecial implements IActiveForProduct {

	private final String VIP_ACTIVITY_TYPE = "AT140820100004";					//特定渠道特定价格
	public BaseActive doProcess(ActiveType activeType,
			ProductSkuInfo pcSkuInfo, int skuNum, 
			String appCode,MDataMap paramsExt,  RootResultWeb rootResult) {
		
		String buyerCode = paramsExt.get(ActiveConst.ACTIVE_PARAMS_BUYERCODE);
		if (StringUtils.isEmpty(buyerCode)) {
			return null;
		}
		//不是内购日
		if (!ConditionCheck.checkIsVipSpecialDay()) {
			return null;
		}
		//不是内购员工
		if(!ConditionCheck.checkIsVipSpecial(buyerCode)){
			return null;
		}
		String sWhere = " activity_type_code = '"+VIP_ACTIVITY_TYPE+"' and start_time <= now() and end_time >= now() ";
		MDataMap mDataMap = DbUp.upTable("gc_activity_info").oneWhere("","",sWhere);
		if (null == mDataMap || mDataMap.isEmpty()) {
			return null;
		}
//		String addressCode = paramsExt.get(ActiveConst.ACTIVE_PARAMS_BUYERADDRESS);
//		NcStaffAddressService nf = new NcStaffAddressService();
//		AddressInformation nstaff = nf.nvStaffAddressValue(addressCode);//根据收货地址获取运费和包装费
//		String additionalPrice = "";
//		if(nstaff != null){
//			additionalPrice = nstaff.getPrice();// 运费和包装费
//		}
//		if(StringUtils.isEmpty(additionalPrice)){
//			return null;//如果运费和包装费为空，则不参加内购
//		}
		String productCode = pcSkuInfo.getProductCode();
		String skuCode = pcSkuInfo.getSkuCode();
		MDataMap pdata = DbUp.upTable("pc_productinfo").oneWhere("cost_price,validate_flag",null, null, "product_code", productCode);//查询商品的成本价
		MDataMap sdata = DbUp.upTable("pc_skuinfo").oneWhere("sell_price",null, null, "sku_code", skuCode);//查询商品的成本价
		String cPrice = pdata.get("cost_price");// 商品成本价
		String sPrice = sdata.get("sell_price");// sku售价
		String validate_flag = pdata.get("validate_flag");
		//家有汇虚拟商品部参加内购，参考任务（TASK #555 内购价大于售价时以售价为准）
		if(StringUtils.isEmpty(cPrice) || "Y".equalsIgnoreCase(validate_flag)){
			return null;
		}
		BigDecimal costPrice = new BigDecimal(cPrice);// 商品成本价
		BigDecimal minSellPrice = new BigDecimal(sPrice);
		BigDecimal endPrice = null;
//		BigDecimal addPrice = new BigDecimal(additionalPrice);
//		endPrice = costPrice.add(addPrice).setScale(0,BigDecimal.ROUND_UP);//内部价(北京内购价格=商品含税成本价+15元,其他各地内购价=商品含税成本价+7元二次包装箱费+5元运费)
		/**
		 * 内购价=成本价+23，只对第一位小数四舍五入
		 */
		endPrice = costPrice.add(new BigDecimal(23)).setScale(1, BigDecimal.ROUND_DOWN).setScale(0, BigDecimal.ROUND_HALF_UP);
		if (endPrice.compareTo(minSellPrice) > 0 && minSellPrice.compareTo(BigDecimal.ZERO)>0) {
			endPrice = minSellPrice;
		}
		BaseActive baseActive = new BaseActive();
		baseActive.setActivity_code((String)mDataMap.get("activity_code"));
		baseActive.setActivity_type_code((String)mDataMap.get("activity_type_code"));
		baseActive.setActivity_title((String)mDataMap.get("activity_title"));
		baseActive.setStart_time((String)mDataMap.get("start_time"));
		baseActive.setEnd_time((String)mDataMap.get("end_time"));
		baseActive.setPri_sort((String)mDataMap.get("pri_sort"));
		baseActive.setRemark((String)mDataMap.get("remark"));
		baseActive.setApp_code(appCode);
		baseActive.setActivePrice(endPrice);
		return baseActive;
	}
}
