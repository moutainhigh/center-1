package com.cmall.groupcenter.third.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GroupMemberTraderRelDetail{

	@ZapcomApi(value = "手机号",remark="用户账号",demo = "15300000000", require = 1)
	String mobile="";
	
	@ZapcomApi(value = "关系",remark="关系",demo = "1", require = 1)
	int relation=0;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public int getRelation() {
		return relation;
	}

	public void setRelation(int relation) {
		this.relation = relation;
	}

	
	
}
