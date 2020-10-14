package com.cmall.newscenter.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.AgentInformation;
import com.cmall.newscenter.model.AgentInformationInput;
import com.cmall.newscenter.model.AgentInformationResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 每级代理商商品流通信息
 * 
 * @author shiyz date 2016-03-21
 * 
 */
public class AgentInformationApi extends
		RootApiForToken<AgentInformationResult, AgentInformationInput> {

	public AgentInformationResult Process(AgentInformationInput inputParam,
			MDataMap mRequestMap) {

		AgentInformationResult result = new AgentInformationResult();
		
		List<AgentInformation> informations = new ArrayList<AgentInformation>();

		if (result.upFlagTrue()) {
			
		List<MDataMap> receMaps =  DbUp.upTable("nc_receiving_scan").queryByWhere("anget_code",inputParam.getAgentCode());	
			
		if(receMaps.size()!=0){
		
			for (int i = 0; i < receMaps.size(); i++) {
				
				AgentInformation information = new AgentInformation();
				
				information.setEntry_time(receMaps.get(i).get("receiving_time"));
				
				information.setReceiving_num(receMaps.get(i).get("receiving_num"));				
				
				information.setReceiving_stats("已收货");
				
				informations.add(information);
				
			}
			
		}
		
		result.setInformations(informations);
		}

		return result;
	}

}
