package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

public class RsyncRequestProductForScanCode implements IRsyncRequest {

	/**
	 * 频道
	 */
	private String so_id = "";

	public String getSo_id() {
		return so_id;
	}

	public void setSo_id(String so_id) {
		this.so_id = so_id;
	}
	
	
}
