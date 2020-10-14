package com.cmall.groupcenter.account.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ShowActionRelationResult extends RootResultWeb {

	
	@ZapcomApi(value = "一度好友总数")
	private int oneSumAccount= 0;
	
	@ZapcomApi(value = "二度好友总数")
	private int twoSumAccount = 0;
	
	@ZapcomApi(value = "本月活跃总数")
	private int actionSumAccount = 0;
	
	@ZapcomApi(value = "活跃社友明细")
	private List<RelationInfo> relationInfos = new ArrayList<RelationInfo>();
	
	@ZapcomApi(value = "翻页结果", remark = "翻页结果")
	PageResults pageResults=new PageResults();

	public PageResults getPageResults() {
		return pageResults;
	}

	public void setPageResults(PageResults pageResults) {
		this.pageResults = pageResults;
	}

	public List<RelationInfo> getRelationInfos() {
		return relationInfos;
	}

	public void setRelationInfos(List<RelationInfo> relationInfos) {
		this.relationInfos = relationInfos;
	}

	public int getOneSumAccount() {
		return oneSumAccount;
	}

	public void setOneSumAccount(int oneSumAccount) {
		this.oneSumAccount = oneSumAccount;
	}

	public int getTwoSumAccount() {
		return twoSumAccount;
	}

	public void setTwoSumAccount(int twoSumAccount) {
		this.twoSumAccount = twoSumAccount;
	}

	public int getActionSumAccount() {
		return actionSumAccount;
	}

	public void setActionSumAccount(int actionSumAccount) {
		this.actionSumAccount = actionSumAccount;
	}




}
