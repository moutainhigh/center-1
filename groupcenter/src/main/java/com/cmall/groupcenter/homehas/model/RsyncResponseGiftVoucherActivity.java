package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncResponse;

/**
 * 同步LD礼金券活动响应
 * @author cc
 *
 */
public class RsyncResponseGiftVoucherActivity implements IRsyncResponse {

	private boolean success;
	private String message;
	
	private List<RsyncModelActivity> eventList = new ArrayList<RsyncModelActivity>();

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

	public List<RsyncModelActivity> getEventList() {
		return eventList;
	}

	public void setEventList(List<RsyncModelActivity> eventList) {
		this.eventList = eventList;
	}
	
}
