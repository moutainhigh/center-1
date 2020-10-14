package com.cmall.groupcenter.kjt.request;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 商品分销渠道库存批量获取
 * @author zmm
 *
 */
public class RsyncRequestGetKjtProductChannelInventoryById implements
		IRsyncRequest {
	private String ProductIDs;

	private String SaleChannelSysNo = "";

	public String getProductIDs() {
		return ProductIDs;
	}

	public void setProductIDs(String productIDs) {
		ProductIDs = productIDs;
	}

	public String getSaleChannelSysNo() {
		return SaleChannelSysNo;
	}

	public void setSaleChannelSysNo(String saleChannelSysNo) {
		SaleChannelSysNo = saleChannelSysNo;
	}
}
