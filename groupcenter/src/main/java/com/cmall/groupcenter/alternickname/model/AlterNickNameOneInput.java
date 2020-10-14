package com.cmall.groupcenter.alternickname.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class AlterNickNameOneInput extends RootInput {
	@ZapcomApi(value="登录人的member_code",remark="登录人的member_code",require=1)
	private String member_code_wo = "";
	@ZapcomApi(value="被修改人的member_code",remark="被修改人的member_code",require=1)
	private String member_code_ta = "";
	public String getMember_code_wo() {
		return member_code_wo;
	}
	public void setMember_code_wo(String member_code_wo) {
		this.member_code_wo = member_code_wo;
	}
	public String getMember_code_ta() {
		return member_code_ta;
	}
	public void setMember_code_ta(String member_code_ta) {
		this.member_code_ta = member_code_ta;
	}
}