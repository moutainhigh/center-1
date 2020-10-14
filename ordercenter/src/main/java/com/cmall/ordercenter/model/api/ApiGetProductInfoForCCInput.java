package com.cmall.ordercenter.model.api;

import java.math.BigDecimal;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetProductInfoForCCInput extends RootInput {

	/** 
	 * 商品编码 
	 */
	@ZapcomApi(value="商品编号")
	private String productCode="";
	
	/**
	 * 商品名称
	 */
	@ZapcomApi(value="商品名称")
	private String productName="";
	/**
	 * 商品SKU编码
	 */
	@ZapcomApi(value="商品SKU编码")
	private String skuCode="";
	
	/**
	 * 商品状态
	 */
	@ZapcomApi(value="商品状态")
	private String productStatus="";
	
	/**
	 * 商户编号
	 */
	@ZapcomApi(value="商户编号")
	private String sellerCode="";
	
	/**
	 * 商户名称
	 */
	@ZapcomApi(value="商户名称")
	private String sellerName = "";
	
	/**
	 * 商品类型
	 */
	@ZapcomApi(value="商品类型")
	private String validateFlag = "";
	/**
	 * 商品分类
	 */
	@ZapcomApi(value="商品分类")
	private String productCategory = "";
	
	/**
	 * 商品品牌
	 */
	@ZapcomApi(value="商品品牌")
	private String brandCode = "";
		
	/**
	 * 商品关键词
	 */
	@ZapcomApi(value="商品关键词")
	private String labels = "";
	
	/**
	 * 商品标签
	 */
	@ZapcomApi(value="商品标签")
    private String keyword = "";
	
	/**
	 * 商品价格区间最小
	 *
	 */
	@ZapcomApi(value="商品价格区间最小")
	private BigDecimal minSellPrice = new BigDecimal(0);

	/**
	 * 商品价格区间最大
	 *
	 */
	@ZapcomApi(value="商品价格区间最大")
	private BigDecimal maxSellPrice = new BigDecimal(0);

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

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	public String getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(String productStatus) {
		this.productStatus = productStatus;
	}

	public String getSellerCode() {
		return sellerCode;
	}

	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getValidateFlag() {
		return validateFlag;
	}

	public void setValidateFlag(String validateFlag) {
		this.validateFlag = validateFlag;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public String getBrandCode() {
		return brandCode;
	}

	public void setBrandCode(String brandCode) {
		this.brandCode = brandCode;
	}

	public String getLabels() {
		return labels;
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
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
}
