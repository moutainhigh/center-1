package com.cmall.groupcenter.homehas.model;

import java.math.BigDecimal;

public class RsyncModelThirdOrderDetail {

	/**
	 * 订单号
	 */
	private Long ord_id;
	
	/**
	 * 小订单序号
	 */
	private Integer ord_seq;
	
	/**
	 * 订单状态
	 */
	private String ord_stat;
	
	/**
	 * 订单金额
	 */
	private BigDecimal ord_amt = new BigDecimal(0);
	
	/**
	 * 订单实付款
	 */
	private BigDecimal ord_sf_amt = new BigDecimal(0);
	
	/**
	 * 订单优惠金额
	 */
	private BigDecimal dis_amt = new BigDecimal(0);
	
	/**
	 * 输入日期
	 */
	private Long etr_date;
	
	/**
	 * 商品售价
	 */
	private BigDecimal prc = new BigDecimal(0);
	
	/**
	 * 商品原价
	 */
	private BigDecimal org_prc = new BigDecimal(0);
	
	/**
	 * 储值金使用金额
	 */
	private BigDecimal ppc_apply_amt = new BigDecimal(0);
	
	/**
	 * 积分使用金额
	 */
	private BigDecimal accm_apply_amt = new BigDecimal(0);
	
	/**
	 * 暂存款使用金额
	 */
	private BigDecimal crdt_apply_amt = new BigDecimal(0);
	
	/**
	 * 运费
	 */
	private BigDecimal dely_fee = new BigDecimal(0);
	
	/**
	 * 收货地址
	 */
	private String rec_addr;
	
	/**
	 * 收货电话
	 */
	private String tel;
	
	/**
	 * 支付方式
	 */
	private String pay_type;
	
	/**
	 * 收货人
	 */
	private String rcver_nm;
	
	/**
	 * 是否海外购(1:是 0:否)
	 */
	private String is_hwg;
	
	/**
	 * 商品编号
	 */
	private Long good_id;
	
	/**
	 * 订购数量
	 */
	private Integer ord_qty;
	
	/**
	 * 商品名称
	 */
	private String good_nm;
	
	/**
	 * 商品颜色编号
	 */
	private Integer color_id;
	
	/**
	 * 商品颜色描述
	 */
	private String color_desc;
	
	/**
	 * 商品款式编号
	 */
	private Integer style_id;
	
	/**
	 * 商品款式描述
	 */
	private String style_desc;
	
	/**
	 * 运单号
	 */
	private String invc_id;
	
	/**
	 * 配送公司
	 */
	private String dl_cd_desc;
	
	/**
	 * 收货日期
	 */
	private Long rcv_date;
	
	/**
	 * 是否换货新单
	 */
	private String is_chg;

	public Long getOrd_id() {
		return ord_id;
	}

	public void setOrd_id(Long ord_id) {
		this.ord_id = ord_id;
	}

	public Integer getOrd_seq() {
		return ord_seq;
	}

	public void setOrd_seq(Integer ord_seq) {
		this.ord_seq = ord_seq;
	}

	public String getOrd_stat() {
		return ord_stat;
	}

	public void setOrd_stat(String ord_stat) {
		this.ord_stat = ord_stat;
	}

	public BigDecimal getOrd_amt() {
		return ord_amt;
	}

	public void setOrd_amt(BigDecimal ord_amt) {
		this.ord_amt = ord_amt;
	}

	public Long getEtr_date() {
		return etr_date;
	}

	public void setEtr_date(Long etr_date) {
		this.etr_date = etr_date;
	}

	public BigDecimal getPrc() {
		return prc;
	}

	public void setPrc(BigDecimal prc) {
		this.prc = prc;
	}

	public BigDecimal getPpc_apply_amt() {
		return ppc_apply_amt;
	}

	public void setPpc_apply_amt(BigDecimal ppc_apply_amt) {
		this.ppc_apply_amt = ppc_apply_amt;
	}

	public BigDecimal getCrdt_apply_amt() {
		return crdt_apply_amt;
	}

	public void setCrdt_apply_amt(BigDecimal crdt_apply_amt) {
		this.crdt_apply_amt = crdt_apply_amt;
	}

	public BigDecimal getDely_fee() {
		return dely_fee;
	}

	public void setDely_fee(BigDecimal dely_fee) {
		this.dely_fee = dely_fee;
	}

	public String getRec_addr() {
		return rec_addr;
	}

	public void setRec_addr(String rec_addr) {
		this.rec_addr = rec_addr;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getPay_type() {
		return pay_type;
	}

	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}

	public String getRcver_nm() {
		return rcver_nm;
	}

	public void setRcver_nm(String rcver_nm) {
		this.rcver_nm = rcver_nm;
	}

	public String getIs_hwg() {
		return is_hwg;
	}

	public void setIs_hwg(String is_hwg) {
		this.is_hwg = is_hwg;
	}

	public Long getGood_id() {
		return good_id;
	}

	public void setGood_id(Long good_id) {
		this.good_id = good_id;
	}

	public String getGood_nm() {
		return good_nm;
	}

	public void setGood_nm(String good_nm) {
		this.good_nm = good_nm;
	}

	public Integer getColor_id() {
		return color_id;
	}

	public void setColor_id(Integer color_id) {
		this.color_id = color_id;
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

	public String getStyle_desc() {
		return style_desc;
	}

	public void setStyle_desc(String style_desc) {
		this.style_desc = style_desc;
	}

	public String getInvc_id() {
		return invc_id;
	}

	public void setInvc_id(String invc_id) {
		this.invc_id = invc_id;
	}

	public String getDl_cd_desc() {
		return dl_cd_desc;
	}

	public void setDl_cd_desc(String dl_cd_desc) {
		this.dl_cd_desc = dl_cd_desc;
	}

	public Integer getOrd_qty() {
		return ord_qty;
	}

	public void setOrd_qty(Integer ord_qty) {
		this.ord_qty = ord_qty;
	}

	public BigDecimal getAccm_apply_amt() {
		return accm_apply_amt;
	}

	public void setAccm_apply_amt(BigDecimal accm_apply_amt) {
		this.accm_apply_amt = accm_apply_amt;
	}

	public BigDecimal getOrd_sf_amt() {
		return ord_sf_amt;
	}

	public void setOrd_sf_amt(BigDecimal ord_sf_amt) {
		this.ord_sf_amt = ord_sf_amt;
	}

	public BigDecimal getDis_amt() {
		return dis_amt;
	}

	public void setDis_amt(BigDecimal dis_amt) {
		this.dis_amt = dis_amt;
	}

	public BigDecimal getOrg_prc() {
		return org_prc;
	}

	public void setOrg_prc(BigDecimal org_prc) {
		this.org_prc = org_prc;
	}

	public Long getRcv_date() {
		return rcv_date;
	}

	public void setRcv_date(Long rcv_date) {
		this.rcv_date = rcv_date;
	}

	public String getIs_chg() {
		return is_chg;
	}

	public void setIs_chg(String is_chg) {
		this.is_chg = is_chg;
	}
	
	
}
