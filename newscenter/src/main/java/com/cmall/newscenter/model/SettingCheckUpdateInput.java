package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 设置 - 检查更新输入类
 * @author liqiang
 * date 2014-7-21
 * @version 1.0
 */
public class SettingCheckUpdateInput extends RootInput{
	
	@ZapcomApi(value="平台",demo="android,ios",verify = {"in=android,ios"})
	private String platform="";

	@ZapcomApi(value="版本",demo="1.0.0")
	private String ver="";
	

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

}
