package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 设置 - 检查更新输入类
 * @author liqiang
 * date 2014-7-21
 * @version 1.0
 */
public class SettingCheckUpdateResult extends RootResultWeb{
	
	@ZapcomApi(value="是否有更新，是-1，否-0",demo="1,0")
	private String has_new = "";

	@ZapcomApi(value="更新地址",demo="itunes://")
	private String url = "";
	
	@ZapcomApi(value="描述",demo="新增XXX功能")
	private String brief = "";

	@ZapcomApi(value="版本",demo="2.0.0")
	private String version = "";

	public String getHas_new() {
		return has_new;
	}

	public void setHas_new(String has_new) {
		this.has_new = has_new;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
