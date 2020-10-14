package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 获取个人主页输入参数
 * @author GaoYang
 *
 */
public class AccountPersonalHomepageInput extends RootInput{
	@ZapcomApi(value = "用户编号", remark = "用户编号",require = 1)
	private String memberCode="";
	@ZapcomApi(value = "筛选类型",remark = "1:我的好友  2:本月活跃" ,demo= "1,2",require = 0)
	private String selectionType = "";
	@ZapcomApi(value = "好友关系级别", remark = "好友关系级别  0:自己 1：一度好友 2：二度好友 -1：推荐人 -2:二度推荐人",require = 0)
	private String relationLevel="";

	public String getMemberCode() {
		return memberCode;
	}
	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}
	public String getSelectionType() {
		return selectionType;
	}
	public void setSelectionType(String selectionType) {
		this.selectionType = selectionType;
	}
	public String getRelationLevel() {
		return relationLevel;
	}
	public void setRelationLevel(String relationLevel) {
		this.relationLevel = relationLevel;
	}
	
}
