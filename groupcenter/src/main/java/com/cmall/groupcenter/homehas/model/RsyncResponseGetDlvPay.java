package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncResponse;

/**
 * 4.56.查询货到付款地区配置信息接口返回数据
 */
public class RsyncResponseGetDlvPay implements IRsyncResponse {

	private boolean success;
	private String message;
	private List<RsyncModelDlvPay> result = new ArrayList<RsyncModelDlvPay>();
	
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
	public List<RsyncModelDlvPay> getResult() {
		return result;
	}
	public void setResult(List<RsyncModelDlvPay> result) {
		this.result = result;
	} 
	
}
