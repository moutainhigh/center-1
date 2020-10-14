package com.cmall.groupcenter.account.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 微公社推动信息设定的内容
 * @author GaoYang
 *
 */
public class AccountPushSetInfoResult extends RootResultWeb{

	@ZapcomApi(value = "接收推送通知总开关", remark = "接收推送通知总开关(1:开启2: 关闭)", demo = "449747100001")
	private String pushTypeMasterOnoff = "";
	
	@ZapcomApi(value = "推送类型列表", remark = "推送类型列表", demo = "下单成功,返利到账")
	private List<AccountPushTypeInfo> pushTypeInfoList = new ArrayList<AccountPushTypeInfo>();

	public String getPushTypeMasterOnoff() {
		return pushTypeMasterOnoff;
	}

	public void setPushTypeMasterOnoff(String pushTypeMasterOnoff) {
		this.pushTypeMasterOnoff = pushTypeMasterOnoff;
	}

	public List<AccountPushTypeInfo> getPushTypeInfoList() {
		return pushTypeInfoList;
	}

	public void setPushTypeInfoList(List<AccountPushTypeInfo> pushTypeInfoList) {
		this.pushTypeInfoList = pushTypeInfoList;
	}

	
	
}
