package com.cmall.groupcenter.homehas.model;

import java.math.BigDecimal;

public class RsyncModelOrderInfo {

	private String yc_update_time = "";
	private String yc_orderform_num;
	private String yc_orderform_time;
	private String yc_goods_name;
	private String yc_goods_num;
	private int yc_goods_count;
	private BigDecimal yc_cost_price;
	private BigDecimal yc_after_base_price;
	private String yc_goods_color;
	private String goods_style;
	private String yc_vipuser_num;
	private String yc_carriage_money;
	private String yc_province;
	private String yc_city;
	private String yc_area;
	private String yc_street;
	private String dlv_addr_seq;
	private String addr_2;
	private String yc_claimuser;
	private String ord_seq;
	private String yc_use_integral;
	private String crdt_apply_amt;
	private String ppc_apply_amt;
	private String accm_apply_amt;
	private String last_stat_date;
	private String chg_cd;
	private String yc_orderform_status;
	private String ord_dlv_addr_seq;
	private String org_prc;
	private String medi_mclss_id;//媒体中分类ID 写入到订单的 order_channel 字段
	private String acctf_send_schd_amt;
	
	private String send_bank_cd;//支付方式
	
	private String aq_good_id ;//惠家有商品id
	private String aq_sku_id ;//惠家有商品SKU编码
	private String receive_mobile ;//下单人手机号
	private String gift_cd ;//空 ：主品    10 : 全体赠品 20 : 商品赠品 30 : 客服赠品 40 : 加价购

	private String org_ord_id; // 原订单号，换货订单时记录原始下单的订单号
	private String web_ord_id; // 惠家有订单号
	private String web_id; // 惠家用户编号
	private String dlv_rec_mobile; //收货人手机号
	
	// 程序里面循环一下订单明细列表，取最后一个商品的订单状态
	private String last_yc_orderform_status;

	public String getAddr_2() {
		return addr_2;
	}

	public void setAddr_2(String addr_2) {
		this.addr_2 = addr_2;
	}

	public String getYc_update_time() {
		return yc_update_time;
	}

	public void setYc_update_time(String yc_update_time) {
		this.yc_update_time = yc_update_time;
	}

	public String getYc_orderform_num() {
		return yc_orderform_num;
	}

	public void setYc_orderform_num(String yc_orderform_num) {
		this.yc_orderform_num = yc_orderform_num;
	}

	public String getYc_orderform_time() {
		return yc_orderform_time;
	}

	public void setYc_orderform_time(String yc_orderform_time) {
		this.yc_orderform_time = yc_orderform_time;
	}

	public String getYc_goods_name() {
		return yc_goods_name;
	}

	public void setYc_goods_name(String yc_goods_name) {
		this.yc_goods_name = yc_goods_name;
	}

	public String getYc_goods_num() {
		return yc_goods_num;
	}

	public void setYc_goods_num(String yc_goods_num) {
		this.yc_goods_num = yc_goods_num;
	}

	public int getYc_goods_count() {
		return yc_goods_count;
	}

	public void setYc_goods_count(int yc_goods_count) {
		this.yc_goods_count = yc_goods_count;
	}

	public BigDecimal getYc_cost_price() {
		return yc_cost_price;
	}

	public void setYc_cost_price(BigDecimal yc_cost_price) {
		this.yc_cost_price = yc_cost_price;
	}

	public BigDecimal getYc_after_base_price() {
		return yc_after_base_price;
	}

	public void setYc_after_base_price(BigDecimal yc_after_base_price) {
		this.yc_after_base_price = yc_after_base_price;
	}

	public String getYc_goods_color() {
		return yc_goods_color;
	}

	public void setYc_goods_color(String yc_goods_color) {
		this.yc_goods_color = yc_goods_color;
	}

	public String getGoods_style() {
		return goods_style;
	}

	public void setGoods_style(String goods_style) {
		this.goods_style = goods_style;
	}

	public String getYc_vipuser_num() {
		return yc_vipuser_num;
	}

	public void setYc_vipuser_num(String yc_vipuser_num) {
		this.yc_vipuser_num = yc_vipuser_num;
	}

	public String getYc_carriage_money() {
		return yc_carriage_money;
	}

	public void setYc_carriage_money(String yc_carriage_money) {
		this.yc_carriage_money = yc_carriage_money;
	}

	public String getYc_province() {
		return yc_province;
	}

	public void setYc_province(String yc_province) {
		this.yc_province = yc_province;
	}

	public String getYc_city() {
		return yc_city;
	}

	public void setYc_city(String yc_city) {
		this.yc_city = yc_city;
	}

	public String getYc_area() {
		return yc_area;
	}

	public void setYc_area(String yc_area) {
		this.yc_area = yc_area;
	}

	public String getDlv_addr_seq() {
		return dlv_addr_seq;
	}

	public void setDlv_addr_seq(String dlv_addr_seq) {
		this.dlv_addr_seq = dlv_addr_seq;
	}

	public String getYc_claimuser() {
		return yc_claimuser;
	}

	public void setYc_claimuser(String yc_claimuser) {
		this.yc_claimuser = yc_claimuser;
	}

	public String getOrd_seq() {
		return ord_seq;
	}

	public void setOrd_seq(String ord_seq) {
		this.ord_seq = ord_seq;
	}

	public String getYc_use_integral() {
		return yc_use_integral;
	}

	public void setYc_use_integral(String yc_use_integral) {
		this.yc_use_integral = yc_use_integral;
	}

	public String getCrdt_apply_amt() {
		return crdt_apply_amt;
	}

	public void setCrdt_apply_amt(String crdt_apply_amt) {
		this.crdt_apply_amt = crdt_apply_amt;
	}

	public String getPpc_apply_amt() {
		return ppc_apply_amt;
	}

	public void setPpc_apply_amt(String ppc_apply_amt) {
		this.ppc_apply_amt = ppc_apply_amt;
	}

	public String getAccm_apply_amt() {
		return accm_apply_amt;
	}

	public void setAccm_apply_amt(String accm_apply_amt) {
		this.accm_apply_amt = accm_apply_amt;
	}

	public String getLast_stat_date() {
		return last_stat_date;
	}

	public void setLast_stat_date(String last_stat_date) {
		this.last_stat_date = last_stat_date;
	}

	public String getChg_cd() {
		return chg_cd;
	}

	public void setChg_cd(String chg_cd) {
		this.chg_cd = chg_cd;
	}

	public String getYc_orderform_status() {
		return yc_orderform_status;
	}

	public void setYc_orderform_status(String yc_orderform_status) {
		this.yc_orderform_status = yc_orderform_status;
	}

	public String getOrd_dlv_addr_seq() {
		return ord_dlv_addr_seq;
	}

	public void setOrd_dlv_addr_seq(String ord_dlv_addr_seq) {
		this.ord_dlv_addr_seq = ord_dlv_addr_seq;
	}

	public String getOrg_prc() {
		return org_prc;
	}

	public void setOrg_prc(String org_prc) {
		this.org_prc = org_prc;
	}

	public String getMedi_mclss_id() {
		return medi_mclss_id;
	}

	public void setMedi_mclss_id(String medi_mclss_id) {
		this.medi_mclss_id = medi_mclss_id;
	}

	public String getAcctf_send_schd_amt() {
		return acctf_send_schd_amt;
	}

	public void setAcctf_send_schd_amt(String acctf_send_schd_amt) {
		this.acctf_send_schd_amt = acctf_send_schd_amt;
	}

	public String getSend_bank_cd() {
		return send_bank_cd;
	}

	public void setSend_bank_cd(String send_bank_cd) {
		this.send_bank_cd = send_bank_cd;
	}

	public String getAq_good_id() {
		return aq_good_id;
	}

	public void setAq_good_id(String aq_good_id) {
		this.aq_good_id = aq_good_id;
	}

	public String getAq_sku_id() {
		return aq_sku_id;
	}

	public void setAq_sku_id(String aq_sku_id) {
		this.aq_sku_id = aq_sku_id;
	}

	public String getReceive_mobile() {
		return receive_mobile;
	}

	public void setReceive_mobile(String receive_mobile) {
		this.receive_mobile = receive_mobile;
	}

	public String getGift_cd() {
		return gift_cd;
	}

	public void setGift_cd(String gift_cd) {
		this.gift_cd = gift_cd;
	}

	public String getOrg_ord_id() {
		return org_ord_id;
	}

	public void setOrg_ord_id(String org_ord_id) {
		this.org_ord_id = org_ord_id;
	}

	public String getWeb_ord_id() {
		return web_ord_id;
	}

	public void setWeb_ord_id(String web_ord_id) {
		this.web_ord_id = web_ord_id;
	}

	public String getWeb_id() {
		return web_id;
	}

	public void setWeb_id(String web_id) {
		this.web_id = web_id;
	}

	public String getLast_yc_orderform_status() {
		return last_yc_orderform_status;
	}

	public void setLast_yc_orderform_status(String last_yc_orderform_status) {
		this.last_yc_orderform_status = last_yc_orderform_status;
	}

	public String getDlv_rec_mobile() {
		return dlv_rec_mobile;
	}

	public void setDlv_rec_mobile(String dlv_rec_mobile) {
		this.dlv_rec_mobile = dlv_rec_mobile;
	}

	public String getYc_street() {
		return yc_street;
	}

	public void setYc_street(String yc_street) {
		this.yc_street = yc_street;
	}
	
}
