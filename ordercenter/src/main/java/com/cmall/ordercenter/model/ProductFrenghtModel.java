package com.cmall.ordercenter.model;

import java.math.BigDecimal;

/**
 * 订单运费类
 * 一个商品，最多有两条默认的运费信息。1：默认 2：指定的特定区域
 * @author huoqiangshou
 *
 */
public class ProductFrenghtModel {
	
	
	/**
	 * 禁用
	 */
	public static String TPL_DISABLE_Y="449746250001";
	
	/**
	 * 没有禁用
	 */
	public static String TPL_DISABLE_N="449746250002";
	
	/**
	 * 免运费 卖家承担运费
	 */
	public static String IS_FREE_Y="449746250001";
	
	/**
	 * 买家承担运费
	 */
	public static String IS_FREE_N="449746250002";
	
	
	
	/**
	 * 指定区域区域 是否可售，默认可售1  不可出售0
	 */
	public static String DETAIL_ENABLE_Y="1";
	
	public static String DETAIL_ENABLE_N="0";
	
	
	
	/**
	 * 计价方式：件数：449746290001 重量：449746290002 体积：449746290003
	 */
	public static String VALUATION_TYPE_P="449746290001";
	public static String VALUATION_TYPE_W ="449746290002";
	public static String VALUATION_TYPE_C ="449746290003";
	
	/**
	 * 店铺编码
	 */
	private String sellerCode;
	
	
	/**
	 * 商品数量 
	 */
	private int amount;
	
	
	/**
	 * 商品编码
	 */
	private String productCode;
	
	/**
	 * 商品重量
	 */
	private double productWeight;
	
	/**
	 * 商品体积
	 */
	private double productVolume;
	
	/**
	 * 商品关联运费模板
	 */
	private String transportTemplate;
	
	/**
	 * 模板是否禁用 是：449746250001
	 * 否：449746250002
	 */
	private String tplDisable;
	
	
	/**
	 * 是否免运费，卖家付款为免运费449746250001
	 * 买家付款：449746250002
	 */
	private String isFree;
	
	/**
	 * 计价方式：件数：449746290001 重量：449746290002 体积：449746290003
	 */
	private String valuationType;
	
	
	/**
	 * 默认区域
	 */
	private String area;
	
	/**
	 * 默认区域编码
	 */
	private String areaCode;
	
	
	/**
	 * 默认区域 是否可售，默认可售
	 */
	private String detailEnable;
	
	/**
	 * 默认区域 开始单位
	 */
	private double expressStart;
	
	/**
	 * 默认区域 开始单位 价格
	 */
	private double expressPostage;
	
	/**
	 * 默认区域 追加单位
	 */
	private double expressPlus;
	
	/**
	 * 默认区域 追加价格
	 */
	private double expressPostagePlus;
	//----
	/**
	 * 指定区域
	 */
	private String speArea;
	
	/**
	 * 指定区域区域编码
	 */
	private String speAreaCode;
	
	
	/**
	 * 指定区域区域 是否可售，默认可售1  不可出售0
	 */
	private String speDetailEnable;
	
	/**
	 * 指定区域区域 开始单位
	 */
	private double speExpressStart;
	
	/**
	 * 指定区域区域 开始单位 价格
	 */
	private double speExpressPostage;
	
	/**
	 * 指定区域区域 追加单位
	 */
	private double speExpressPlus;
	
	/**
	 * 指定区域区域 追加价格
	 */
	private double speExpressPostagePlus;

	public String getSellerCode() {
		return sellerCode;
	}

	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getTplDisable() {
		return tplDisable;
	}

	public void setTplDisable(String tplDisable) {
		this.tplDisable = tplDisable;
	}

	public String getIsFree() {
		return isFree;
	}

	public void setIsFree(String isFree) {
		this.isFree = isFree;
	}

	public String getValuationType() {
		return valuationType;
	}

	public void setValuationType(String valuationType) {
		this.valuationType = valuationType;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getDetailEnable() {
		return detailEnable;
	}

	public void setDetailEnable(String detailEnable) {
		this.detailEnable = detailEnable;
	}


	public String getSpeArea() {
		return speArea;
	}

	public void setSpeArea(String speArea) {
		this.speArea = speArea;
	}

	public String getSpeAreaCode() {
		return speAreaCode;
	}

	public void setSpeAreaCode(String speAreaCode) {
		this.speAreaCode = speAreaCode;
	}

	public String getSpeDetailEnable() {
		return speDetailEnable;
	}

	public void setSpeDetailEnable(String speDetailEnable) {
		this.speDetailEnable = speDetailEnable;
	}

	public String getTransportTemplate() {
		return transportTemplate;
	}

	public void setTransportTemplate(String transportTemplate) {
		this.transportTemplate = transportTemplate;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public double getExpressStart() {
		return expressStart;
	}

	public void setExpressStart(double expressStart) {
		this.expressStart = expressStart;
	}

	public double getExpressPostage() {
		return expressPostage;
	}

	public void setExpressPostage(double expressPostage) {
		this.expressPostage = expressPostage;
	}

	public double getExpressPlus() {
		return expressPlus;
	}

	public void setExpressPlus(double expressPlus) {
		this.expressPlus = expressPlus;
	}

	public double getExpressPostagePlus() {
		return expressPostagePlus;
	}

	public void setExpressPostagePlus(double expressPostagePlus) {
		this.expressPostagePlus = expressPostagePlus;
	}

	public double getSpeExpressStart() {
		return speExpressStart;
	}

	public void setSpeExpressStart(double speExpressStart) {
		this.speExpressStart = speExpressStart;
	}

	public double getSpeExpressPostage() {
		return speExpressPostage;
	}

	public void setSpeExpressPostage(double speExpressPostage) {
		this.speExpressPostage = speExpressPostage;
	}

	public double getSpeExpressPlus() {
		return speExpressPlus;
	}

	public void setSpeExpressPlus(double speExpressPlus) {
		this.speExpressPlus = speExpressPlus;
	}

	public double getSpeExpressPostagePlus() {
		return speExpressPostagePlus;
	}

	public void setSpeExpressPostagePlus(double speExpressPostagePlus) {
		this.speExpressPostagePlus = speExpressPostagePlus;
	}

	public double getProductWeight() {
		return productWeight;
	}

	public void setProductWeight(double productWeight) {
		this.productWeight = productWeight;
	}

	public double getProductVolume() {
		return productVolume;
	}

	public void setProductVolume(double productVolume) {
		this.productVolume = productVolume;
	}

}
