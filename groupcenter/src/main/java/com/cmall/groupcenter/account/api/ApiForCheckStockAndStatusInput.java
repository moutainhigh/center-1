package com.cmall.groupcenter.account.api;

import java.util.HashMap;
import java.util.Map;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiForCheckStockAndStatusInput extends RootInput{
	
	@ZapcomApi(value = "sku编号和sku数量",remark = "key:skuCode，value:数量" ,demo= "80194416,10",require = 1)
	Map<String, Integer> map = new HashMap<String, Integer>();

	public Map<String, Integer> getMap() {
		return map;
	}

	public void setMap(Map<String, Integer> map) {
		this.map = map;
	}
	
}
