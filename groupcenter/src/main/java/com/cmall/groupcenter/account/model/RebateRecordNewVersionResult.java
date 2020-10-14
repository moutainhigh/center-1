package com.cmall.groupcenter.account.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 新版本返利明细结果(2.1.4版)
 * @author GaoYang
 *
 */
public class RebateRecordNewVersionResult extends RootResultWeb{
	
	@ZapcomApi(value = "预计返利", remark = "预计返利")
	String accountRebateMoney="0.00";
	
	@ZapcomApi(value = "已返利", remark = "已返利")
	String totalReckonMoney="0.00";
	
	@ZapcomApi(value = "返利记录列表", remark = "返利记录列表")
	List<WithdrawRecordNewVersionInfo> rebateRecordList=new ArrayList<WithdrawRecordNewVersionInfo>();

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

	public List<WithdrawRecordNewVersionInfo> getRebateRecordList() {
		return rebateRecordList;
	}

	public void setRebateRecordList(
			List<WithdrawRecordNewVersionInfo> rebateRecordList) {
		this.rebateRecordList = rebateRecordList;
	}

	public PageResults getPageResults() {
		return pageResults;
	}

	public void setPageResults(PageResults pageResults) {
		this.pageResults = pageResults;
	}

	
}
