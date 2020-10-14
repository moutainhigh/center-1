package com.cmall.productcenter.model;

import java.util.ArrayList;
import java.util.List;
import com.srnpr.zapcom.topapi.RootResult;

/**
 * ClassName:店铺私有商品分类<br/>
 * Date:     2013-10-22 下午2:34:42 <br/>
 * @author   jack
 * @version  1.0
 */
public class SellerCategoryForPhpResult extends RootResult {
	private List<SellerCategory> list = new ArrayList<SellerCategory>();

	public List<SellerCategory> getList() {
		return list;
	}

	public void setList(List<SellerCategory> list) {
		this.list = list;
	}
	
}

