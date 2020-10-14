package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncResponse;

/**
 * 5.2.6 LD订单列表
 * @author cc
 *
 */
public class RsyncResponseGetThirdOrderList implements IRsyncResponse {

	private boolean success;
	private String message;
	
	private List<RsyncModelThirdOrder> result = new ArrayList<RsyncModelThirdOrder>();

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

	public List<RsyncModelThirdOrder> getResult() {
		return result;
	}

	public void setResult(List<RsyncModelThirdOrder> result) {
		this.result = result;
	}
	
	
}
