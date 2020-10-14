package com.cmall.groupcenter.account.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 获取好友列表结果
 * @author GaoYang
 *
 */
public class AccountFriendsListResult extends RootResultWeb{
	@ZapcomApi(value = "一度好友人数", remark = "一度好友人数")
	String oneLevelFriendsNumber="0";
	
	@ZapcomApi(value = "二度好友人数", remark = "二度好友人数")
	String twoLevelFriendsNumber="0";
	
	@ZapcomApi(value = "我的推荐人数", remark = "我的推荐人数")
	String refereeNumber="0";
	
	@ZapcomApi(value = "活跃好友人数", remark = "活跃好友人数")
	String activeFriendsNumber="0";
	
	@ZapcomApi(value = "账户编号", remark = "账户编号")
	private String accountCode="";
	
	@ZapcomApi(value = "好友信息列表", remark = "好友信息列表")
	List<AccountFridensInfo> friendsInfoList=new ArrayList<AccountFridensInfo>();

	@ZapcomApi(value = "翻页结果", remark = "翻页结果")
	PageResults pageResults=new PageResults();

	public String getOneLevelFriendsNumber() {
		return oneLevelFriendsNumber;
	}

	public void setOneLevelFriendsNumber(String oneLevelFriendsNumber) {
		this.oneLevelFriendsNumber = oneLevelFriendsNumber;
	}

	public String getTwoLevelFriendsNumber() {
		return twoLevelFriendsNumber;
	}

	public void setTwoLevelFriendsNumber(String twoLevelFriendsNumber) {
		this.twoLevelFriendsNumber = twoLevelFriendsNumber;
	}

	public String getRefereeNumber() {
		return refereeNumber;
	}

	public void setRefereeNumber(String refereeNumber) {
		this.refereeNumber = refereeNumber;
	}

	public String getActiveFriendsNumber() {
		return activeFriendsNumber;
	}

	public void setActiveFriendsNumber(String activeFriendsNumber) {
		this.activeFriendsNumber = activeFriendsNumber;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public List<AccountFridensInfo> getFriendsInfoList() {
		return friendsInfoList;
	}

	public void setFriendsInfoList(List<AccountFridensInfo> friendsInfoList) {
		this.friendsInfoList = friendsInfoList;
	}

	public PageResults getPageResults() {
		return pageResults;
	}

	public void setPageResults(PageResults pageResults) {
		this.pageResults = pageResults;
	}
	
}
