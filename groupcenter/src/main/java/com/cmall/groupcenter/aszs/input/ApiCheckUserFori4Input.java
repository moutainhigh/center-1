package com.cmall.groupcenter.aszs.input;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiCheckUserFori4Input extends RootInput{
	
	@ZapcomApi(value = "应用唯一标识",remark = "应用唯一标识",require=1)
	private String appid = "";
	
	@ZapcomApi(value = "WIFI MAC 地址",remark = "全小写，去掉分隔符",require=1)
	private String mac = "";
	
	@ZapcomApi(value = "用户设备标识",remark = "iOS7.0 以上系统",require=1)
	private String idfa = "";

	@ZapcomApi(value = "用户设备标识",remark = "用户设备标识",require=1)
	private String openudid = "";
	
	@ZapcomApi(value = "用户设备iOS 系统版本",remark = "用户设备iOS 系统版本",require=1)
	private String os = "";

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getIdfa() {
		return idfa;
	}

	public void setIdfa(String idfa) {
		this.idfa = idfa;
	}

	public String getOpenudid() {
		return openudid;
	}

	public void setOpenudid(String openudid) {
		this.openudid = openudid;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}
	
}
