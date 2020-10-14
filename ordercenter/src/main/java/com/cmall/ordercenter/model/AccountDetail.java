package com.cmall.ordercenter.model;

import java.math.BigDecimal;

/**
 * 结算单对应订单(付款成功订单、退款成功订单)的详细信息 Date: 2013-10-30 下午4:04:07
 * 
 * @author jack
 */
public class AccountDetail {
	/**
	 * 结算单编号
	 */
	private String account_code = "";
	
	/**
	 * 订单编号
	 */
	private String order_code = "";
	
	/**
	 * 订单金额
	 */
	private BigDecimal order_money = new BigDecimal(0.00);
	
	/**
	 * 付款完成时间
	 */
	private String order_time = "";
	
	/**
	 * 退货订单号
	 */
	private String return_code = "";
	
	/**
	 * 退货手续费金额 
	 */
	private BigDecimal return_cost = new BigDecimal(0.00);
	
	/**
	 *退货金额 
	 * 
	 */
	private BigDecimal return_money = new BigDecimal(0.00);
	
	/**
	 * 退款完成时间
	 */
	private String return_time = "";
	
	/**
	 * 支付方式
	 */
	private String pay_type = "";
	
	/**
	 * 支付平台手续费
	 */
	private BigDecimal pay_cost = new BigDecimal(0.00);
	
	/**
	 * 商城分成金额
	 */
	private BigDecimal storeshare_money = new BigDecimal(0.00);
	
	/**
	 * 结算金额
	 */
	private BigDecimal account_money = new BigDecimal(0.00);
	
	/**
	 * 结算日期
	 */
	private String account_time = "";
	
	/**
	 * 备注
	 */
	private String remark = "";

	/**
	 * 获取account_code.
	 * 
	 * @return account_code
	 */
	public String getAccount_code() {
		return account_code;
	}

	/**
	 * 设置account_code.
	 * 
	 * @param account_code
	 */
	public void setAccount_code(String account_code) {
		this.account_code = account_code;
	}

	/**
	 * 获取order_code.
	 * 
	 * @return order_code
	 */
	public String getOrder_code() {
		return order_code;
	}

	/**
	 * 设置order_code.
	 * 
	 * @param order_code
	 */
	public void setOrder_code(String order_code) {
		this.order_code = order_code;
	}

	/**
	 * 获取order_money.
	 * 
	 * @return order_money
	 */
	public BigDecimal getOrder_money() {
		return order_money;
	}

	/**
	 * 设置order_money.
	 * 
	 * @param order_money
	 */
	public void setOrder_money(BigDecimal order_money) {
		this.order_money = order_money;
	}

	/**
	 * 获取order_time.
	 * 
	 * @return order_time
	 */
	public String getOrder_time() {
		return order_time;
	}

	/**
	 * 设置order_time.
	 * 
	 * @param order_time
	 */
	public void setOrder_time(String order_time) {
		this.order_time = order_time;
	}

	/**
	 * 获取return_code.
	 * 
	 * @return return_code
	 */
	public String getReturn_code() {
		return return_code;
	}

	/**
	 * 设置return_code.
	 * 
	 * @param return_code
	 */
	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}

	/**
	 * 获取退货金额 .
	 * 
	 * @return return_money
	 */
	public BigDecimal getReturn_money() {
		return return_money;
	}

	/**
	 * 设置退货金额 .
	 * 
	 * @param return_money
	 */
	public void setReturn_money(BigDecimal return_money) {
		this.return_money = return_money;
	}

	/**
	 * 获取return_time.
	 * 
	 * @return return_time
	 */
	public String getReturn_time() {
		return return_time;
	}

	/**
	 * 设置return_time.
	 * 
	 * @param return_time
	 */
	public void setReturn_time(String return_time) {
		this.return_time = return_time;
	}

	/**
	 * 获取pay_type.
	 * 
	 * @return pay_type
	 */
	public String getPay_type() {
		return pay_type;
	}

	/**
	 * 设置pay_type.
	 * 
	 * @param pay_type
	 */
	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}

	/**
	 * 获取pay_cost.
	 * 
	 * @return pay_cost
	 */
	public BigDecimal getPay_cost() {
		return pay_cost;
	}

	/**
	 * 设置pay_cost.
	 * 
	 * @param pay_cost
	 */
	public void setPay_cost(BigDecimal pay_cost) {
		this.pay_cost = pay_cost;
	}

	/**
	 * 获取storeshare_money.
	 * 
	 * @return storeshare_money
	 */
	public BigDecimal getStoreshare_money() {
		return storeshare_money;
	}

	/**
	 * 设置storeshare_money.
	 * 
	 * @param storeshare_money
	 */
	public void setStoreshare_money(BigDecimal storeshare_money) {
		this.storeshare_money = storeshare_money;
	}

	/**
	 * 获取account_money.
	 * 
	 * @return account_money
	 */
	public BigDecimal getAccount_money() {
		return account_money;
	}

	/**
	 * 设置account_money.
	 * 
	 * @param account_money
	 */
	public void setAccount_money(BigDecimal account_money) {
		this.account_money = account_money;
	}

	/**
	 * 获取account_time.
	 * 
	 * @return account_time
	 */
	public String getAccount_time() {
		return account_time;
	}

	/**
	 * 设置account_time.
	 * 
	 * @param account_time
	 */
	public void setAccount_time(String account_time) {
		this.account_time = account_time;
	}

	/**
	 * 获取remark.
	 * 
	 * @return remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * 设置remark.
	 * 
	 * @param remark
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public BigDecimal getReturn_cost() {
		return return_cost;
	}

	public void setReturn_cost(BigDecimal return_cost) {
		this.return_cost = return_cost;
	}
	
}
