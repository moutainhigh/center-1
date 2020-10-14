package com.cmall.productcenter.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.srnpr.xmassystem.modelproduct.PlusModelPcProductdescription;
import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class GoodsProduct {
	@ZapcomApi(value="活动编号")
	private String eventCode="";
	
	@ZapcomApi(value="开始时间")
    private String beginTime="";
	
	@ZapcomApi(value="结束时间")
    private String endTime="";
	
	@ZapcomApi(value="商品编号")
	private String productCode="";
	
	@ZapcomApi(value="sku编号")
	private String skuCode=""; 
	
	@ZapcomApi(value="sku名称")
	private String skuName="";
	
	@ZapcomApi(value="销售价",remark="就是通常划掉的那个价格")
	private BigDecimal sellingPrice=BigDecimal.ZERO;
	
	@ZapcomApi(value="成团价",remark="真实的购买价")
	private BigDecimal favorablePrice=BigDecimal.ZERO;
	
	@ZapcomApi(value="成团人数")
	private Integer purchaseNum=0;
	
	@ZapcomApi(value="销量",remark="为近三十天的销量")
	private Integer fictitionSales=0;
	
	@ZapcomApi(value="商品主图片",remark="第一张大图")
	private String mainpicUrl = "";
	
	@ZapcomApi(value="轮播图")
    private List<String> pcPicList = new ArrayList<String>();
	
	@ZapcomApi(value="描述图片")
    private PlusModelPcProductdescription description = new PlusModelPcProductdescription();

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
	 * @return the skuCode
	 */
	public String getSkuCode() {
		return skuCode;
	}

	/**
	 * @param skuCode the skuCode to set
	 */
	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	/**
	 * @return the skuName
	 */
	public String getSkuName() {
		return skuName;
	}

	/**
	 * @param skuName the skuName to set
	 */
	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}

	/**
	 * @return the sellingPrice
	 */
	public BigDecimal getSellingPrice() {
		return sellingPrice;
	}

	/**
	 * @param sellingPrice the sellingPrice to set
	 */
	public void setSellingPrice(BigDecimal sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	/**
	 * @return the favorablePrice
	 */
	public BigDecimal getFavorablePrice() {
		return favorablePrice;
	}

	/**
	 * @param favorablePrice the favorablePrice to set
	 */
	public void setFavorablePrice(BigDecimal favorablePrice) {
		this.favorablePrice = favorablePrice;
	}


	/**
	 * @return the purchaseNum
	 */
	public Integer getPurchaseNum() {
		return purchaseNum;
	}

	/**
	 * @param purchaseNum the purchaseNum to set
	 */
	public void setPurchaseNum(Integer purchaseNum) {
		this.purchaseNum = purchaseNum;
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
	 * @return the pcPicList
	 */
	public List<String> getPcPicList() {
		return pcPicList;
	}

	/**
	 * @param pcPicList the pcPicList to set
	 */
	public void setPcPicList(List<String> pcPicList) {
		this.pcPicList = pcPicList;
	}

	/**
	 * @return the description
	 */
	public PlusModelPcProductdescription getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(PlusModelPcProductdescription description) {
		this.description = description;
	}

	/**
	 * @return the fictitionSales
	 */
	public Integer getFictitionSales() {
		return fictitionSales;
	}

	/**
	 * @param fictitionSales the fictitionSales to set
	 */
	public void setFictitionSales(Integer fictitionSales) {
		this.fictitionSales = fictitionSales;
	}

	/**
	 * @return the eventCode
	 */
	public String getEventCode() {
		return eventCode;
	}

	/**
	 * @param eventCode the eventCode to set
	 */
	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}

	/**
	 * @return the beginTime
	 */
	public String getBeginTime() {
		return beginTime;
	}

	/**
	 * @param beginTime the beginTime to set
	 */
	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	/**
	 * @return the endTime
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	
}
