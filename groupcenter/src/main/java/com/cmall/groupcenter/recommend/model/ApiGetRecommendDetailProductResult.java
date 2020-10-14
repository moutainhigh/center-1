package com.cmall.groupcenter.recommend.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class ApiGetRecommendDetailProductResult{
	@ZapcomApi(value = "商品编号")
	private String productCode;
	
	@ZapcomApi(value = "商品名称")
	private String productName;
	
	@ZapcomApi(value="商品图片")
	private String productPic;
	
	@ZapcomApi(value = "商品价格")
	private String productPrice;
	
	@ZapcomApi(value = "商品描述")
	private String productDesc;
	
	@ZapcomApi(value = "商品来源code")
	private String productSourceCode;
	
	@ZapcomApi(value = "商品来源")
	private String productSource;
	
	@ZapcomApi(value = "商品链接")
	private String productLink;

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductPic() {
		return productPic;
	}

	public void setProductPic(String productPic) {
		this.productPic = productPic;
	}

	public String getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(String productPrice) {
		this.productPrice = productPrice;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public String getProductSourceCode() {
		return productSourceCode;
	}

	public void setProductSourceCode(String productSourceCode) {
		this.productSourceCode = productSourceCode;
	}

	public String getProductSource() {
		return productSource;
	}

	public void setProductSource(String productSource) {
		this.productSource = productSource;
	}

	public String getProductLink() {
		return productLink;
	}

	public void setProductLink(String productLink) {
		this.productLink = productLink;
	}
	
}
