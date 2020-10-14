package com.cmall.newscenter.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import com.cmall.newscenter.model.ReceiptConfirmationInput;
import com.cmall.newscenter.model.ReceiptConfirmationResult;
import com.cmall.newscenter.util.MemberUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 验证防伪码真伪
 * 
 * @author shiyz date 2014-09-20
 * 
 */
public class ReceiptConfirmationApi extends
		RootApiForToken<ReceiptConfirmationResult, ReceiptConfirmationInput> {

	public ReceiptConfirmationResult Process(
			ReceiptConfirmationInput inputParam, MDataMap mRequestMap) {

		ReceiptConfirmationResult result = new ReceiptConfirmationResult();

		if (result.upFlagTrue()) {
			
			
			List<String> list = new ArrayList<String>();
			List<String> mapList = new ArrayList<String>();
			StringTokenizer st = new StringTokenizer(inputParam.getSecurityCode(), ",");
			while (st.hasMoreTokens()) {
				list.add(st.nextToken());
			}
			for (int i = 0; i < list.size(); i++) {
				
				MDataMap delicerymap = DbUp.upTable("nc_delivery_details").one(
						"dimensional_code",list.get(i));
				
				if(delicerymap!=null&&!delicerymap.isEmpty()){
					
					StringTokenizer st1 = new StringTokenizer(delicerymap.get("delivery_no"));
					while (st1.hasMoreTokens()) {
						mapList.add(st1.nextToken());
					}
					
				}
				
			}
			

			MDataMap map = DbUp.upTable("nc_agency").one("level_number",
					inputParam.getAgent_code());

			Map<String, Object> levelMap = new HashMap<String, Object>();

			if (map != null && !map.isEmpty()) {
				levelMap = DbUp
						.upTable("nc_agency_level")
						.dataSqlOne(
								"select agency_level from nc_agency_level where parent_id=(select agency_level from nc_agency_level where parent_id='')",
								new MDataMap());
			}

			if (levelMap != null && !levelMap.isEmpty()) {
				MDataMap delicerymap = DbUp.upTable("nc_delivery").one(
						"delivery_code",inputParam.getAgent_code(),"delivery_no",mapList.get(0));
				// 判断是否为大区
				if (map.get("agent_level").equals(levelMap.get("agency_level"))) {

					if (delicerymap != null && !delicerymap.isEmpty()) {

						result.setExpress_company(delicerymap
								.get("express_company"));

						result.setExpress_number(delicerymap
								.get("express_number"));

						result.setDelivery_time(delicerymap
								.get("delivery_time"));
					}

				}
				MDataMap nameMap = new MemberUtil().Agent_name(new MemberUtil()
						.Agent_parent(inputParam.getAgent_code()));

				result.setAgent_phone(nameMap.get("agent_mobilephone"));

				result.setAgent_parent(nameMap.get("agent_name"));

				result.setAgent_wchat(nameMap.get("agent_wechat"));

			}

		}

		return result;
	}

}
