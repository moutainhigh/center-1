package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

public class RsyncRequestGetStock implements IRsyncRequest {

	/**
	 * 商品编号
	 */
	private String good_id = "";

	public String getGood_id() {
		return good_id;
	}

	public void setGood_id(String good_id) {
		this.good_id = good_id;
	}
	
	

}
