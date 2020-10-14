package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 微公社账户推送设定类型
 * @author GaoYang
 *
 */
public class AccountPushTypeInfo {
	
	@ZapcomApi(value = "推送标题", remark = "推送标题", demo = "人脉越广希望越多")
	private String pushTitle = "";
	
	@ZapcomApi(value = "推送类型ID", remark = "推送类型ID", demo = "")
	private String pushTypeId = "";
	
	@ZapcomApi(value = "推送类型名称", remark = "推送类型名称", demo = "返利到账")
	private String pushTypeName = "";
	
	@ZapcomApi(value = "推送范围", remark = "推送范围(449747220001：一度好友,449747220002：一度好友,449747220003：自己,449747220004：关闭)", demo = "449747220001,449747220002,449747220003,449747220004")
	private String accountPushRange = "";
	
	@ZapcomApi(value = "推送类型开关", remark = "推送类型开关(1:开启2: 关闭)", demo = "1")
	private String pushTypeOnoff = "";
	
	@ZapcomApi(value = "推送范围类型", remark = "推送范围类型(0 :独立开关 1 :范围选择)", demo = "0")
	private String pushRangeType = "0";

	public String getPushTitle() {
		return pushTitle;
	}

	public void setPushTitle(String pushTitle) {
		this.pushTitle = pushTitle;
	}

	public String getPushTypeId() {
		return pushTypeId;
	}

	public void setPushTypeId(String pushTypeId) {
		this.pushTypeId = pushTypeId;
	}

	public String getPushTypeName() {
		return pushTypeName;
	}

	public void setPushTypeName(String pushTypeName) {
		this.pushTypeName = pushTypeName;
	}

	public String getAccountPushRange() {
		return accountPushRange;
	}

	public void setAccountPushRange(String accountPushRange) {
		this.accountPushRange = accountPushRange;
	}

	public String getPushTypeOnoff() {
		return pushTypeOnoff;
	}

	public void setPushTypeOnoff(String pushTypeOnoff) {
		this.pushTypeOnoff = pushTypeOnoff;
	}

	public String getPushRangeType() {
		return pushRangeType;
	}

	public void setPushRangeType(String pushRangeType) {
		this.pushRangeType = pushRangeType;
	}

	
	
}
