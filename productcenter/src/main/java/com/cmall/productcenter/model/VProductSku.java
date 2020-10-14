package com.cmall.productcenter.model;

import java.math.BigDecimal;

public class VProductSku  {
	
private String uid="";
	
	/**
	 * 产品编号
	 */
	private String skuCode ="";
	/**
	 * 商品编号
	 */
	private String productCode="";
	/**
	 * 销售价
	 */
	private BigDecimal sellPrice= new BigDecimal(0.00);
	/**
	 * 市场价
	 */
	private BigDecimal marketPrice = new BigDecimal(0.00);
	/**
	 * 库存数
	 */
	private int stockNum= 0;
	
	
	/**
	 * skuKey
	 */
	private String skuKey = "";
	
	/**
	 * skuValue
	 */
	private String skuValue = "";
	
	
	 /**
     * 商品的Sku的图片信息
     */
    private String skuPicUrl = "";
	
    
    /**
     * 商品的sku信息
     */
    private String skuName="";
    
    /**
     * 商家编码
     */
    private String sellProductcode="";
    
    
    /**
     * sku安全库存
     */
    private int securityStockNum = 0;
    
    
    /**
     * 卖家编号
     */
    private String sellerCode = "";
    
    
    /**
     * 广告语
     */
    private String skuAdv="";
    
    
    /**
     * sku二维码
     */
    private String qrcodeLink = "";
    
    
    /**
     * 积分抵扣 单位 个 需要 10%折 钱。
     */
    private BigDecimal virtualMoneyDeduction= new BigDecimal(0.00);
	
    /**
     * 上下架状态
     */
    private Integer flagSale   = 0 ;
    
    
	
	
	/**
	 * 是否货到付款 0 否 1 是
	 */
	private int flagPayway = 0;
	
	
	
    /**
     * 	4497153900060001	待上架
		4497153900060002	已上架
		4497153900060003	商家下架
		4497153900060004	平台强制下架
     */
    private String productStatus = "";



	public Integer getFlagSale() {
		return flagSale;
	}



	public void setFlagSale(Integer flagSale) {
		this.flagSale = flagSale;
	}



	public int getFlagPayway() {
		return flagPayway;
	}



	public void setFlagPayway(int flagPayway) {
		this.flagPayway = flagPayway;
	}



	public String getProductStatus() {
		return productStatus;
	}



	public void setProductStatus(String productStatus) {
		this.productStatus = productStatus;
	}



	public String getUid() {
		return uid;
	}



	public void setUid(String uid) {
		this.uid = uid;
	}



	public String getSkuCode() {
		return skuCode;
	}



	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}



	public String getProductCode() {
		return productCode;
	}



	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}



	



	public BigDecimal getSellPrice() {
		return sellPrice;
	}



	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}



	public BigDecimal getMarketPrice() {
		return marketPrice;
	}



	public void setMarketPrice(BigDecimal marketPrice) {
		this.marketPrice = marketPrice;
	}



	public void setVirtualMoneyDeduction(BigDecimal virtualMoneyDeduction) {
		this.virtualMoneyDeduction = virtualMoneyDeduction;
	}



	public int getStockNum() {
		return stockNum;
	}



	public void setStockNum(int stockNum) {
		this.stockNum = stockNum;
	}



	public String getSkuKey() {
		return skuKey;
	}



	public void setSkuKey(String skuKey) {
		this.skuKey = skuKey;
	}



	public String getSkuValue() {
		return skuValue;
	}



	public void setSkuValue(String skuValue) {
		this.skuValue = skuValue;
	}



	public String getSkuPicUrl() {
		return skuPicUrl;
	}



	public void setSkuPicUrl(String skuPicUrl) {
		this.skuPicUrl = skuPicUrl;
	}



	public String getSkuName() {
		return skuName;
	}



	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}



	public String getSellProductcode() {
		return sellProductcode;
	}



	public void setSellProductcode(String sellProductcode) {
		this.sellProductcode = sellProductcode;
	}



	public int getSecurityStockNum() {
		return securityStockNum;
	}



	public void setSecurityStockNum(int securityStockNum) {
		this.securityStockNum = securityStockNum;
	}



	public String getSellerCode() {
		return sellerCode;
	}



	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}



	public String getSkuAdv() {
		return skuAdv;
	}



	public void setSkuAdv(String skuAdv) {
		this.skuAdv = skuAdv;
	}



	public String getQrcodeLink() {
		return qrcodeLink;
	}



	public void setQrcodeLink(String qrcodeLink) {
		this.qrcodeLink = qrcodeLink;
	}



	public BigDecimal getVirtualMoneyDeduction() {
		return virtualMoneyDeduction;
	}

}
