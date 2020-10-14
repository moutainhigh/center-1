package com.cmall.groupcenter.third.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 查询用户与店铺关系输入
 * @author panwei
 *
 */
public class GroupMemberTraderRelInput extends RootInput{

	@ZapcomApi(value = "手机号",remark = "13511111111,13511111112", require = 1)
	String mobile="";

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	

}
