package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 同步ld用户礼金券状态明细请求
 * @author cc
 *
 */
public class RsyncRequestGiftVoucherStatus implements IRsyncRequest {
	/**
	 * 精确到秒  2018-04-16 12:12:12
	 */
	private String start_date = "";

	/**
	 * 精确到秒  2018-04-16 12:12:12
	 */
	private String end_date = "";

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

}
