package com.cmall.groupcenter.groupapp.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GetFriendInformationInfoResult extends RootResultWeb {
	@ZapcomApi(value = "好友信息")
	public Person friendInfo=new Person();

	public Person getFriendInfo() {
		return friendInfo;
	}

	public void setFriendInfo(Person friendInfo) {
		this.friendInfo = friendInfo;
	}

    
	
	
}
