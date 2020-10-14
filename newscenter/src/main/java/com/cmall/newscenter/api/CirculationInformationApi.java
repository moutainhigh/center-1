package com.cmall.newscenter.api;

import java.util.ArrayList;
import java.util.List;
import com.cmall.newscenter.model.CirculationInformationInput;
import com.cmall.newscenter.model.CirculationiInformation;
import com.cmall.newscenter.model.CirculationiInformationResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 商品流通信息
 * 
 * @author shiyz date 2016-03-19
 * 
 */
public class CirculationInformationApi extends
   RootApiForToken<CirculationiInformationResult, CirculationInformationInput> {

	public CirculationiInformationResult Process(CirculationInformationInput inputParam,
			MDataMap mRequestMap) {

		CirculationiInformationResult result = new CirculationiInformationResult();
		
		List<CirculationiInformation> informations = new ArrayList<CirculationiInformation>();

		if (result.upFlagTrue()) {
			
		List<MDataMap> receMaps =  DbUp.upTable("nc_delivery_receipt").queryByWhere("receipt_qrcode",inputParam.getSecurityCode());	
			
		if(receMaps.size()!=0){
		
			for (int i = 0; i < receMaps.size(); i++) {
				
				CirculationiInformation information = new CirculationiInformation();
				
				MDataMap map = DbUp.upTable("nc_agency").one("level_number",receMaps.get(i).get("receipt_code"));
				
				if(map!=null&&!map.isEmpty()){
					
					information.setSuperior_agent(map.get("agent_name"));	
					
					information.setAgent_level(new com.cmall.newscenter.util.MemberUtil().Agent_grade(map.get("agent_level")));
				}
				
				information.setEntry_time(receMaps.get(i).get("scan_time"));
				
				informations.add(information);
				
			}
			result.setInformations(informations);
		}
		
		}

		return result;
	}

}
