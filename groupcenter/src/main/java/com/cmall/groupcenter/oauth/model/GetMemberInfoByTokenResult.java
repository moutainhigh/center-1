package com.cmall.groupcenter.oauth.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GetMemberInfoByTokenResult extends RootResultWeb {

	@ZapcomApi(value = "用户编号", demo = "123456", require = 1, remark = "用户的编号，用于建立各种关联关系的唯一标识")
	private String memberCode = "";

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	@ZapcomApi(value = "是否可绑定关系", demo = "0", require = 1, remark = "是否可绑定上级，0为不可绑定，1为可绑定")
	private int flagRelation = 0;

	@ZapcomApi(value = "登陆名", demo = "13522283810", require = 1, remark = "用户的登陆名")
	private String loginName = "";
	
	@ZapcomApi(value = "上级用户编号", demo = "123456", require = 1, remark = "上级用户编号")
	private String parentMemberCode = "";
	
	@ZapcomApi(value = "等级名称")
	private String level_name = "";

	public String getParentMemberCode() {
		return parentMemberCode;
	}

	public void setParentMemberCode(String parentMemberCode) {
		this.parentMemberCode = parentMemberCode;
	}

	public int getFlagRelation() {
		return flagRelation;
	}

	public void setFlagRelation(int flagRelation) {
		this.flagRelation = flagRelation;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLevel_name() {
		return level_name;
	}

	public void setLevel_name(String level_name) {
		this.level_name = level_name;
	}

}
