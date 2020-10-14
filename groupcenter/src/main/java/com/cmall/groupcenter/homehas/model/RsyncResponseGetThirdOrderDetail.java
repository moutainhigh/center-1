package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncResponse;

/**
 * 5.2.6 LD订单详情
 * @author cc
 *
 */
public class RsyncResponseGetThirdOrderDetail implements IRsyncResponse {
	
	private boolean success;
	private String message;
	
	private List<RsyncModelThirdOrderDetail> result = new ArrayList<RsyncModelThirdOrderDetail>();
	
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
	public List<RsyncModelThirdOrderDetail> getResult() {
		return result;
	}
	public void setResult(List<RsyncModelThirdOrderDetail> result) {
		this.result = result;
	}
	
	
}
