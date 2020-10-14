package com.cmall.groupcenter.hserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 商品状态推送实体类
 */
public class ProductStatus {
	@JsonProperty("GOOD_ID")
	private String goodId = "";
	@JsonProperty("COLOR_ID")
	private String colorId = "";
	@JsonProperty("STYLE_ID")
	private String styleId = "";
	@JsonProperty("SITE_NO")
	private String siteNo = "";
	@JsonProperty("SALE_YN")
	private String saleYn = "";
	@JsonProperty("CHANGE_CD")
	private String changeCd = "";
	
	public String getGoodId() {
		return goodId;
	}
	public void setGoodId(String goodId) {
		this.goodId = goodId;
	}
	public String getColorId() {
		return colorId;
	}
	public void setColorId(String colorId) {
		this.colorId = colorId;
	}
	public String getStyleId() {
		return styleId;
	}
	public void setStyleId(String styleId) {
		this.styleId = styleId;
	}
	public String getSiteNo() {
		return siteNo;
	}
	public void setSiteNo(String siteNo) {
		this.siteNo = siteNo;
	}
	public String getSaleYn() {
		return saleYn;
	}
	public void setSaleYn(String saleYn) {
		this.saleYn = saleYn;
	}
	public String getChangeCd() {
		return changeCd;
	}
	public void setChangeCd(String changeCd) {
		this.changeCd = changeCd;
	}
	
}
