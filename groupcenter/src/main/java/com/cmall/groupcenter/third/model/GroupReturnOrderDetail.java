package com.cmall.groupcenter.third.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class GroupReturnOrderDetail {

	@ZapcomApi(value = "退货明细编号",remark="微公社创建订单时传入的明细编号",demo = "20945628_1", require = 1)
	String detailCode="";
	
	@ZapcomApi(value = "退货sku编号",remark="sku编号",demo = "20945628", require = 1)
	String skuCode="";
	
	@ZapcomApi(value = "退货数量",remark="退货数量",demo = "2", require = 1)
	int  productNumber=0;

	public String getDetailCode() {
		return detailCode;
	}

	public void setDetailCode(String detailCode) {
		this.detailCode = detailCode;
	}

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	public int getProductNumber() {
		return productNumber;
	}

	public void setProductNumber(int productNumber) {
		this.productNumber = productNumber;
	}
}
