package com.cmall.groupcenter.wallet.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.report.model.ReportReasonResultList;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class WalletAccountCheckResult extends RootResultWeb{
	
	@ZapcomApi(value="对账列表",remark="对账列表")
	private List<WalletAccountCheckResultList> list = new ArrayList<WalletAccountCheckResultList>();

	public List<WalletAccountCheckResultList> getList() {
		return list;
	}

	public void setList(List<WalletAccountCheckResultList> list) {
		this.list = list;
	}
	
	

}
