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
 * ClassName:place编号<br/>
 * Date:     2014-07-23 上午11:21:42 <br/>
 * @author   李国杰
 * @version  1.0
 */
public class GetAppAndColumnInput extends RootInput {
	private String placeCode = "";

	public String getPlaceCode() {
		return placeCode;
	}

	public void setPlaceCode(String placeCode) {
		this.placeCode = placeCode;
	}

	public GetAppAndColumnInput(String placeCode) {
		super();
		this.placeCode = placeCode;
	}

	public GetAppAndColumnInput() {
		super();
	}

}

