/**
 * Project Name:productcenter
 * File Name:SellerCategoryResult.java
 * Package Name:com.cmall.productcenter.model
 * Date:2013-10-21下午1:54:18
 *
*/

package com.cmall.productcenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;

/**
 * ClassName:店铺私有商品分类<br/>
 * Date:     2013-10-21 下午1:34:42 <br/>
 * @author   jack
 * @version  1.0
 */
public class SellerCategoryThreeResult extends RootResult {
	
	private String parentCategoryCode = "";
	
	private String categoryName = "";
	
	private List<MDataMap> list = new ArrayList<MDataMap>();

	public String getParentCategoryCode() {
		return parentCategoryCode;
	}

	public void setParentCategoryCode(String parentCategoryCode) {
		this.parentCategoryCode = parentCategoryCode;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public List<MDataMap> getList() {
		return list;
	}

	public void setList(List<MDataMap> list) {
		this.list = list;
	}
}

