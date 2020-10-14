package com.cmall.groupcenter.recommend.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 推荐连接跳转下载页
 * @author fq
 *
 */
public class ApiClickLinkToDowndLoadPageInput extends RootInput{
	
	@ZapcomApi(value="推荐关系标识",remark="uqcode")
	private String id = "";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
