package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetCategoryRateInput  extends RootInput{
	
	/**
	 * 分类ID 
	 */
	@ZapcomApi(value="分类ID")
	private String cid = "";

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	
}
