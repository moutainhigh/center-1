package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class UserLoginInput extends UserInput {

	
	@ZapcomApi(value = "验证类型", remark = "非必填字段，默认为空表示使用用户的密码进行登录，当该参数为msg_code时表示使用手机验证码进行登录", require = 0,verify="in=msg_code")
	private String verify_type = "";

	

	public String getVerify_type() {
		return verify_type;
	}

	public void setVerify_type(String verify_type) {
		this.verify_type = verify_type;
	}

	

	

	


}
