package com.cmall.newscenter.api;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import com.cmall.newscenter.model.DeliveryManagementInput;
import com.cmall.newscenter.model.DeliveryManagementResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 记录发货信息
 * 
 * @author shiyz date 2016-03-18
 * 
 */
public class DeliveryManagementApi extends
		RootApiForToken<DeliveryManagementResult, DeliveryManagementInput> {

	public DeliveryManagementResult Process(DeliveryManagementInput inputParam,
			MDataMap mRequestMap) {

		DeliveryManagementResult result = new DeliveryManagementResult();

		if (result.upFlagTrue()) {

			String code = inputParam.getSecurityCode();

			String delivery_no = UUID.randomUUID().toString();

			MDataMap mDataMap = new MDataMap();
			
			MDataMap addresMap = new MDataMap();
			
			List<String> list = new ArrayList<String>();
			StringTokenizer st = new StringTokenizer(code, ",");
			while (st.hasMoreTokens()) {
				list.add(st.nextToken());
			}

			
			//判断是否重复发货
			if (list.size() != 0) {
				for (int j = 0; j < list.size(); j++) {
					
					MDataMap nameMap = new MDataMap();
					
					nameMap.put("dimensional_code", list.get(j));
					
					List<MDataMap> listName = DbUp.upTable("nc_delivery_details").queryAll("", "", " dimensional_code=:dimensional_code", nameMap);
					if(listName.size()!=0){
						
						result.setResultCode(934205169);
						result.setResultMessage(bInfo(934205169));
						return result;
					}
					
				}
			
				}
			
			MDataMap angetmMap =  DbUp.upTable("nc_agency").one("level_number",inputParam.getAngent_code());

			if(angetmMap != null && !angetmMap.isEmpty()){
				
			addresMap = DbUp.upTable("nc_address").one("address_code",angetmMap.get("member_code"),"address_default","1");
				
				if(addresMap != null && !addresMap.isEmpty()){
					
					mDataMap.put("receipt_address", addresMap.get("address_id"));
				}else {
					
					result.setResultCode(934205155);
					result.setResultMessage(bInfo(934205155));
					return result;
				}	
			/* 系统当前时间 */
			String create_time = com.cmall.newscenter.util.DateUtil
					.getNowTime();

			mDataMap.put("phone_number", angetmMap.get("agent_mobilephone"));

			mDataMap.put("delivery_name", angetmMap.get("agent_name"));

			mDataMap.put("delivery_grade", angetmMap.get("agent_level"));

			mDataMap.put("wx_number",angetmMap.get("agent_wechat"));

			mDataMap.put("delivery_number",
					String.valueOf(inputParam.getDelivery_number()));

			mDataMap.put("express_company", inputParam.getExpress_company());

			mDataMap.put("express_number", inputParam.getExpress_number());

			mDataMap.put("delivery_time", create_time);

			mDataMap.put("delivery_no", delivery_no);
			
		    mDataMap.put("category_code", inputParam.getCategory_code());
		    
		    mDataMap.put("delivery_code", inputParam.getAngent_code());

			DbUp.upTable("nc_delivery").dataInsert(mDataMap);

			if (list.size() != 0) {
				for (int i = 0; i < list.size(); i++) {

					MDataMap codeMap = new MDataMap();

					codeMap.put("dimensional_code", list.get(i));

					codeMap.put("delivery_no", delivery_no);

					DbUp.upTable("nc_delivery_details").dataInsert(codeMap);

				}

			}
			}
		}

		return result;
	}

}
