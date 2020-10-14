package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;


public class CreateRelationPrivateInput extends RootInput {

	@ZapcomApi(value = "手机号", demo = "13512345678", require = 1, verify = "base=mobile")
	String loginName="";
	
	@ZapcomApi(value = "手机号", demo = "13512345678", require = 1, verify = "base=mobile")
	String parentLoginName="";
	
	@ZapcomApi(value = "创建时间", demo = "2014-08-25 09:57:02", require = 0, verify = "base=datetime")
	String createTime="";
	
	@ZapcomApi(value = "验证码",  require = 1)
	String verify_code= "";

	
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getParentLoginName() {
		return parentLoginName;
	}

	public void setParentLoginName(String parentLoginName) {
		this.parentLoginName = parentLoginName;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getVerify_code() {
		return verify_code;
	}

	public void setVerify_code(String verify_code) {
		this.verify_code = verify_code;
	}
	

	
}
