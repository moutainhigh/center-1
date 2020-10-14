package com.cmall.groupcenter.groupapp.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GetHomePageInfoResult extends RootResultWeb{

	@ZapcomApi(value = "好友信息")
	public AccountModel accountModel=new AccountModel();
	
	@ZapcomApi(value = "内容列表")
	private List<HomeContent> homeContentList=new ArrayList<HomeContent>();

	public AccountModel getAccountModel() {
		return accountModel;
	}

	public void setAccountModel(AccountModel accountModel) {
		this.accountModel = accountModel;
	}

	public List<HomeContent> getHomeContentList() {
		return homeContentList;
	}

	public void setHomeContentList(List<HomeContent> homeContentList) {
		this.homeContentList = homeContentList;
	}
	
	
}
