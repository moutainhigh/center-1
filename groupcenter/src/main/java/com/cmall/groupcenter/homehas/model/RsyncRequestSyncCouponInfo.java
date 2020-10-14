package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 批量同步折扣券的请求参数
 * 参考RsyncRequestSyncOrders
 * @author cc
 *
 */
public class RsyncRequestSyncCouponInfo implements IRsyncRequest  {
	/**
	 * 精确到秒  2018-04-16 12:12:12
	 */
	private String startDate = "";

	/**
	 * 精确到秒  2018-04-16 12:12:12
	 */
	private String endDate = "";

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	
}