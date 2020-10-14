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
 * 普通会员
 * @author ligj
 *
 */
public class ActiveForVipGeneral implements IActiveForProduct {
	
	private final String VIP_TYPE_GENERAL = "4497469400050002";				//一般会员
	private final String VIP_ACTIVITY_TYPE = "AT140820100003";			//会员活动
	public BaseActive doProcess(ActiveType activeType,
			ProductSkuInfo pcSkuInfo, int skuNum,
			String appCode,MDataMap paramsExt, RootResultWeb rootResult) {

		String buyerCode=paramsExt.get(ActiveConst.ACTIVE_PARAMS_BUYERCODE);
		
      	//不是会员日
  		if (!new ConditionCheck().checkIsVipGeneralDay()) {
  			return null;
  		}
  		if (StringUtils.isEmpty(buyerCode)) {
			return null;
		}
		MDataMap userInfo = DbUp.upTable("mc_extend_info_homepool").one("member_code",buyerCode,"vip_type",VIP_TYPE_GENERAL);
	  	if (userInfo == null || userInfo.isEmpty()) {
			return null;
	  	}
  		
	  	
	  	String sWhere = " activity_type_code = '"+VIP_ACTIVITY_TYPE+"' and start_time <= now() and end_time >= now() ";
		MDataMap mDataMap = DbUp.upTable("gc_activity_info").oneWhere("","",sWhere);
		if (null == mDataMap || mDataMap.isEmpty()) {
			return null;
		}
		
	  	MDataMap discountMap = DbUp.upTable("mc_member_level_homepool").oneWhere("discount,outer_activity_code","","","level_code",userInfo.get("vip_level"));
		Double discount = Double.valueOf(discountMap.get("discount"));
		BigDecimal endPrice = pcSkuInfo.getSellPrice().multiply(new BigDecimal(discount)).setScale(2, BigDecimal.ROUND_HALF_UP);
		
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
		
		baseActive.setOuter_activity_code((String)discountMap.get("outer_activity_code"));
		return baseActive;
	}

	
}
