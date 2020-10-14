package com.cmall.groupcenter.account.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 
 * 新版本账户明细结果(2.1.4版)
 * @author GaoYang
 *
 */
public class AccountRecordResult extends RootResultWeb{
	@ZapcomApi(value = "可提现返利", remark = "可提现返利")
	String accountWithdrawRebateMoney="0.00";
	
	@ZapcomApi(value = "累计返利", remark = "累计返利")
	String totalRebateMoney="0.00";
	
	@ZapcomApi(value = "账户明细列表", remark = "账户明细列表")
	List<AccountRecordInfo> rebateRecordList=new ArrayList<AccountRecordInfo>();

	@ZapcomApi(value = "翻页结果", remark = "翻页结果")
	PageResults pageResults=new PageResults();

	public String getAccountWithdrawRebateMoney() {
		return accountWithdrawRebateMoney;
	}

	public void setAccountWithdrawRebateMoney(String accountWithdrawRebateMoney) {
		this.accountWithdrawRebateMoney = accountWithdrawRebateMoney;
	}

	public String getTotalRebateMoney() {
		return totalRebateMoney;
	}

	public void setTotalRebateMoney(String totalRebateMoney) {
		this.totalRebateMoney = totalRebateMoney;
	}

	public List<AccountRecordInfo> getRebateRecordList() {
		return rebateRecordList;
	}

	public void setRebateRecordList(List<AccountRecordInfo> rebateRecordList) {
		this.rebateRecordList = rebateRecordList;
	}

	public PageResults getPageResults() {
		return pageResults;
	}

	public void setPageResults(PageResults pageResults) {
		this.pageResults = pageResults;
	}
	
}
