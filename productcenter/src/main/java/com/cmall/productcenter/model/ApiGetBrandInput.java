package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetBrandInput  extends RootInput{
	
	/**
	 * 品牌父ID ：默认值为  44971602
	 */
	@ZapcomApi(value="品牌父ID")
	private String pid = "";

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}
}
