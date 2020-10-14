package com.cmall.groupcenter.account.model;



import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GetPapersInfoResult extends RootResultWeb {

	@ZapcomApi(value = "证件类型")
	private String papersType = "";
	@ZapcomApi(value = "证件号码")
	private String papersCode = "";
	@ZapcomApi(value = "姓名")
	private String userName = "";
	@ZapcomApi(value = "证件名称")
	private String papersName = "";
	@ZapcomApi(value = "是否可修改",remark="1可以修改,0不可修改")
	private String isModifyFlag = "";
	
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
	public String getPapersName() {
		return papersName;
	}
	public void setPapersName(String papersName) {
		this.papersName = papersName;
	}
	public String getIsModifyFlag() {
		return isModifyFlag;
	}
	public void setIsModifyFlag(String isModifyFlag) {
		this.isModifyFlag = isModifyFlag;
	}
	
	
}
