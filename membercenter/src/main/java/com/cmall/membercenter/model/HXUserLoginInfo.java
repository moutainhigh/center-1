package com.cmall.membercenter.model;

import java.util.HashMap;
import java.util.Map;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class HXUserLoginInfo {
	private String hxUserName = "";
	private String hxPassWord = "";
	private String hxWorkerId = "";
	private String hxStatus = "0";
	private HXUserLoginInfoExtendInfo extendInfo = new HXUserLoginInfoExtendInfo();
	
	public String getHxUserName() {
		return hxUserName;
	}
	public void setHxUserName(String hxUserName) {
		this.hxUserName = hxUserName;
	}
	public String getHxPassWord() {
		return hxPassWord;
	}
	public void setHxPassWord(String hxPassWord) {
		this.hxPassWord = hxPassWord;
	}
	public String getHxWorkerId() {
		return hxWorkerId;
	}
	public void setHxWorkerId(String hxWorkerId) {
		this.hxWorkerId = hxWorkerId;
	}
	public String getHxStatus() {
		return hxStatus;
	}
	public void setHxStatus(String hxStatus) {
		this.hxStatus = hxStatus;
	}
	public HXUserLoginInfoExtendInfo getExtendInfo() {
		return extendInfo;
	}
	public void setExtendInfo(HXUserLoginInfoExtendInfo extendInfo) {
		this.extendInfo = extendInfo;
	}
}