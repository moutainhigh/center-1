package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ApiOrderDonationDetailsResult extends RootResultWeb{
	@ZapcomApi(value="商品编号")
	private String productCode = "";
	@ZapcomApi(value="图片链接")
	private String pictureUrl = "";
	@ZapcomApi(value="名称")
	private String detailsName = "";
	@ZapcomApi(value="数量")
	private String detailsNumber = "";
	
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getPictureUrl() {
		return pictureUrl;
	}
	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}
	public String getDetailsName() {
		return detailsName;
	}
	public void setDetailsName(String detailsName) {
		this.detailsName = detailsName;
	}
	public String getDetailsNumber() {
		return detailsNumber;
	}
	public void setDetailsNumber(String detailsNumber) {
		this.detailsNumber = detailsNumber;
	}
	
	
	
}
