package com.cmall.groupcenter.account.model;

import java.math.BigDecimal;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 商品信息
 * @dyc
 * */
public class ProductInfo {

	@ZapcomApi(value="明细编号",remark="格式：订单号+下划线+第几条",require=1)
	private String detailCode="";
	@ZapcomApi(value="商品编号",require=1)
	private String productCode="";
	@ZapcomApi(value="SKU编号",require=1)
	private String skuCode="";
	@ZapcomApi(value="商品名称",require=1)
	private String productName="";
	@ZapcomApi(value="购买数量",require=1)
	private int buyNum=0;
	
	@ZapcomApi(value="商品原价",require=0)
	private BigDecimal originalPrice = new BigDecimal(0);
	@ZapcomApi(value="成本价",require=0)
	private BigDecimal costprice = new BigDecimal(0);
	@ZapcomApi(value="清分金额",remark="计算商品返利金额。商品折扣后金额",require=1)
	private BigDecimal reckonAmount = new BigDecimal(0);
	@ZapcomApi(value="商品售价",remark="一般情况下等同于清分金额",require=1)
	private BigDecimal salePrice = new BigDecimal(0);
	@ZapcomApi(value="是否参与清分",remark="1:参与,0:不参与",require=1)
	private String isReckon="";
	/**
	 * 获取  detailCode
	 */
	public String getDetailCode() {
		return detailCode;
	}
	/**
	 * 设置 
	 * @param detailCode 
	 */
	public void setDetailCode(String detailCode) {
		this.detailCode = detailCode;
	}
	/**
	 * 获取  productCode
	 */
	public String getProductCode() {
		return productCode;
	}
	/**
	 * 设置 
	 * @param productCode 
	 */
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	/**
	 * 获取  skuCode
	 */
	public String getSkuCode() {
		return skuCode;
	}
	/**
	 * 设置 
	 * @param skuCode 
	 */
	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}
	/**
	 * 获取  productName
	 */
	public String getProductName() {
		return productName;
	}
	/**
	 * 设置 
	 * @param productName 
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}
	/**
	 * 获取  buyNum
	 */
	public int getBuyNum() {
		return buyNum;
	}
	/**
	 * 设置 
	 * @param buyNum 
	 */
	public void setBuyNum(int buyNum) {
		this.buyNum = buyNum;
	}
	/**
	 * 获取  originalPrice
	 */
	public BigDecimal getOriginalPrice() {
		return originalPrice;
	}
	/**
	 * 设置 
	 * @param originalPrice 
	 */
	public void setOriginalPrice(BigDecimal originalPrice) {
		this.originalPrice = originalPrice;
	}
	/**
	 * 获取  costprice
	 */
	public BigDecimal getCostprice() {
		return costprice;
	}
	/**
	 * 设置 
	 * @param costprice 
	 */
	public void setCostprice(BigDecimal costprice) {
		this.costprice = costprice;
	}
	/**
	 * 获取  reckonAmount
	 */
	public BigDecimal getReckonAmount() {
		return reckonAmount;
	}
	/**
	 * 设置 
	 * @param reckonAmount 
	 */
	public void setReckonAmount(BigDecimal reckonAmount) {
		this.reckonAmount = reckonAmount;
	}
	/**
	 * 获取  salePrice
	 */
	public BigDecimal getSalePrice() {
		return salePrice;
	}
	/**
	 * 设置 
	 * @param salePrice 
	 */
	public void setSalePrice(BigDecimal salePrice) {
		this.salePrice = salePrice;
	}
	/**
	 * 获取  isReckon
	 */
	public String getIsReckon() {
		return isReckon;
	}
	/**
	 * 设置 
	 * @param isReckon 
	 */
	public void setIsReckon(String isReckon) {
		this.isReckon = isReckon;
	}
	
}
