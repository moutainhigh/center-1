package com.cmall.groupcenter.account.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class AddPapersInput extends RootInput {

	@ZapcomApi(value = "证件类型", demo = "1", require = 1, verify = { "maxlength=2" })
	private String papersType = "";
	@ZapcomApi(value = "证件号码", demo = "123456", require = 1, verify = { "maxlength=40" })
	private String papersCode = "";
	@ZapcomApi(value = "姓名", demo = "刘测测", require = 1, verify = { "maxlength=40" })
	private String userName = "";


	public String getPapersType() {
		return papersType;
	}

	public void setPapersType(String papersType) {
		this.papersType = papersType;
	}

	public String getPapersCode() {
		return papersCode;
	}

	public void setPapersCode(String papersCode) {
		this.papersCode = papersCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
