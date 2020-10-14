package com.cmall.productcenter.model;



public class PcProductinfoBase  {
    
    /**
     * 商品编码
     */
    private String productCode  = ""  ;
    /**
     * 商品名称
     */
    private String productName  = ""  ;
    /**
	/**
	 * 销售价
	 */
	private String sellPrice= "";
	/**
	 * 市场价
	 */
	private String marketPrice = "";
	
	/**
	 * 主图的Url
	 */
	private String mainPicUrl = "";
    
    /**
		4497153900060002	已上架
     */
    private String productStatus = "";

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

	public String getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(String sellPrice) {
		this.sellPrice = sellPrice;
	}

	public String getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(String marketPrice) {
		this.marketPrice = marketPrice;
	}

	public String getMainPicUrl() {
		return mainPicUrl;
	}

	public void setMainPicUrl(String mainPicUrl) {
		this.mainPicUrl = mainPicUrl;
	}

	public String getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(String productStatus) {
		this.productStatus = productStatus;
	}
    
}

