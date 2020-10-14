package com.cmall.groupcenter.sync.orderstatus.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 微公社对接系统通过该接口同步订单最新状态 返回结果
 * @author chenxk
 *
 */
public class SyncOrderStatusResult extends RootResultWeb{

	@ZapcomApi(value="状态流水号",demo="cafceeb9062c4276852a026622849bff")
	private String statusSerialNum = "";

	public String getStatusSerialNum() {
		return statusSerialNum;
	}

	public void setStatusSerialNum(String statusSerialNum) {
		this.statusSerialNum = statusSerialNum;
	}
	
}
