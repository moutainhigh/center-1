package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 商品信息查询接口的请求参数
 * @author jlin
 *
 */
public class RsyncRequestSyncGoodsById implements IRsyncRequest {

	private String good_id = "";

	public String getGood_id() {
		return good_id;
	}

	public void setGood_id(String good_id) {
		this.good_id = good_id;
	}

	
	
}
