package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncResponse;

public class RsyncResponseAddOrder implements IRsyncResponse {

	private boolean success;
	private String message;
	
	private String status;//新添加字段 状态码
	
	private String ord_id;
	private String dlv_add_seq;
	private String cust_id;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getOrd_id() {
		return ord_id;
	}
	public void setOrd_id(String ord_id) {
		this.ord_id = ord_id;
	}
	public String getDlv_add_seq() {
		return dlv_add_seq;
	}
	public void setDlv_add_seq(String dlv_add_seq) {
		this.dlv_add_seq = dlv_add_seq;
	}
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
