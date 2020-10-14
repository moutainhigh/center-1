package com.cmall.membercenter.model;

public class MLoginInputHomehas extends MLoginInput {

	/**
	 * 家有会员编号
	 */
	private String homeHasCode = "";
	/**
	 * 会员名称
	 */
	private String memberName = "";

	/**
	 * 用户标记
	 */
	private String memberSign = "";

	/**
	 * 旧系统编号
	 */
	private String oldCode = "";

	public String getHomeHasCode() {
		return homeHasCode;
	}

	public void setHomeHasCode(String homeHasCode) {
		this.homeHasCode = homeHasCode;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getMemberSign() {
		return memberSign;
	}

	public void setMemberSign(String memberSign) {
		this.memberSign = memberSign;
	}

	public String getOldCode() {
		return oldCode;
	}

	public void setOldCode(String oldCode) {
		this.oldCode = oldCode;
	}

}
