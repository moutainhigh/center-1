package com.cmall.groupcenter.account.model;

import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ShowMoneyHistoryResult extends RootResultWeb{

	@ZapcomApi(value = "本月返利", remark = "本月返利", demo = "0.00")
	private String currentRebateMoney = "0.00";
	
	@ZapcomApi(value = "当前月份", remark = "当前月份", demo = "")
	private String currentMonth = "";
	
	@ZapcomApi(value = "当前级别", remark = "当前级别", demo = "")
	private String currentLevelName = "";
	
	@ZapcomApi(value = "本月消费", remark = "本月消费", demo = "")
	private String currentConsume = "0.00";
	
	@ZapcomApi(value = "距离升级还需的消费金额", remark = "距离升级还需的消费金额", demo = "")
	private String GapConsume = "";
	
	@ZapcomApi(value = "历史记录", remark = "历史记录", demo = "")
	private List<MoneyHistoryDetail> list=null;

	public String getCurrentRebateMoney() {
		return currentRebateMoney;
	}

	public void setCurrentRebateMoney(String currentRebateMoney) {
		this.currentRebateMoney = currentRebateMoney;
	}

	public String getCurrentMonth() {
		return currentMonth;
	}

	public void setCurrentMonth(String currentMonth) {
		this.currentMonth = currentMonth;
	}

	public String getCurrentLevelName() {
		return currentLevelName;
	}

	public void setCurrentLevelName(String currentLevelName) {
		this.currentLevelName = currentLevelName;
	}

	public String getCurrentConsume() {
		return currentConsume;
	}

	public void setCurrentConsume(String currentConsume) {
		this.currentConsume = currentConsume;
	}

	public String getGapConsume() {
		return GapConsume;
	}

	public void setGapConsume(String gapConsume) {
		GapConsume = gapConsume;
	}

	public List<MoneyHistoryDetail> getList() {
		return list;
	}

	public void setList(List<MoneyHistoryDetail> list) {
		this.list = list;
	}
}
