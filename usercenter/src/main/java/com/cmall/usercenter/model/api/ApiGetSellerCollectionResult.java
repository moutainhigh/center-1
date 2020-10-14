package com.cmall.usercenter.model.api;

import java.util.List;

import com.cmall.usercenter.model.CollectionSellerModel;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetSellerCollectionResult extends RootResult {
	
	private List<CollectionSellerModel> collectionList = null;

	public List<CollectionSellerModel> getCollectionList() {
		return collectionList;
	}

	public void setCollectionList(List<CollectionSellerModel> collectionList) {
		this.collectionList = collectionList;
	}
	
	

}
