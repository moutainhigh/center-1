package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncResponse;

/**
 * 4.66.积分变化明细(惠家有通路)同步接口返回数据
 */
public class RsyncResponseGetAccmDetail implements IRsyncResponse {

	private boolean success;
	private String message;
	private List<RsyncModelAccmDetail> result1 = new ArrayList<RsyncModelAccmDetail>();
	private List<RsyncModelAccmDetailCancel> result2 = new ArrayList<RsyncModelAccmDetailCancel>();

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

	public List<RsyncModelAccmDetail> getResult1() {
		return result1;
	}

	public void setResult1(List<RsyncModelAccmDetail> result1) {
		this.result1 = result1;
	}

	public List<RsyncModelAccmDetailCancel> getResult2() {
		return result2;
	}

	public void setResult2(List<RsyncModelAccmDetailCancel> result2) {
		this.result2 = result2;
	}
	
}
