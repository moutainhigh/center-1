package com.cmall.groupcenter.homehas.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 4.59.获取调编后商品编码接口返回消息
 */
public class RsyncResponseGetNewGoodId extends RsyncResponseBase {

	@JSONField(name = "NEW_GOOD_ID")
	private String new_good_id;
	private String msg;
	
	public String getNew_good_id() {
		return new_good_id;
	}
	public void setNew_good_id(String new_good_id) {
		this.new_good_id = new_good_id;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}

}
