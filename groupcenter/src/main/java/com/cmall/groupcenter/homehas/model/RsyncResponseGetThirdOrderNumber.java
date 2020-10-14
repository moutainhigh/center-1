package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncResponse;

/**
 * 获取LD订单数量
 * @author cc
 *
 */
public class RsyncResponseGetThirdOrderNumber implements IRsyncResponse {
	private boolean success;
	private String message;
	
	private List<RsyncModelOrderNumber> result = new ArrayList<RsyncModelOrderNumber>();
	
	public boolean isSuccess() {
		return success;
	}
	public List<RsyncModelOrderNumber> getResult() {
		return result;
	}
	public void setResult(List<RsyncModelOrderNumber> result) {
		this.result = result;
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
}
