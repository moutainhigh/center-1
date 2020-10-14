/**
 * Project Name:productcenter
 * File Name:SellerCategoryResult.java
 * Package Name:com.cmall.productcenter.model
 * Date:2013-10-21下午1:54:18
 *
*/

package com.cmall.productcenter.model;

import com.srnpr.zapcom.topapi.RootResult;

/**
 * ClassName:店铺私有商品分类<br/>
 * Date:     2013-10-21 下午1:34:42 <br/>
 * @author   jack
 * @version  1.0
 */
public class GetCategoryCpsApiResult extends RootResult {
	private String cpsrate = "";


	public String getCpsrate() {
		return cpsrate;
	}

	public void setCpsrate(String cpsrate) {
		this.cpsrate = cpsrate;
	}
	
}

