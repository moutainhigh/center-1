package com.cmall.groupcenter.homehas.model;

import java.math.BigDecimal;

public class RsyncModelThirdOrder {

	/**
	 * 商品售价
	 */
	private BigDecimal prc = new BigDecimal(0);
	
	/**
	 * 商品原价
	 */
	private BigDecimal org_prc = new BigDecimal(0);
	
	/**
	 * 订单金额
	 */
	private BigDecimal ord_amt = new BigDecimal(0);
	
	/**
	 * 订单实付款
	 */
	private BigDecimal ord_sf_amt = new BigDecimal(0);
	
	/**
	 * 订单号
	 */
	private Long ord_id;
	
	/**
	 * 小订单序号
	 */
	private Integer ord_seq;
	
	/**
	 * 商品编号
	 */
	private Long good_id;
	
	/**
	 * 商品款式描述
	 */
	private String style_desc;
	
	/**
	 * 输入日期
	 */
	private Long etr_date;
	
	/**
	 * 订购数量
	 */
	private Integer ord_qty;
	
	/**
	 * 商品颜色编号
	 */
	private Integer color_id;
	
	/**
	 * 订单状态
	 */
	private String ORD_STAT;
	
	/**
	 * 商品名称
	 */
	private String good_nm;
	
	/**
	 * 商品颜色描述
	 */
	private String color_desc;
	
	/**
	 * 商品款式编号
	 */
	private Integer style_id;
	
	/**
	 * 是否海外购
	 */
	private String is_hwg;
	
	/**
	 * 订单使用积分数
	 */
	private Integer accm_apply_amt;
	
	/**
	 * 订单支付方式 COD是货到付款
	 */
	private String pay_type;
	
	/**
	 * 是否换货新单
	 */
	private String is_chg;

	public BigDecimal getPrc() {
		return prc;
	}

	public void setPrc(BigDecimal prc) {
		this.prc = prc;
	}

	public BigDecimal getOrd_amt() {
		return ord_amt;
	}

	public void setOrd_amt(BigDecimal ord_amt) {
		this.ord_amt = ord_amt;
	}

	public BigDecimal getOrd_sf_amt() {
		return ord_sf_amt;
	}

	public void setOrd_sf_amt(BigDecimal ord_sf_amt) {
		this.ord_sf_amt = ord_sf_amt;
	}

	public Long getOrd_id() {
		return ord_id;
	}

	public void setOrd_id(Long ord_id) {
		this.ord_id = ord_id;
	}

	public Long getGood_id() {
		return good_id;
	}

	public void setGood_id(Long good_id) {
		this.good_id = good_id;
	}

	public String getStyle_desc() {
		return style_desc;
	}

	public void setStyle_desc(String style_desc) {
		this.style_desc = style_desc;
	}

	public Long getEtr_date() {
		return etr_date;
	}

	public void setEtr_date(Long etr_date) {
		this.etr_date = etr_date;
	}

	public Integer getOrd_qty() {
		return ord_qty;
	}

	public void setOrd_qty(Integer ord_qty) {
		this.ord_qty = ord_qty;
	}

	public Integer getColor_id() {
		return color_id;
	}

	public void setColor_id(Integer color_id) {
		this.color_id = color_id;
	}

	public String getORD_STAT() {
		return ORD_STAT;
	}

	public void setORD_STAT(String oRD_STAT) {
		ORD_STAT = oRD_STAT;
	}

	public String getGood_nm() {
		return good_nm;
	}

	public void setGood_nm(String good_nm) {
		this.good_nm = good_nm;
	}

	public String getColor_desc() {
		return color_desc;
	}

	public void setColor_desc(String color_desc) {
		this.color_desc = color_desc;
	}

	public Integer getStyle_id() {
		return style_id;
	}

	public void setStyle_id(Integer style_id) {
		this.style_id = style_id;
	}

	public Integer getOrd_seq() {
		return ord_seq;
	}

	public void setOrd_seq(Integer ord_seq) {
		this.ord_seq = ord_seq;
	}

	public String getIs_hwg() {
		return is_hwg;
	}

	public void setIs_hwg(String is_hwg) {
		this.is_hwg = is_hwg;
	}

	public Integer getAccm_apply_amt() {
		return accm_apply_amt;
	}

	public void setAccm_apply_amt(Integer accm_apply_amt) {
		this.accm_apply_amt = accm_apply_amt;
	}

	public String getPay_type() {
		return pay_type;
	}

	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}

	public BigDecimal getOrg_prc() {
		return org_prc;
	}

	public void setOrg_prc(BigDecimal org_prc) {
		this.org_prc = org_prc;
	}

	public String getIs_chg() {
		return is_chg;
	}

	public void setIs_chg(String is_chg) {
		this.is_chg = is_chg;
	}
	
	
}
