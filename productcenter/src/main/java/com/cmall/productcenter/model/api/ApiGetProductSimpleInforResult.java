package com.cmall.productcenter.model.api;

import java.util.HashMap;
import java.util.Map;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetProductSimpleInforResult extends RootResult {
	
	@ZapcomApi(value="商品信息实体")
	private Map<String, Object> productMap = new HashMap<String, Object>();

	public Map<String, Object> getProductMap() {
		return productMap;
	}

	public void setProductMap(Map<String, Object> productMap) {
		this.productMap = productMap;
	}

}
