package com.cmall.groupcenter.kjt.request;

import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncRequest;

public class RsyncRequestTraceOrder implements IRsyncRequest {

	private List<Long> OrderIds;
	private long SalesChannelSysNo;
	
	public List<Long> getOrderIds() {
		return OrderIds;
	}
	public void setOrderIds(List<Long> orderIds) {
		OrderIds = orderIds;
	}
	public long getSalesChannelSysNo() {
		return SalesChannelSysNo;
	}
	public void setSalesChannelSysNo(long salesChannelSysNo) {
		SalesChannelSysNo = salesChannelSysNo;
	}
	
}
