package com.cmall.usercenter.model.api;

import java.util.List;

import com.cmall.usercenter.model.CollectionSellerModel;
import com.cmall.usercenter.model.ShopTemplateForI;
import com.cmall.usercenter.model.UcSellerInfo;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetShopTemplateResult extends RootResult {
	
	private List<ShopTemplateForI> shopTemplateList = null;

	public List<ShopTemplateForI> getShopTemplateList() {
		return shopTemplateList;
	}

	public void setShopTemplateList(List<ShopTemplateForI> shopTemplateList) {
		this.shopTemplateList = shopTemplateList;
	}

	
	
}
