package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncResponse;

/**
 * 5.2.6 取消TV品订单
 * @author cc
 *
 */
public class RsyncResponseRecordTvOrderStat implements IRsyncResponse {

	private boolean success;
	private String message;
	private List<RsyncModelRecordTvOrderStat> result = new ArrayList<RsyncModelRecordTvOrderStat>();
	public boolean isSuccess() {
		return success;
	}
	public List<RsyncModelRecordTvOrderStat> getResult() {
		return result;
	}
	public void setResult(List<RsyncModelRecordTvOrderStat> result) {
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
