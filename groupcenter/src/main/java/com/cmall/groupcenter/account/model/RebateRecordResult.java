package com.cmall.groupcenter.account.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class RebateRecordResult extends RootResultWeb{

	@ZapcomApi(value = "预期返利", remark = "预期返利")
	String accountRebateMoney="0.00";
	
	@ZapcomApi(value = "累计返利", remark = "累计返利")
	String totalReckonMoney="0.00";
	
	@ZapcomApi(value = "返利记录列表", remark = "返利记录列表")
	List<WithdrawRecordInfo> rebateRecordList=new ArrayList<WithdrawRecordInfo>();

	@ZapcomApi(value = "翻页结果", remark = "翻页结果")
	PageResults pageResults=new PageResults();

	public String getAccountRebateMoney() {
		return accountRebateMoney;
	}

	public void setAccountRebateMoney(String accountRebateMoney) {
		this.accountRebateMoney = accountRebateMoney;
	}

	public String getTotalReckonMoney() {
		return totalReckonMoney;
	}

	public void setTotalReckonMoney(String totalReckonMoney) {
		this.totalReckonMoney = totalReckonMoney;
	}

	public List<WithdrawRecordInfo> getRebateRecordList() {
		return rebateRecordList;
	}

	public void setRebateRecordList(List<WithdrawRecordInfo> rebateRecordList) {
		this.rebateRecordList = rebateRecordList;
	}

	public PageResults getPageResults() {
		return pageResults;
	}

	public void setPageResults(PageResults pageResults) {
		this.pageResults = pageResults;
	}
}
