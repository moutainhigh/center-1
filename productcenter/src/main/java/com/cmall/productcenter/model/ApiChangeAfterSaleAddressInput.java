package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiChangeAfterSaleAddressInput extends RootInput {

	@ZapcomApi(value="新的商户售后地址uid")
	private String changeUid;
	@ZapcomApi(value="商户code")
	private String manageCode;
	@ZapcomApi(value="之前的商户售后地址uid")
	private String beforeUid;
	@ZapcomApi(value="商品uid")
	private String productUid;
	
	public String getChangeUid() {
		return changeUid;
	}
	public void setChangeUid(String changeUid) {
		this.changeUid = changeUid;
	}
	public String getManageCode() {
		return manageCode;
	}
	public void setManageCode(String manageCode) {
		this.manageCode = manageCode;
	}
	public String getBeforeUid() {
		return beforeUid;
	}
	public void setBeforeUid(String beforeUid) {
		this.beforeUid = beforeUid;
	}
	public String getProductUid() {
		return productUid;
	}
	public void setProductUid(String productUid) {
		this.productUid = productUid;
	}
	
	
}
