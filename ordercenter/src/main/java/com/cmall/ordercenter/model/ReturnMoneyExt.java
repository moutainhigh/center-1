package com.cmall.ordercenter.model;

import java.math.BigDecimal;

/**
 * 退款扩展信息
 * 
 * @author jlin
 *
 */
public class ReturnMoneyExt {

	//合并支付订单号
	private String big_order_code = "";
	//支付网关订单号
	private String paygate_order_code = "";
	//交易流水号
	private String pay_type = "";
	
	//支付方式
	private String pay_sequenceid = "";
	
	//支付方式名称
	private String pay_type_name = "";
	
	//已付金额
	private BigDecimal order_payed_money=BigDecimal.ZERO;
	
	//已退金额
	private BigDecimal order_returned_money=BigDecimal.ZERO;
	
	//应退商品金额
	private BigDecimal order_rr_money=BigDecimal.ZERO;
	
	//应退运费
	private BigDecimal order_rr_transport_money=BigDecimal.ZERO;
	
	//本次退款金额 order_rr_money+运费
	private BigDecimal order_current_money=BigDecimal.ZERO;
	
	//订单金额
	private BigDecimal order_money=BigDecimal.ZERO;
	
	//收款账号
	private String php_code="";
	
	// 支付银行
	private String pay_bank="";

	public String getPay_bank() {
		return pay_bank;
	}

	public void setPay_bank(String pay_bank) {
		this.pay_bank = pay_bank;
	}

	public String getBig_order_code() {
		return big_order_code;
	}

	public void setBig_order_code(String big_order_code) {
		this.big_order_code = big_order_code;
	}

	public String getPay_type() {
		return pay_type;
	}

	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}

	public String getPay_sequenceid() {
		return pay_sequenceid;
	}

	public void setPay_sequenceid(String pay_sequenceid) {
		this.pay_sequenceid = pay_sequenceid;
	}

	public BigDecimal getOrder_payed_money() {
		return order_payed_money;
	}

	public void setOrder_payed_money(BigDecimal order_payed_money) {
		this.order_payed_money = order_payed_money;
	}

	public BigDecimal getOrder_returned_money() {
		return order_returned_money;
	}

	public void setOrder_returned_money(BigDecimal order_returned_money) {
		this.order_returned_money = order_returned_money;
	}

	public BigDecimal getOrder_rr_money() {
		return order_rr_money;
	}

	public void setOrder_rr_money(BigDecimal order_rr_money) {
		this.order_rr_money = order_rr_money;
	}

	public BigDecimal getOrder_current_money() {
		return order_current_money;
	}

	public void setOrder_current_money(BigDecimal order_current_money) {
		this.order_current_money = order_current_money;
	}

	public BigDecimal getOrder_money() {
		return order_money;
	}

	public void setOrder_money(BigDecimal order_money) {
		this.order_money = order_money;
	}

	public BigDecimal getOrder_rr_transport_money() {
		return order_rr_transport_money;
	}

	public void setOrder_rr_transport_money(BigDecimal order_rr_transport_money) {
		this.order_rr_transport_money = order_rr_transport_money;
	}

	public String getPay_type_name() {
		return pay_type_name;
	}

	public void setPay_type_name(String pay_type_name) {
		this.pay_type_name = pay_type_name;
	}

	public String getPhp_code() {
		return php_code;
	}

	public void setPhp_code(String php_code) {
		this.php_code = php_code;
	}

	public String getPaygate_order_code() {
		return paygate_order_code;
	}

	public void setPaygate_order_code(String paygate_order_code) {
		this.paygate_order_code = paygate_order_code;
	}

	
}
