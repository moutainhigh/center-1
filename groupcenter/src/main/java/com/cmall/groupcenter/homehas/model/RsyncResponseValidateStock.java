package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncResponse;

public class RsyncResponseValidateStock implements IRsyncResponse {

	private boolean success;
	private String message;
	private String is_ok;	// 库存是否足够
	private int max_cnt; // 如果订单单一主品且库存不足，返回最大可接单量
	private String site_no; // 如果订单单一主品且库存不足，最大可接单量所属仓库
	
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
	public String getIs_ok() {
		return is_ok;
	}
	public void setIs_ok(String is_ok) {
		this.is_ok = is_ok;
	}
	public int getMax_cnt() {
		return max_cnt;
	}
	public void setMax_cnt(int max_cnt) {
		this.max_cnt = max_cnt;
	}
	public String getSite_no() {
		return site_no;
	}
	public void setSite_no(String site_no) {
		this.site_no = site_no;
	}
}
