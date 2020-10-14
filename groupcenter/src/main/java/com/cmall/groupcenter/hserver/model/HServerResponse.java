package com.cmall.groupcenter.hserver.model;

/**
 * 统一响应数据
 * @author jlin
 *
 */
public class HServerResponse {

	private boolean success = true;
	private String moi_code_cd = "";

	public HServerResponse() {
	}
	
	public HServerResponse(boolean success, String moi_code_cd) {
		super();
		this.success = success;
		this.moi_code_cd = moi_code_cd;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMoi_code_cd() {
		return moi_code_cd;
	}

	public void setMoi_code_cd(String moi_code_cd) {
		this.moi_code_cd = moi_code_cd;
	}

	
}
