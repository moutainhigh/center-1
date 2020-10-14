package com.cmall.groupcenter.homehas.model;

import java.io.Serializable;

public class RsyncModelReturnGoods implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ord_id;
	private String good_id;
	private String color_id;
	private String style_id;
	private String rtn_qty; //退货数量
	private String cod_stat_cd;
	private String rtn_cnfm_date; //退货入库确定时间  格式如：2018-08-14 15:45:34
	
	public String getOrd_id() {
		return ord_id;
	}
	public void setOrd_id(String ord_id) {
		this.ord_id = ord_id;
	}
	public String getGood_id() {
		return good_id;
	}
	public void setGood_id(String good_id) {
		this.good_id = good_id;
	}
	public String getColor_id() {
		return color_id;
	}
	public void setColor_id(String color_id) {
		this.color_id = color_id;
	}
	public String getStyle_id() {
		return style_id;
	}
	public void setStyle_id(String style_id) {
		this.style_id = style_id;
	}
	public String getRtn_qty() {
		return rtn_qty;
	}
	public void setRtn_qty(String rtn_qty) {
		this.rtn_qty = rtn_qty;
	}
	public String getCod_stat_cd() {
		return cod_stat_cd;
	}
	public void setCod_stat_cd(String cod_stat_cd) {
		this.cod_stat_cd = cod_stat_cd;
	}
	public String getRtn_cnfm_date() {
		return rtn_cnfm_date;
	}
	public void setRtn_cnfm_date(String rtn_cnfm_date) {
		this.rtn_cnfm_date = rtn_cnfm_date;
	}
	
}
