package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

public class RsyncRequestModRtnBankStatus implements IRsyncRequest {

	private String ord_id;
	private String ord_seq;
	
	public String getOrd_id() {
		return ord_id;
	}
	public void setOrd_id(String ord_id) {
		this.ord_id = ord_id;
	}
	public String getOrd_seq() {
		return ord_seq;
	}
	public void setOrd_seq(String ord_seq) {
		this.ord_seq = ord_seq;
	}
}
