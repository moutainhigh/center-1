package com.cmall.groupcenter.homehas.model;

/**
 * 同步商品状态至LD商品列表对象
 * @author ligj
 *
 */
public class RsyncModelProductStatus {

	/**
	 * 商品编号
	 */
	private String good_id;
	/**
	 * 款式ID
	 */
	private String style_id;
	/**
	 * 颜色ID
	 */
	private String color_id;
	/**
	 * Y-上架  N-下架
	 */
	private String sale_yn;
	public String getGood_id() {
		return good_id;
	}
	public void setGood_id(String good_id) {
		this.good_id = good_id;
	}
	public String getStyle_id() {
		return style_id;
	}
	public void setStyle_id(String style_id) {
		this.style_id = style_id;
	}
	public String getColor_id() {
		return color_id;
	}
	public void setColor_id(String color_id) {
		this.color_id = color_id;
	}
	public String getSale_yn() {
		return sale_yn;
	}
	public void setSale_yn(String sale_yn) {
		this.sale_yn = sale_yn;
	}
	
}
