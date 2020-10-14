package com.cmall.groupcenter.groupapp.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class GetFriendInformationInfoInput extends RootInput{
	
	@ZapcomApi(value = "好友编号",require=1)
	private String memberCode= "";

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}
	
    

	
	
	
}
