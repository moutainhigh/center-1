package com.cmall.productcenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetAnnounceResult  extends RootResult{
	
	@ZapcomApi(value="店铺公告ID",remark="")
	private String zid = "";
	
	@ZapcomApi(value="店铺公告标题",remark="")
	private String title = "";
	
	@ZapcomApi(value="店铺公告内容",remark="")
	private String content = "";

	public String getZid() {
		return zid;
	}

	public void setZid(String zid) {
		this.zid = zid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
