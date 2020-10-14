package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.topapi.RootInput;

public class ApiPaymentFlowKjInput extends RootInput {

	private String payCode;
	private String flag;
	private String comment = "";

	public final String getComment() {
		return comment;
	}

	public final void setComment(String comment) {
		this.comment = comment;
	}

	public String getPayCode() {
		return payCode;
	}

	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}
