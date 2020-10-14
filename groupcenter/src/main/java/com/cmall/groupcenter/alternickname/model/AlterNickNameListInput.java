package com.cmall.groupcenter.alternickname.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class AlterNickNameListInput extends RootInput {
	@ZapcomApi(value="登录人的account_code",remark="登录人的account_code",require=1)
	private String account_code_wo = "";
	@ZapcomApi(value="被修改人的account_code列表",remark="被修改人的account_code列表")
	private List<String> account_code_ta = new ArrayList<String>();
	
	public String getAccount_code_wo() {
		return account_code_wo;
	}
	public void setAccount_code_wo(String account_code_wo) {
		this.account_code_wo = account_code_wo;
	}
	public List<String> getAccount_code_ta() {
		return account_code_ta;
	}
	public void setAccount_code_ta(List<String> account_code_ta) {
		this.account_code_ta = account_code_ta;
	}
}