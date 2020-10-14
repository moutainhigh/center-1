package com.cmall.membercenter.model;

import com.cmall.dborm.txmodel.membercenter.McMemberInfo;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.webmodel.MWebResult;

public class MReginsterResult extends RootResultWeb {

	/**
	 * 用户信息
	 */
	private McMemberInfo memberInfo = new McMemberInfo();

	public McMemberInfo getMemberInfo() {
		return memberInfo;
	}

	public void setMemberInfo(McMemberInfo memberInfo) {
		this.memberInfo = memberInfo;
	}

}
