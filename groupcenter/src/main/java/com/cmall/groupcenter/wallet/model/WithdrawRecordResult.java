package com.cmall.groupcenter.wallet.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class WithdrawRecordResult extends RootResultWeb {
	@ZapcomApi(value = "提现记录", remark = "提现记录")
	List<WithdrawRecord> withdrawRecordList = new ArrayList<WithdrawRecord>();

	public List<WithdrawRecord> getWithdrawRecordList() {
		return withdrawRecordList;
	}

	public void setWithdrawRecordList(List<WithdrawRecord> withdrawRecordList) {
		this.withdrawRecordList = withdrawRecordList;
	}

	

}
