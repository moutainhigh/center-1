package com.cmall.groupcenter.pc.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * PC版本扣款内容
 * @author GaoYang
 *
 */
public class PcCutPaymentRecordInfo {
	@ZapcomApi(value = "扣款时间", remark = "扣款时间")
	private String createTime="";
	
	@ZapcomApi(value = "扣款金额", remark = "扣款金额")
	private String withdrawMoney="";
	
	@ZapcomApi(value = "扣款原因", remark = "扣款原因")
	private String withdrawChangeType="";

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getWithdrawMoney() {
		return withdrawMoney;
	}

	public void setWithdrawMoney(String withdrawMoney) {
		this.withdrawMoney = withdrawMoney;
	}

	public String getWithdrawChangeType() {
		return withdrawChangeType;
	}

	public void setWithdrawChangeType(String withdrawChangeType) {
		this.withdrawChangeType = withdrawChangeType;
	}
	

}
