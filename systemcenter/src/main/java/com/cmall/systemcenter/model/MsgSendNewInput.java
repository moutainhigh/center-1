package com.cmall.systemcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class MsgSendNewInput extends RootInput {

	@ZapcomApi(value = "手机号码", require = 1, remark = "手机号码", verify = "base=mobile")
	private String mobile = "";
	@ZapcomApi(value = "短信内容", require = 1, remark = "")
	private String content = "";
	
	@ZapcomApi(value = "发送渠道", require = 0, remark = "4497467200020002:营销短信渠道,4497467200020003：嘉玲国际短信渠道,4497467200020004:家有汇渠道发送,4497467200020005:微公社渠道,4497467200020006:惠家有渠道,默认为4497467200020001")
	private String send_source = "4497467200020001" ;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSend_source() {
		return send_source;
	}

	public void setSend_source(String send_source) {
		this.send_source = send_source;
	}
	
}
