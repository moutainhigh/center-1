package com.cmall.groupcenter.model;

/**
 * 账户关联关系表
 * 
 * @author srnpr
 * 
 */
public class AccountRelation {

	/**
	 * 账户编号
	 */
	private String accountCode = "";

	/**
	 * 关系度数
	 */
	private int deep = 0;

	

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public int getDeep() {
		return deep;
	}

	public void setDeep(int deep) {
		this.deep = deep;
	}

	

}
