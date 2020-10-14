package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 设置 - 推送开关输入类
 * @author liqiang
 * date 2014-7-22
 * @version 1.0
 */
public class SettingSwitchPushInput extends RootInput{
	
	@ZapcomApi(value="设备唯一ID",demo="ABCDEFG")
	private String uuid="";

	@ZapcomApi(value="推送TOKEN",demo="ABCDEFG")
	private String token="";
	
	@ZapcomApi(value="平台",demo="android,ios")
	private String platform="";
	
	@ZapcomApi(value="0-关, 1-开",demo="1")
	private int push_switch;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public int getPush_switch() {
		return push_switch;
	}

	public void setPush_switch(int push_switch) {
		this.push_switch = push_switch;
	}

}
