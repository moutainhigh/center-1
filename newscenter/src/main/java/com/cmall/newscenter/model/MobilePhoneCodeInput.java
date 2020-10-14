package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 用户 - 获取手机验证码输入类
 * @author shiyz
 * date 2014-7-18
 * @version 1.0
 */
public class MobilePhoneCodeInput extends RootInput {

	@ZapcomApi(value = "电话号码",remark = "电话号码",demo = "18612363314",require = 1,verify = "regex=^1[0-9]{10}")

	private String mobile = "";
	
	@ZapcomApi(value = "发送类型",remark = "发送类型",demo = "",require = 1)
	private String send_type = "";

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getSend_type() {
		return send_type;
	}

	public void setSend_type(String send_type) {
		this.send_type = send_type;
	}
	
}
