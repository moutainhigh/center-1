package com.cmall.productcenter.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PcProductInfoForI {
	
	
	/**
     * 商品编码
     */
    private String productCode  = ""  ;
    /**
     * 商品名称
     */
    private String produtName  = ""  ;
    /**
     * 卖家编号
     */
    private String sellerCode  = ""  ;
    /**
     * 品牌编号
     */
    private String brandCode  = ""  ;
    /**
     * 商品重量
     */
    private BigDecimal productWeight = new BigDecimal(0.00)   ;
    /**
     * 上下架状态 0 非可售 1可售
     */
    private Integer flagSale   = 0 ;
    /**
     * 
     */
    private String createTime  = ""  ;
    /**
     * 
     */
    private String updateTime  = ""  ;
    
	/**
	 * 最小销售价
	 */
	private BigDecimal minSellPrice=new BigDecimal(0.00);
	/**
	 * 最大销售价
	 */
	private BigDecimal maxSellPrice=new BigDecimal(0.00);
	/**
	 * 市场价
	 */
	private BigDecimal marketPrice = new BigDecimal(0.00);
    
	
    /**
     * 	4497153900060001	待上架
		4497153900060002	已上架
		4497153900060003	商家下架
		4497153900060004	平台强制下架
     */
    private String productStatus = "";
    

	public String getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(String productStatus) {
		this.productStatus = productStatus;
	}

	
	/**
	 * 主图的Url
	 */
	private String mainPicUrl = "";
	
	
	/**
	 * 是否货到付款 0 否 1 是
	 */
	private int flagPayway = 0;
	
    
    /**
     * 商品的Sku列表的属性信息
     */
    private List<ProductSkuInfo> productSkuInfoList = null;
    
    
    /**
     * 商品关联属性信息
     */
    private List<PcProductproperty> pcProductpropertyList = new ArrayList<PcProductproperty>();
    
    
    /**
     * 商品分类信息
     */
    private PcCategoryinfo category = new PcCategoryinfo();
    
    
    /**
     * 商品体积
     */
    private BigDecimal productVolume = new BigDecimal(0.00);
    
    /**
     * 运费模板
     */
    private String transportTemplate ="";
    
    /**
     * 商家编码
     */
    private String sellProductcode ="";
    
    
	public PcCategoryinfo getCategory() {
		return category;
	}

	public void setCategory(PcCategoryinfo category) {
		this.category = category;
	}

	public int getFlagPayway() {
		return flagPayway;
	}

	public void setFlagPayway(int flagPayway) {
		this.flagPayway = flagPayway;
	}

	public List<PcProductproperty> getPcProductpropertyList() {
		return pcProductpropertyList;
	}

	public void setPcProductpropertyList(
			List<PcProductproperty> pcProductpropertyList) {
		this.pcProductpropertyList = pcProductpropertyList;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProdutName() {
		return produtName;
	}

	public void setProdutName(String produtName) {
		this.produtName = produtName;
	}

	public String getSellerCode() {
		return sellerCode;
	}

	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}

	public String getBrandCode() {
		return brandCode;
	}

	public void setBrandCode(String brandCode) {
		this.brandCode = brandCode;
	}

	public Integer getFlagSale() {
		return flagSale;
	}

	public void setFlagSale(Integer flagSale) {
		this.flagSale = flagSale;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getMainPicUrl() {
		return mainPicUrl;
	}

	public void setMainPicUrl(String mainPicUrl) {
		this.mainPicUrl = mainPicUrl;
	}

	public List<ProductSkuInfo> getProductSkuInfoList() {
		return productSkuInfoList;
	}

	public void setProductSkuInfoList(List<ProductSkuInfo> productSkuInfoList) {
		this.productSkuInfoList = productSkuInfoList;
	}

	public String getTransportTemplate() {
		return transportTemplate;
	}

	public void setTransportTemplate(String transportTemplate) {
		this.transportTemplate = transportTemplate;
	}

	public String getSellProductcode() {
		return sellProductcode;
	}

	public void setSellProductcode(String sellProductcode) {
		this.sellProductcode = sellProductcode;
	}

	public BigDecimal getProductWeight() {
		return productWeight;
	}

	public void setProductWeight(BigDecimal productWeight) {
		this.productWeight = productWeight;
	}

	public BigDecimal getMinSellPrice() {
		return minSellPrice;
	}

	public void setMinSellPrice(BigDecimal minSellPrice) {
		this.minSellPrice = minSellPrice;
	}

	public BigDecimal getMaxSellPrice() {
		return maxSellPrice;
	}

	public void setMaxSellPrice(BigDecimal maxSellPrice) {
		this.maxSellPrice = maxSellPrice;
	}

	public BigDecimal getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(BigDecimal marketPrice) {
		this.marketPrice = marketPrice;
	}

	public BigDecimal getProductVolume() {
		return productVolume;
	}

	public void setProductVolume(BigDecimal productVolume) {
		this.productVolume = productVolume;
	}
    
}
