package com.cmall.ordercenter.model.api.reconciliation.input;

import com.srnpr.zapcom.topapi.RootInput;

public class ReconciliationPaymentInput extends RootInput {

	private String ids;

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

}
