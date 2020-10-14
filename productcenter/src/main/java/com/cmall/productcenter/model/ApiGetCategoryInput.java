package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetCategoryInput  extends RootInput{
	
	/**
	 * 分类父ID ：默认值为 44971603
	 */
	@ZapcomApi(value="分类父ID")
	private String pid = "";

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}
}
