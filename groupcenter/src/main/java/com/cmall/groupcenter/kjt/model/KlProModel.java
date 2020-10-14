package com.cmall.groupcenter.kjt.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class KlProModel implements Serializable {

	private static final long serialVersionUID = 1L;
    /**
     * 商品前台Id
     */
	private Long goodsId;
	public Long getGoodsId() {
		return goodsId;
	}
	/**
     * 商品后台Id(貌似和上面的属性没啥区别)
     */
	private Long productId;
	/**
	 * 商品skuId
	 */
	private String skuId;
	
	/**
	 * 商品标题	
	 */
	private String title;
	/**
	 * 商品副标题
	 */
	private String subTitle;
	/**
	 * 商品短标题
	 */
	private String shortTitle;
	
	/**
	 * 品牌名称
	 */
	private String brandName;
	/**
	 * 上架状态:1上架，0下架
	 */
	private String onlineStatus;
	/**
	 * 	供货价(渠道进货价，和考拉商品价格没有关联)
	 */
	private BigDecimal price;
	/**
	 * 考拉价(非考拉促销价格)
	 */
	private BigDecimal suggestPrice;
	/**
	 * 市场指导价	
	 */
	private BigDecimal marketPrice;
	/**
	 * 商品关税税率
	 */
	private BigDecimal taxRate;
	/**
	 * 库存
	 */
	private Integer store;
	/**
	 * 商品分类	
	 */
	private String category;
	/**
	 * 商品图片		
	 */
	private String imageUrl;
	/**
	 *仓库		
	 */
	private String storage;
	/**
	 * 品牌国家名称		
	 */
	private String brandCountryName;
	/**
	 * 商品详情文本			
	 */
	private String detail;
	/**
	 * 商品属性	
	 */
	private Map<String,Object> goodsProperty = new HashMap<String,Object>();
	/**
	 * sku销售属性	
	 */
	private Map<String,Object> skuProperty = new HashMap<String,Object>();
	/**
	 * 物流属性	
	 */
	private Map<String,Object> logisticsProperty= new HashMap<String,Object>();
	/**
	 * 商品图片列表		
	 */
	private Map<String,Object> goodsImages = new HashMap<String,Object>();
	/**
	 *推荐的仓库，为某些渠道特设字段，可忽略
	 */
	private Map<String,Object> recommandStore = new HashMap<String,Object>();

	
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public String getSkuId() {
		return skuId;
	}
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	public String getShortTitle() {
		return shortTitle;
	}
	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public String getOnlineStatus() {
		return onlineStatus;
	}
	public void setOnlineStatus(String onlineStatus) {
		this.onlineStatus = onlineStatus;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getSuggestPrice() {
		return suggestPrice;
	}
	public void setSuggestPrice(BigDecimal suggestPrice) {
		this.suggestPrice = suggestPrice;
	}
	public BigDecimal getMarketPrice() {
		return marketPrice;
	}
	public void setMarketPrice(BigDecimal marketPrice) {
		this.marketPrice = marketPrice;
	}
	public BigDecimal getTaxRate() {
		return taxRate;
	}
	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}
	public Integer getStore() {
		return store;
	}
	public void setStore(Integer store) {
		this.store = store;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getStorage() {
		return storage;
	}
	public void setStorage(String storage) {
		this.storage = storage;
	}
	public String getBrandCountryName() {
		return brandCountryName;
	}
	public void setBrandCountryName(String brandCountryName) {
		this.brandCountryName = brandCountryName;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public Map<String, Object> getGoodsProperty() {
		return goodsProperty;
	}
	public void setGoodsProperty(Map<String, Object> goodsProperty) {
		this.goodsProperty = goodsProperty;
	}
	public Map<String, Object> getSkuProperty() {
		return skuProperty;
	}
	public void setSkuProperty(Map<String, Object> skuProperty) {
		this.skuProperty = skuProperty;
	}
	public Map<String, Object> getLogisticsProperty() {
		return logisticsProperty;
	}
	public void setLogisticsProperty(Map<String, Object> logisticsProperty) {
		this.logisticsProperty = logisticsProperty;
	}
	public Map<String, Object> getGoodsImages() {
		return goodsImages;
	}
	public void setGoodsImages(Map<String, Object> goodsImages) {
		this.goodsImages = goodsImages;
	}
	public Map<String, Object> getRecommandStore() {
		return recommandStore;
	}
	public void setRecommandStore(Map<String, Object> recommandStore) {
		this.recommandStore = recommandStore;
	}
	
	
	
}
