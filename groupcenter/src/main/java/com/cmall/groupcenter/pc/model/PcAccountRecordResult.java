package com.cmall.groupcenter.pc.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class PcAccountRecordResult extends RootResultWeb{
	
	@ZapcomApi(value = "提示查询开始日期", remark = "提示查询开始日期")
	String tipsBeginTime="";
	@ZapcomApi(value = "提示查询结束日期", remark = "提示查询结束日期")
	String tipsEndTime="";
	@ZapcomApi(value = "累计入账", remark = "累计入账")
	String totalInComeMoney="0.00";
	@ZapcomApi(value = "累计提现", remark = "累计入账")
	String totalWithdrawMoney="0.00";
	@ZapcomApi(value = "累计扣款", remark = "累计扣款")
	String totalCutPaymentMoney="0.00";
	@ZapcomApi(value = "累计购物", remark = "累计购物")
	String totalPayMoney="0.00";
	
	@ZapcomApi(value = "统计入账笔数", remark = "统计入账笔数")
	String countInComeNum="0";
	@ZapcomApi(value = "统计入账金额", remark = "统计入账金额")
	String countInComeMoney="0.00";
	
	@ZapcomApi(value = "统计提现笔数", remark = "统计提现笔数")
	String countWithdrawNum="0";
	@ZapcomApi(value = "统计提现金额", remark = "统计提现金额")
	String countWithdrawMoney="0.00";
	
	@ZapcomApi(value = "统计扣款笔数", remark = "统计扣款笔数")
	String countCutPaymentNum="0";
	@ZapcomApi(value = "统计扣款金额", remark = "统计扣款金额")
	String countCutPaymentMoney="0.00";
	
	@ZapcomApi(value = "统计购物笔数", remark = "统计购物笔数")
	String conutPayNum="0";
	@ZapcomApi(value = "统计购物金额", remark = "统计购物金额")
	String conutPayMoney="0.00";
	
	@ZapcomApi(value = "扣款记录列表", remark = "扣款记录列表")
	List<PcAccountRecordInfo> accountRecordList=new ArrayList<PcAccountRecordInfo>();

	@ZapcomApi(value = "翻页结果", remark = "翻页结果")
	PageResults pageResults=new PageResults();
	
	public String getTotalInComeMoney() {
		return totalInComeMoney;
	}

	public void setTotalInComeMoney(String totalInComeMoney) {
		this.totalInComeMoney = totalInComeMoney;
	}

	public String getTotalWithdrawMoney() {
		return totalWithdrawMoney;
	}

	public void setTotalWithdrawMoney(String totalWithdrawMoney) {
		this.totalWithdrawMoney = totalWithdrawMoney;
	}

	public String getTotalCutPaymentMoney() {
		return totalCutPaymentMoney;
	}

	public void setTotalCutPaymentMoney(String totalCutPaymentMoney) {
		this.totalCutPaymentMoney = totalCutPaymentMoney;
	}

	public String getTotalPayMoney() {
		return totalPayMoney;
	}

	public void setTotalPayMoney(String totalPayMoney) {
		this.totalPayMoney = totalPayMoney;
	}

	public String getCountInComeNum() {
		return countInComeNum;
	}

	public void setCountInComeNum(String countInComeNum) {
		this.countInComeNum = countInComeNum;
	}

	public String getCountInComeMoney() {
		return countInComeMoney;
	}

	public void setCountInComeMoney(String countInComeMoney) {
		this.countInComeMoney = countInComeMoney;
	}

	public String getCountWithdrawNum() {
		return countWithdrawNum;
	}

	public void setCountWithdrawNum(String countWithdrawNum) {
		this.countWithdrawNum = countWithdrawNum;
	}

	public String getCountWithdrawMoney() {
		return countWithdrawMoney;
	}

	public void setCountWithdrawMoney(String countWithdrawMoney) {
		this.countWithdrawMoney = countWithdrawMoney;
	}

	public String getCountCutPaymentNum() {
		return countCutPaymentNum;
	}

	public void setCountCutPaymentNum(String countCutPaymentNum) {
		this.countCutPaymentNum = countCutPaymentNum;
	}

	public String getCountCutPaymentMoney() {
		return countCutPaymentMoney;
	}

	public void setCountCutPaymentMoney(String countCutPaymentMoney) {
		this.countCutPaymentMoney = countCutPaymentMoney;
	}

	public String getConutPayNum() {
		return conutPayNum;
	}

	public void setConutPayNum(String conutPayNum) {
		this.conutPayNum = conutPayNum;
	}

	public String getConutPayMoney() {
		return conutPayMoney;
	}

	public void setConutPayMoney(String conutPayMoney) {
		this.conutPayMoney = conutPayMoney;
	}

	public List<PcAccountRecordInfo> getAccountRecordList() {
		return accountRecordList;
	}

	public void setAccountRecordList(List<PcAccountRecordInfo> accountRecordList) {
		this.accountRecordList = accountRecordList;
	}

	public PageResults getPageResults() {
		return pageResults;
	}

	public void setPageResults(PageResults pageResults) {
		this.pageResults = pageResults;
	}

	public String getTipsBeginTime() {
		return tipsBeginTime;
	}

	public void setTipsBeginTime(String tipsBeginTime) {
		this.tipsBeginTime = tipsBeginTime;
	}

	public String getTipsEndTime() {
		return tipsEndTime;
	}

	public void setTipsEndTime(String tipsEndTime) {
		this.tipsEndTime = tipsEndTime;
	}
	
}
