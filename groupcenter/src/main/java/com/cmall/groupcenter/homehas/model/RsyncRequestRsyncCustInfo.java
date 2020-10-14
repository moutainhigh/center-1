package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 同步会员信息
 * @author jlin
 *
 */
public class RsyncRequestRsyncCustInfo implements IRsyncRequest {

	/**
	 * 家有会员编号
	 */
	private String cust_id = "";

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	
	
}
