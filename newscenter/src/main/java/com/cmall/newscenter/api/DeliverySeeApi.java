package com.cmall.newscenter.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.DeliverySeeInput;
import com.cmall.newscenter.model.DeliverySeeResult;
import com.cmall.newscenter.util.MemberUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 记录发货信息
 * 
 * @author shiyz date 2016-03-18
 * 
 */
public class DeliverySeeApi extends
    RootApiForManage<DeliverySeeResult, DeliverySeeInput> {

	public DeliverySeeResult Process(DeliverySeeInput inputParam,
			MDataMap mRequestMap) {
		
		DeliverySeeResult result = new DeliverySeeResult();

		if (result.upFlagTrue()) {

			MDataMap mDataMap = new MDataMap();
			
			List<MDataMap> deliveryMap = new ArrayList<MDataMap>();
			
			mDataMap.put("phone_number", inputParam.getAgentCode());
			
			mDataMap.put("wx_number", inputParam.getAgentCode());
			
			mDataMap.put("delivery_name", inputParam.getAgentCode());
			
			deliveryMap = DbUp.upTable("nc_delivery").queryAll("", "", "phone_number=:phone_number or wx_number=:wx_number or delivery_name=:delivery_name", mDataMap);
			
			if(deliveryMap.size()!=0){
				
				for (int i = 0; i < deliveryMap.size(); i++) {
					
					result.setAgent_grade(new MemberUtil().Agent_grade(deliveryMap.get(i).get("delivery_grade")));	
					
					result.setAgent_mobilephone(deliveryMap.get(i).get("phone_number"));
					
					result.setAgent_name(deliveryMap.get(i).get("delivery_name"));
					
					result.setAgent_wechat(deliveryMap.get(i).get("wx_number"));
					
					result.setDelivery_number(Integer.valueOf(deliveryMap.get(i).get("delivery_number")));
					
					result.setExpress_company(deliveryMap.get(i).get("express_company"));
					
					result.setExpress_number(deliveryMap.get(i).get("express_number"));
					
					result.setAddress(new MemberUtil().addressName(deliveryMap.get(i).get("receipt_address")));
				}
				
				}
			}

		return result;
	}

}
