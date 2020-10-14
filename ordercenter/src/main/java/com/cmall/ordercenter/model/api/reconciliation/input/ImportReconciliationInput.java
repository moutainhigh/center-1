package com.cmall.ordercenter.model.api.reconciliation.input;

import com.srnpr.zapcom.topapi.RootInput;

public class ImportReconciliationInput extends RootInput {

	private String upload;

	private String reconciliationType;

	private String payType;

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getUpload() {
		return upload;
	}

	public void setUpload(String upload) {
		this.upload = upload;
	}

	public String getReconciliationType() {
		return reconciliationType;
	}

	public void setReconciliationType(String reconciliationType) {
		this.reconciliationType = reconciliationType;
	}

}
