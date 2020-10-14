package com.cmall.groupcenter.kjt.request;

import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncRequest;

public class RsyncRequestInvoiceFEPBillPost implements IRsyncRequest {

	private List<Long> OrderIds;
	private long SalesChannelCode;
	
	public List<Long> getOrderIds() {
		return OrderIds;
	}
	public void setOrderIds(List<Long> orderIds) {
		OrderIds = orderIds;
	}
	public long getSalesChannelCode() {
		return SalesChannelCode;
	}
	public void setSalesChannelCode(long salesChannelCode) {
		SalesChannelCode = salesChannelCode;
	}
	
}
