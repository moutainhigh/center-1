/**
 * Project Name:productcenter
 * File Name:AppSelectInput.java
 * Package Name:com.cmall.productcenter.model
 * Date:2014-07-21下午4:46:39
 *
*/

package com.cmall.productcenter.model;

import com.srnpr.zapcom.topapi.RootInput;

/**
 * ClassName:app编号<br/>
 * Date:     2014-07-21 下午4:47:42 <br/>
 * @author   李国杰
 * @version  1.0
 */
public class AppSelectInput extends RootInput {
	private String appCode = "";

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public AppSelectInput(String appCode) {
		super();
		this.appCode = appCode;
	}

	public AppSelectInput() {
		super();
	}

}

