package com.cmall.groupcenter.pc.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class PcCutPaymentRecordResult  extends RootResultWeb{

	@ZapcomApi(value = "提示查询开始日期", remark = "提示查询开始日期")
	String tipsBeginTime="";
	
	@ZapcomApi(value = "提示查询结束日期", remark = "提示查询结束日期")
	String tipsEndTime="";
	
	@ZapcomApi(value = "当前账户余额", remark = "当前账户余额")
	String accountWithdrawMoney="0.00";
	
	@ZapcomApi(value = "累计扣款", remark = "累计扣款")
	String totalCutPaymentMoney="0.00";
	
	@ZapcomApi(value = "统计退货扣款笔数", remark = "统计退货扣款笔数")
	String countReturnGoodsCutNum="0";
	
	@ZapcomApi(value = "统计退货扣款金额", remark = "统计退货扣款金额")
	String countReturnGoodsCutMoney="0.00";
	
	@ZapcomApi(value = "统计平台扣减笔数", remark = "统计平台扣减笔数")
	String countPlatformCutNum="0";
	
	@ZapcomApi(value = "统计平台扣减金额", remark = "统计平台扣减金额")
	String countPlatformCutMoney="0.00";
	
	@ZapcomApi(value = "扣款记录列表", remark = "扣款记录列表")
	List<PcCutPaymentRecordInfo> cutPaymentRecordList=new ArrayList<PcCutPaymentRecordInfo>();

	@ZapcomApi(value = "翻页结果", remark = "翻页结果")
	PageResults pageResults=new PageResults();
	
	public String getAccountWithdrawMoney() {
		return accountWithdrawMoney;
	}

	public void setAccountWithdrawMoney(String accountWithdrawMoney) {
		this.accountWithdrawMoney = accountWithdrawMoney;
	}

	public String getTotalCutPaymentMoney() {
		return totalCutPaymentMoney;
	}

	public void setTotalCutPaymentMoney(String totalCutPaymentMoney) {
		this.totalCutPaymentMoney = totalCutPaymentMoney;
	}

	public List<PcCutPaymentRecordInfo> getCutPaymentRecordList() {
		return cutPaymentRecordList;
	}

	public String getCountReturnGoodsCutNum() {
		return countReturnGoodsCutNum;
	}

	public void setCountReturnGoodsCutNum(String countReturnGoodsCutNum) {
		this.countReturnGoodsCutNum = countReturnGoodsCutNum;
	}

	public String getCountReturnGoodsCutMoney() {
		return countReturnGoodsCutMoney;
	}

	public void setCountReturnGoodsCutMoney(String countReturnGoodsCutMoney) {
		this.countReturnGoodsCutMoney = countReturnGoodsCutMoney;
	}

	public String getCountPlatformCutNum() {
		return countPlatformCutNum;
	}

	public void setCountPlatformCutNum(String countPlatformCutNum) {
		this.countPlatformCutNum = countPlatformCutNum;
	}

	public String getCountPlatformCutMoney() {
		return countPlatformCutMoney;
	}

	public void setCountPlatformCutMoney(String countPlatformCutMoney) {
		this.countPlatformCutMoney = countPlatformCutMoney;
	}

	public void setCutPaymentRecordList(
			List<PcCutPaymentRecordInfo> cutPaymentRecordList) {
		this.cutPaymentRecordList = cutPaymentRecordList;
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
