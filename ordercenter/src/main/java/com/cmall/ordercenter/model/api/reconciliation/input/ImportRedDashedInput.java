package com.cmall.ordercenter.model.api.reconciliation.input;

import com.srnpr.zapcom.topapi.RootInput;

public class ImportRedDashedInput extends RootInput {

	private String reconciliationType;

	private String upload;

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
