package com.cmall.groupcenter.groupapp.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GetFriendsListResult  extends RootResultWeb{
	
	@ZapcomApi(value = "一度好友数量", remark = "一度好友数量")
	String oneLevelFriendsNumber="0";
	
	@ZapcomApi(value = "好友信息列表", remark = "好友信息列表")
	List<Person> friendInfoModelList=new ArrayList<Person>();
	
	@ZapcomApi(value = "翻页结果", remark = "翻页结果")
	PageResults pageResults=new PageResults();

	public String getOneLevelFriendsNumber() {
		return oneLevelFriendsNumber;
	}

	public void setOneLevelFriendsNumber(String oneLevelFriendsNumber) {
		this.oneLevelFriendsNumber = oneLevelFriendsNumber;
	}

	public List<Person> getFriendInfoModelList() {
		return friendInfoModelList;
	}

	public void setFriendInfoModelList(List<Person> friendInfoModelList) {
		this.friendInfoModelList = friendInfoModelList;
	}

	public PageResults getPageResults() {
		return pageResults;
	}

	public void setPageResults(PageResults pageResults) {
		this.pageResults = pageResults;
	}
	
}
