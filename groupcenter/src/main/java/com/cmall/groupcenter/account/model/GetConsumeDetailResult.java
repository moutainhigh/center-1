package com.cmall.groupcenter.account.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GetConsumeDetailResult extends RootResultWeb{

	@ZapcomApi(value = "用户编号", demo = "MI15000")
	private String memberCode = "";
	
	@ZapcomApi(value = "关联度", remark="0:自己，1：一度，2：二度",demo = "0")
	private String relationLevel = "";
	
	@ZapcomApi(value = "总消费", demo = "3333")
	private String totalConsume = "";
	
	@ZapcomApi(value = "总返利",demo="4444" )
	private String totalRebate = "";
	
	@ZapcomApi(value = "消费列表")
	private List<ConsumeDetail> consumeList=new ArrayList<ConsumeDetail>();

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getRelationLevel() {
		return relationLevel;
	}

	public void setRelationLevel(String relationLevel) {
		this.relationLevel = relationLevel;
	}

	public String getTotalConsume() {
		return totalConsume;
	}

	public void setTotalConsume(String totalConsume) {
		this.totalConsume = totalConsume;
	}

	public String getTotalRebate() {
		return totalRebate;
	}

	public void setTotalRebate(String totalRebate) {
		this.totalRebate = totalRebate;
	}

	public List<ConsumeDetail> getConsumeList() {
		return consumeList;
	}

	public void setConsumeList(List<ConsumeDetail> consumeList) {
		this.consumeList = consumeList;
	}
	
	
}
