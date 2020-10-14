package com.cmall.membercenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class MemberConfig {

	@ZapcomApi(value = "是否接受消息通知")
	private int push = 1;

	@ZapcomApi(value = "关联账号")
	private List<MemberOther> accounts = new ArrayList<MemberOther>();

	public int getPush() {
		return push;
	}

	public void setPush(int push) {
		this.push = push;
	}

	public List<MemberOther> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<MemberOther> accounts) {
		this.accounts = accounts;
	}

}
