package com.cmall.groupcenter.account.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class WithdrawRecordResult extends RootResultWeb{
	
	@ZapcomApi(value = "累计已提现到账", remark = "累计已提现到账")
	String arrivalMoney="0.00";
	
	@ZapcomApi(value = "提现到账金额", remark = "提现到账金额")
	String searchArrivalMoney = "0.00";
	
	@ZapcomApi(value = "提现到账笔数", remark = "提现到账笔数")
	String searchArrivalNum = "0";
	
	@ZapcomApi(value = "提现失败金额", remark = "提现失败金额")
	String searchErrorMoney = "0.00";
	
	@ZapcomApi(value = "提失败笔数", remark = "提现失败笔数")
	String searchErrorNum = "0";
	
	@ZapcomApi(value = "正在提现金额", remark = "正在提现金额")
	String searchReadyMoney = "0.00";
	
	@ZapcomApi(value = "正在提现笔数", remark = "正在提现笔数")
	String searchReadyNum = "0";
	
	@ZapcomApi(value = "账户余额", remark = "账户余额")
	String accountWithdrawMoney="0.00";
	
	@ZapcomApi(value = "提现金额", remark = "提现金额")
	String withdrawedMoney="0.00";
	
	@ZapcomApi(value = "提现记录列表", remark = "提现记录列表")
	List<WithdrawRecordInfo> withdrawRecordList=new ArrayList<WithdrawRecordInfo>();

	@ZapcomApi(value = "翻页结果", remark = "翻页结果")
	PageResults pageResults=new PageResults();
	
	public PageResults getPageResults() {
		return pageResults;
	}

	public void setPageResults(PageResults pageResults) {
		this.pageResults = pageResults;
	}

	public String getAccountWithdrawMoney() {
		return accountWithdrawMoney;
	}

	public void setAccountWithdrawMoney(String accountWithdrawMoney) {
		this.accountWithdrawMoney = accountWithdrawMoney;
	}

	public String getWithdrawedMoney() {
		return withdrawedMoney;
	}

	public void setWithdrawedMoney(String withdrawedMoney) {
		this.withdrawedMoney = withdrawedMoney;
	}

	public List<WithdrawRecordInfo> getWithdrawRecordList() {
		return withdrawRecordList;
	}

	public void setWithdrawRecordList(List<WithdrawRecordInfo> withdrawRecordList) {
		this.withdrawRecordList = withdrawRecordList;
	}

	public String getArrivalMoney() {
		return arrivalMoney;
	}

	public void setArrivalMoney(String arrivalMoney) {
		this.arrivalMoney = arrivalMoney;
	}

	public String getSearchArrivalMoney() {
		return searchArrivalMoney;
	}

	public void setSearchArrivalMoney(String searchArrivalMoney) {
		this.searchArrivalMoney = searchArrivalMoney;
	}

	public String getSearchArrivalNum() {
		return searchArrivalNum;
	}

	public void setSearchArrivalNum(String searchArrivalNum) {
		this.searchArrivalNum = searchArrivalNum;
	}

	public String getSearchErrorMoney() {
		return searchErrorMoney;
	}

	public void setSearchErrorMoney(String searchErrorMoney) {
		this.searchErrorMoney = searchErrorMoney;
	}

	public String getSearchErrorNum() {
		return searchErrorNum;
	}

	public void setSearchErrorNum(String searchErrorNum) {
		this.searchErrorNum = searchErrorNum;
	}

	public String getSearchReadyMoney() {
		return searchReadyMoney;
	}

	public void setSearchReadyMoney(String searchReadyMoney) {
		this.searchReadyMoney = searchReadyMoney;
	}

	public String getSearchReadyNum() {
		return searchReadyNum;
	}

	public void setSearchReadyNum(String searchReadyNum) {
		this.searchReadyNum = searchReadyNum;
	}
	
	
}
