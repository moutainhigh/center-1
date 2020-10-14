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
public class SellerCategoryInput extends RootInput {
	private String seller_code = "";

	private String showAll =  "0";
	
	private String maxLevel =  "";
	
	
	public String getShowAll() {
		return showAll;
	}

	public void setShowAll(String showAll) {
		this.showAll = showAll;
	}

	public String getSeller_code() {
		return seller_code;
	}

	public void setSeller_code(String seller_code) {
		this.seller_code = seller_code;
	}

	public SellerCategoryInput(String seller_code) {
		super();
		this.seller_code = seller_code;
	}

	public SellerCategoryInput() {
		super();
	}

	public String getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(String maxLevel) {
		this.maxLevel = maxLevel;
	}
	
}

