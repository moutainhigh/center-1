package com.cmall.groupcenter.homehas.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.cmall.groupcenter.groupface.IRsyncRequest;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 4.59.获取调编后商品编码接口请求参数
 */
public class RsyncRequestGetNewGoodId implements IRsyncRequest {

	@JsonProperty(value = "GOOD_ID")  
	@JSONField(name = "GOOD_ID")
	private String good_id = "";

	public String getGood_id() {
		return good_id;
	}

	public void setGood_id(String good_id) {
		this.good_id = good_id;
	}

}
