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
 * ClassName:店铺私有商品分类<br/>
 * Date:     2013-10-21 下午1:34:42 <br/>
 * @author   jack
 * @version  1.0
 */
public class SellerCategoryProductsInput extends RootInput {
	private String categoryCode = "";

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public SellerCategoryProductsInput(String categoryCode) {
		super();
		this.categoryCode = categoryCode;
	}

	public SellerCategoryProductsInput() {
		super();
	}

}

