/**
 * Project Name:productcenter
 * File Name:SellerCategoryInput.java
 * Package Name:com.cmall.productcenter.model
 * Date:2013-10-21下午1:45:39
 *
*/

package com.cmall.productcenter.model;

import com.srnpr.zapcom.topapi.RootInput;

/**
 * ClassName:根据类目编号获取类目cps<br/>
 * Date:     2014-05-07 下午1:34:42 <br/>
 * @author   jack
 * @version  1.0
 */
public class GetCategoryCpsApiInput extends RootInput {
	private String category_code = "";

	public String getCategory_code() {
		return category_code;
	}

	public void setCategory_code(String category_code) {
		this.category_code = category_code;
	}
	
	
	
}

