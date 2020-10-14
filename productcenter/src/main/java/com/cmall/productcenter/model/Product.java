package com.cmall.productcenter.model;

import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;


/**
 * 商品缓存的实体类
 * @author zhouguohui 20141022
 * @version 1.0
 */
public class Product {
	/**
	 * 商品ID
	 */
	@Field
	private String productCode ;
	/**
	 * 商品名称
	 */
	@Field
	private String productName;
	/**
	 * 关键词 对应数据库labels
	 */
	@Field
	private String remarkName;
	/**
	 * 商品详情信息
	 */
	@Field
	private String productDetails;
	/**
	 * 图片url地址
	 */
	@Field
	private String mainpicUrl;
	/**
	 * 商品原价
	 */
	@Field
	private Double originalPrice;
	/**
	 * 当前价格
	 */
	@Field
	private Double currentPrice;
	/**
	 * 更新时间
	 */
	@Field
	private Date updateTime;
	/**
	 * 一级ID
	 */
	@Field
	private List<String> oneId;
	/**
	 * 一级name
	 */
	@Field
	private List<String> oneName;
	/**
	 * 二级ID
	 */
	@Field
	private List<String> twoId;
	/**
	 * 二级name
	 */
	@Field
	private List<String> twoName;
	/**
	 * 商品规格
	 */
	@Field
	private List<String> propertyValue ;
	/**
	 * 品牌ID
	 */
	@Field
	private String brandCode;
	/**
	 * 品牌名称
	 */
	@Field
	private String brandCodeName;
	/**
	 * 商品销量
	 */
	@Field
	private int productNumber;
	/**
	 * 商品卖家
	 */
	@Field
	private String sellerCode;
	/**
	 * 标签
	 */
	@Field
	private String tagList;
	/**
	 * 商品库存  0代表没有库存  1代表有库存
	 */
	@Field
	private int stockNum;
	/**
	 * 人气数量
	 */
	@Field
	private Double popularityNum;
	/**
	 * 是否海外购  0代表不是海外购  1代表海外购
	 */
	@Field
	private int smallSellerCode;
	/**
	 * @return the productCode
	 */
	public String getProductCode() {
		return productCode;
	}
	/**
	 * @param productCode the productCode to set
	 */
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	/**
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}
	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}
	/**
	 * @return the remarkName
	 */
	public String getRemarkName() {
		return remarkName;
	}
	/**
	 * @param remarkName the remarkName to set
	 */
	public void setRemarkName(String remarkName) {
		this.remarkName = remarkName;
	}
	/**
	 * @return the mainpicUrl
	 */
	public String getMainpicUrl() {
		return mainpicUrl;
	}
	/**
	 * @param mainpicUrl the mainpicUrl to set
	 */
	public void setMainpicUrl(String mainpicUrl) {
		this.mainpicUrl = mainpicUrl;
	}
	/**
	 * @return the brandCode
	 */
	public String getBrandCode() {
		return brandCode;
	}
	/**
	 * @param brandCode the brandCode to set
	 */
	public void setBrandCode(String brandCode) {
		this.brandCode = brandCode;
	}
	/**
	 * @return the oneId
	 */
	public List<String> getOneId() {
		return oneId;
	}
	/**
	 * @param oneId the oneId to set
	 */
	public void setOneId(List<String> oneId) {
		this.oneId = oneId;
	}
	/**
	 * @return the oneName
	 */
	public List<String> getOneName() {
		return oneName;
	}
	/**
	 * @param oneName the oneName to set
	 */
	public void setOneName(List<String> oneName) {
		this.oneName = oneName;
	}
	/**
	 * @return the twoId
	 */
	public List<String> getTwoId() {
		return twoId;
	}
	/**
	 * @param twoId the twoId to set
	 */
	public void setTwoId(List<String> twoId) {
		this.twoId = twoId;
	}
	/**
	 * @return the twoName
	 */
	public List<String> getTwoName() {
		return twoName;
	}
	/**
	 * @param twoName the twoName to set
	 */
	public void setTwoName(List<String> twoName) {
		this.twoName = twoName;
	}
	/**
	 * @return the productNumber
	 */
	public int getProductNumber() {
		return productNumber;
	}
	/**
	 * @param productNumber the productNumber to set
	 */
	public void setProductNumber(int productNumber) {
		this.productNumber = productNumber;
	}
	
	/**
	 * @return the sellerCode
	 */
	public String getSellerCode() {
		return sellerCode;
	}
	/**
	 * @param sellerCode the sellerCode to set
	 */
	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}
	/**
	 * @return the productDetails
	 */
	public String getProductDetails() {
		return productDetails;
	}
	/**
	 * @param productDetails the productDetails to set
	 */
	public void setProductDetails(String productDetails) {
		this.productDetails = productDetails;
	}
	/**
	 * @return the brandCodeName
	 */
	public String getBrandCodeName() {
		return brandCodeName;
	}
	/**
	 * @param brandCodeName the brandCodeName to set
	 */
	public void setBrandCodeName(String brandCodeName) {
		this.brandCodeName = brandCodeName;
	}
	/**
	 * @return the originalPrice
	 */
	public Double getOriginalPrice() {
		return originalPrice;
	}
	/**
	 * @param originalPrice the originalPrice to set
	 */
	public void setOriginalPrice(Double originalPrice) {
		this.originalPrice = originalPrice;
	}
	/**
	 * @return the currentPrice
	 */
	public Double getCurrentPrice() {
		return currentPrice;
	}
	/**
	 * @param currentPrice the currentPrice to set
	 */
	public void setCurrentPrice(Double currentPrice) {
		this.currentPrice = currentPrice;
	}
	/**
	 * @return the tagList
	 */
	public String getTagList() {
		return tagList;
	}
	/**
	 * @param tagList the tagList to set
	 */
	public void setTagList(String tagList) {
		this.tagList = tagList;
	}
	/**
	 * @return the propertyValue
	 */
	public List<String> getPropertyValue() {
		return propertyValue;
	}
	/**
	 * @param propertyValue the propertyValue to set
	 */
	public void setPropertyValue(List<String> propertyValue) {
		this.propertyValue = propertyValue;
	}
	/**
	 * @return the stockNum
	 */
	public int getStockNum() {
		return stockNum;
	}
	/**
	 * @param stockNum the stockNum to set
	 */
	public void setStockNum(int stockNum) {
		this.stockNum = stockNum;
	}
	/**
	 * @return the updateTime
	 */
	public Date getUpdateTime() {
		return updateTime;
	}
	/**
	 * @param updateTime the updateTime to set
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	/**
	 * @return the popularityNum
	 */
	public Double getPopularityNum() {
		return popularityNum;
	}
	/**
	 * @param popularityNum the popularityNum to set
	 */
	public void setPopularityNum(Double popularityNum) {
		this.popularityNum = popularityNum;
	}
	/**
	 * @return the smallSellerCode
	 */
	public int getSmallSellerCode() {
		return smallSellerCode;
	}
	/**
	 * @param smallSellerCode the smallSellerCode to set
	 */
	public void setSmallSellerCode(int smallSellerCode) {
		this.smallSellerCode = smallSellerCode;
	}
	
	
	
}
