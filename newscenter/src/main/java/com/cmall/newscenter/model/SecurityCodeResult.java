package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 活动 -报名列表输入类
 * @author yangrong
 * date 2014-8-21
 * @version 1.0
 */
public class SecurityCodeResult extends RootResultWeb{

	@ZapcomApi(value="批次内序号")
	private String securityBatch = "";
	@ZapcomApi(value="防伪码")
	private List<SecurityCode> securityCodeList = new  ArrayList<SecurityCode>();


	public List<SecurityCode> getSecurityCodeList() {
		return securityCodeList;
	}

	public void setSecurityCodeList(List<SecurityCode> securityCodeList) {
		this.securityCodeList = securityCodeList;
	}

	public String getSecurityBatch() {
		return securityBatch;
	}

	public void setSecurityBatch(String securityBatch) {
		this.securityBatch = securityBatch;
	}
	
}
