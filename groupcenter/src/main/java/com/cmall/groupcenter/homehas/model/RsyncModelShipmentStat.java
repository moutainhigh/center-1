package com.cmall.groupcenter.homehas.model;

/**
 * 同步配送状态返回参数
 * 
 * @author srnpr
 * 
 */
public class RsyncModelShipmentStat {

	private String invc_id = "";
	private String ord_id = ""; 
	private String ord_seq = "";
	private String good_id = "";
	private String color_id = "";
	private String style_id = "";
	private String rcpt_no = "";
	private String cod_stat_cd = "";
	private String medi_mclss_id = "";//媒体中分类ID
	private String stat_date="";//状态更新日期 20151112 gaoy add

	public String getInvc_id() {
		return invc_id;
	}

	public void setInvc_id(String invc_id) {
		this.invc_id = invc_id;
	}

	public String getOrd_id() {
		return ord_id;
	}

	public void setOrd_id(String ord_id) {
		this.ord_id = ord_id;
	}

	public String getRcpt_no() {
		return rcpt_no;
	}

	public void setRcpt_no(String rcpt_no) {
		this.rcpt_no = rcpt_no;
	}

	public String getCod_stat_cd() {
		return cod_stat_cd;
	}

	public void setCod_stat_cd(String cod_stat_cd) {
		this.cod_stat_cd = cod_stat_cd;
	}

	public String getStat_date() {
		return stat_date;
	}

	public void setStat_date(String stat_date) {
		this.stat_date = stat_date;
	}

	public String getMedi_mclss_id() {
		return medi_mclss_id;
	}

	public void setMedi_mclss_id(String medi_mclss_id) {
		this.medi_mclss_id = medi_mclss_id;
	}

	public String getOrd_seq() {
		return ord_seq;
	}

	public void setOrd_seq(String ord_seq) {
		this.ord_seq = ord_seq;
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

}
