package com.cmall.groupcenter.pc.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class PcRebateRecordResult extends RootResultWeb{

	@ZapcomApi(value = "提示查询开始日期", remark = "提示查询开始日期")
	String tipsBeginTime="";
	
	@ZapcomApi(value = "提示查询结束日期", remark = "提示查询结束日期")
	String tipsEndTime="";
	
	@ZapcomApi(value = "累计已返利", remark = "累计已返利")
	String totalRebateMoney="0.00";
	
	@ZapcomApi(value = "累计预计返利", remark = "累计预计返利")
	String totalExpectMoney="0.00";
	
	@ZapcomApi(value = "统计已返利笔数", remark = "统计已返利笔数")
	String countRebateNum="0";
	
	@ZapcomApi(value = "统计已返利金额", remark = "统计已返利金额")
	String countRebateMoney="0.00";
	
	@ZapcomApi(value = "统计预计返利笔数", remark = "统计预计返利笔数")
	String countExpectRebateNum="0";
	
	@ZapcomApi(value = "统计预计返利金额", remark = "统计预计返利金额")
	String countExpectRebateMoney="0.00";
	
	@ZapcomApi(value = "统计取消返利笔数", remark = "统计取消返利笔数")
	String countCancelRebateNum="0";
	
	@ZapcomApi(value = "统计取消返利金额", remark = "统计取消返利金额")
	String countCancelRebateMoney="0.00";
	
	@ZapcomApi(value = "返利记录列表", remark = "返利记录列表")
	List<PcRebateRecordInfo> rebateRecordList=new ArrayList<PcRebateRecordInfo>();

	@ZapcomApi(value = "翻页结果", remark = "翻页结果")
	PageResults pageResults=new PageResults();

	public String getTotalRebateMoney() {
		return totalRebateMoney;
	}

	public void setTotalRebateMoney(String totalRebateMoney) {
		this.totalRebateMoney = totalRebateMoney;
	}

	public String getTotalExpectMoney() {
		return totalExpectMoney;
	}

	public void setTotalExpectMoney(String totalExpectMoney) {
		this.totalExpectMoney = totalExpectMoney;
	}

	public String getCountRebateNum() {
		return countRebateNum;
	}

	public void setCountRebateNum(String countRebateNum) {
		this.countRebateNum = countRebateNum;
	}

	public String getCountRebateMoney() {
		return countRebateMoney;
	}

	public void setCountRebateMoney(String countRebateMoney) {
		this.countRebateMoney = countRebateMoney;
	}

	public String getCountExpectRebateNum() {
		return countExpectRebateNum;
	}

	public void setCountExpectRebateNum(String countExpectRebateNum) {
		this.countExpectRebateNum = countExpectRebateNum;
	}

	public String getCountExpectRebateMoney() {
		return countExpectRebateMoney;
	}

	public void setCountExpectRebateMoney(String countExpectRebateMoney) {
		this.countExpectRebateMoney = countExpectRebateMoney;
	}

	public String getCountCancelRebateNum() {
		return countCancelRebateNum;
	}

	public void setCountCancelRebateNum(String countCancelRebateNum) {
		this.countCancelRebateNum = countCancelRebateNum;
	}

	public String getCountCancelRebateMoney() {
		return countCancelRebateMoney;
	}

	public void setCountCancelRebateMoney(String countCancelRebateMoney) {
		this.countCancelRebateMoney = countCancelRebateMoney;
	}

	public List<PcRebateRecordInfo> getRebateRecordList() {
		return rebateRecordList;
	}

	public void setRebateRecordList(List<PcRebateRecordInfo> rebateRecordList) {
		this.rebateRecordList = rebateRecordList;
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
