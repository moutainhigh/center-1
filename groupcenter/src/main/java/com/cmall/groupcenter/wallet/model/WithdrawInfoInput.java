package com.cmall.groupcenter.wallet.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 钱包(提现信息Input)
 * @author huangs
 * @date 2011-11-4
 *
 */
public class WithdrawInfoInput extends RootInput{
	
	@ZapcomApi(value = "用户编号", remark = "用户编号", demo = "" , require=1)
	private String memberCode = "";

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	
	

}
