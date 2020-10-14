package com.cmall.usercenter.model.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ApiGetCityOfTemplateResult extends RootResultWeb {
	@ZapcomApi(value="区域信息集合",remark="区域信息集合")
	
	private List<Map<String,Object>> tempMap = new ArrayList<Map<String,Object>>();

	public List<Map<String, Object>> getTempMap() {
		return tempMap;
	}

	public void setTempMap(List<Map<String, Object>> tempMap) {
		this.tempMap = tempMap;
	}
	
}
