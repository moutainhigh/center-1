package com.cmall.usercenter.model.api;

import com.srnpr.zapcom.topapi.RootInput;

/**
 * ClassName:部门下用户<br/>
 * Date:     2013-11-12 下午1:34:42 <br/>
 * @author   jack
 * @version  1.0
 */
public class UserOrganizationInput extends RootInput {
	
	private String code = "";

	/**
	 * 获取code.
	 * @return  code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * 设置code.
	 * @param   code
	 */
	public void setCode(String code) {
		this.code = code;
	}

	public UserOrganizationInput(String code) {
		super();
		this.code = code;
	}

	public UserOrganizationInput() {
		super();
	}
	
}

