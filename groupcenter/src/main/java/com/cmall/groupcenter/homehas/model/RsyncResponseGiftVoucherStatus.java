package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncResponse;

/**
 * 同步ld用户礼金券状态明细响应
 * @author cc
 *
 */
public class RsyncResponseGiftVoucherStatus implements IRsyncResponse {
	
	private boolean success;
	private String message;
	
	/**
	 * 返回的礼金券集合
	 */
	private List<RsyncModelGiftVoucherDetail> ljqList = new ArrayList<RsyncModelGiftVoucherDetail>();

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

	public List<RsyncModelGiftVoucherDetail> getLjqList() {
		return ljqList;
	}

	public void setLjqList(List<RsyncModelGiftVoucherDetail> ljqList) {
		this.ljqList = ljqList;
	}
	
}
