package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class ApiGetProductMaybeLove {
	
	@ZapcomApi(value="商品编号")
	private String procuctCode="";
	
	@ZapcomApi(value="商品名称")
	private String productName="";
	
	@ZapcomApi(value="销售价")
	private String productPrice="";
	
	@ZapcomApi(value="商品图片")
	private String  mainPicUrl="";
	
	@ZapcomApi(value="市场价")
	private String marketPrice="";
	
	public String getProcuctCode() {
		return procuctCode;
	}

	public void setProcuctCode(String procuctCode) {
		this.procuctCode = procuctCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(String productPrice) {
		this.productPrice = productPrice;
	}

	public String getMainPicUrl() {
		return mainPicUrl;
	}

	public void setMainPicUrl(String mainPicUrl) {
		this.mainPicUrl = mainPicUrl;
	}

	public String getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(String marketPrice) {
		this.marketPrice = marketPrice;
	}

	public String getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(String productStatus) {
		this.productStatus = productStatus;
	}

	@ZapcomApi(value="商品状态")
	private String productStatus="";

}
