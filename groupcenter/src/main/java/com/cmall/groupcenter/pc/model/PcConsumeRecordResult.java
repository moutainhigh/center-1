package com.cmall.groupcenter.pc.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class PcConsumeRecordResult  extends RootResultWeb{

	@ZapcomApi(value = "提示查询开始日期", remark = "提示查询开始日期")
	String tipsBeginTime="";
	
	@ZapcomApi(value = "提示查询结束日期", remark = "提示查询结束日期")
	String tipsEndTime="";
	
	@ZapcomApi(value = "累计消费金额", remark = "累计消费金额")
	String totalConsumeMoney="0.00";
	
	@ZapcomApi(value = "本月消费金额", remark = "本月消费金额")
	String currentMonthConsumeMoney="0.00";
	
	@ZapcomApi(value = "本月活跃好友数", remark = "本月活跃好友数")
	String currentMonthActiveNum="0";
	
	@ZapcomApi(value = "本月升级还需消费", remark = "本月升级还需消费")
	String nextLevelGapConsume="0.00";
	
	@ZapcomApi(value = "汇总统计-消费总计", remark = "汇总统计-消费总计")
	String countConsumeMoney="0.00";
	
	@ZapcomApi(value = "消费列表", remark = "消费列表")
	List<PcConsumeRecordInfo> consumeRecordList=new ArrayList<PcConsumeRecordInfo>();
	
	@ZapcomApi(value = "翻页结果", remark = "翻页结果")
	PageResults pageResults=new PageResults();
	
	public String getTotalConsumeMoney() {
		return totalConsumeMoney;
	}

	public void setTotalConsumeMoney(String totalConsumeMoney) {
		this.totalConsumeMoney = totalConsumeMoney;
	}

	public String getCurrentMonthConsumeMoney() {
		return currentMonthConsumeMoney;
	}

	public void setCurrentMonthConsumeMoney(String currentMonthConsumeMoney) {
		this.currentMonthConsumeMoney = currentMonthConsumeMoney;
	}

	public String getCurrentMonthActiveNum() {
		return currentMonthActiveNum;
	}

	public void setCurrentMonthActiveNum(String currentMonthActiveNum) {
		this.currentMonthActiveNum = currentMonthActiveNum;
	}

	public String getNextLevelGapConsume() {
		return nextLevelGapConsume;
	}

	public void setNextLevelGapConsume(String nextLevelGapConsume) {
		this.nextLevelGapConsume = nextLevelGapConsume;
	}

	public String getCountConsumeMoney() {
		return countConsumeMoney;
	}

	public void setCountConsumeMoney(String countConsumeMoney) {
		this.countConsumeMoney = countConsumeMoney;
	}

	public List<PcConsumeRecordInfo> getConsumeRecordList() {
		return consumeRecordList;
	}

	public void setConsumeRecordList(List<PcConsumeRecordInfo> consumeRecordList) {
		this.consumeRecordList = consumeRecordList;
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
