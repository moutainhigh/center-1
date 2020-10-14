package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 查询客户积分、储值金、暂存款查询接口的请求参数
 */
public class RsyncRequestGetCustRelHb implements IRsyncRequest {

	private String cust_id = "";
	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	
}
