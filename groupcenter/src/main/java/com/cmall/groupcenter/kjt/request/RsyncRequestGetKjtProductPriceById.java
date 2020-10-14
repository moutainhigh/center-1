package com.cmall.groupcenter.kjt.request;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 根据ID批量获取商品价格信息
 * 
 * @author liqt
 * 
 */
public class RsyncRequestGetKjtProductPriceById implements IRsyncRequest {

	private String[] ProductIDs ;

	private String SaleChannelSysNo = "";
	
	public String[] getProductIDs() {
		return ProductIDs;
	}

	public void setProductIDs(String[] productIDs) {
		ProductIDs = productIDs;
	}

	public String getSaleChannelSysNo() {
		return SaleChannelSysNo;
	}

	public void setSaleChannelSysNo(String saleChannelSysNo) {
		SaleChannelSysNo = saleChannelSysNo;
	}
}
