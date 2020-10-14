package com.cmall.groupcenter.wallet.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class WithdrawRecordInput extends RootInput{

	@ZapcomApi(value = "提款单号", remark = "提款单号", demo ="t123123123,t43534234,t234324",require=1)
	private String withdrawCode = "";

	public String getWithdrawCode() {
		return withdrawCode;
	}

	public void setWithdrawCode(String withdrawCode) {
		this.withdrawCode = withdrawCode;
	}
	
}
