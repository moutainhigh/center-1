package com.cmall.groupcenter.alternickname.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class AlterNickNameOneResult extends RootResultWeb {
	@ZapcomApi(value="昵称",remark="昵称",require=1)
	private String nickname;

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
}