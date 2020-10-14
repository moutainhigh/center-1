package com.cmall.groupcenter.servicephone.api;

import java.util.Map;

import com.cmall.groupcenter.servicephone.model.CheckServiceTelResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 获取服务电话接口
 * @author lijx
 *
 */

public class apiCheckServiceTel extends RootApiForManage<CheckServiceTelResult, RootInput>{
	
	public CheckServiceTelResult Process(RootInput inputParam, MDataMap mRequestMap) {
		
		CheckServiceTelResult result = new CheckServiceTelResult();

		 Map<String, Object>  mDate = DbUp.upTable("gc_service_tel").dataSqlOne("select * from gc_service_tel", new MDataMap());
		 
		 if(mDate != null){
			 result.setServiceTel(String.valueOf(mDate.get("tel_number")));
		 }
		
		return result;
		
	}

}
