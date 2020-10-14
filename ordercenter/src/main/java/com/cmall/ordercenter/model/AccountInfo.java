package com.cmall.ordercenter.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 结算主信息(结算时使用)
 * 
 * @author jack
 * @version 1.0
 */
public class AccountInfo {
	/**
	 * 结算单编号
	 */
	private String account_code = "";

	/**
	 * 结算总金额
	 */
	private BigDecimal account_amount = new BigDecimal(0.00);

	/**
	 * 退货金额
	 */
	private BigDecimal return_amount = new BigDecimal(0.00);

	/**
	 * 手续费金额
	 */
	private BigDecimal procedure_amount = new BigDecimal(0.00);

	/**
	 * 商城分成金额
	 */
	private BigDecimal storeshare_amount = new BigDecimal(0.00);
	/***
	 * 结算金额（商家结算金额）
	 */
	private BigDecimal sellershare_amount = new BigDecimal(0.00);

	/**
	 * 卖家编号(商家编号)
	 */
	private String seller_code = "";

	/**
	 * 结算起始日期
	 */
	private String start_time = "'";

	/**
	 * 结算结束日期
	 */
	private String end_time = "";

	/**
	 * 结算单状态
	 */
	private String account_status = "";

	/**
	 * 结算单创建时间
	 */
	private String create_time = "";

	/**
	 * 结算单创建者
	 */
	private String create_user = "";
	
	/**
	 *结算的每张订单的详细信息
	 */
	private List<AccountDetail> list = new ArrayList<AccountDetail>();
	
	/**
	 *结算的每张退款订单的详细信息 
	 */
	private List<AccountDetail> reOrders= new ArrayList<AccountDetail>();
	
	/**
	 * 获取结算单编号.
	 * 
	 * @return account_code
	 */
	public String getAccount_code() {
		return account_code;
	}

	/**
	 * 设置结算单编号.
	 * 
	 * @param account_code
	 */
	public void setAccount_code(String account_code) {
		this.account_code = account_code;
	}

	/**
	 * 获取结算总金额.
	 * 
	 * @return account_amount
	 */
	public BigDecimal getAccount_amount() {
		return account_amount;
	}

	/**
	 * 设置结算总金额.
	 * 
	 * @param account_amount
	 */
	public void setAccount_amount(BigDecimal account_amount) {
		this.account_amount = account_amount;
	}

	/**
	 * 获取退货金额.
	 * 
	 * @return return_amount
	 */
	public BigDecimal getReturn_amount() {
		return return_amount;
	}

	/**
	 * 设置退货金额.
	 * 
	 * @param return_amount
	 */
	public void setReturn_amount(BigDecimal return_amount) {
		this.return_amount = return_amount;
	}

	/**
	 * 获取手续费金额.
	 * 
	 * @return procedure_amount
	 */
	public BigDecimal getProcedure_amount() {
		return procedure_amount;
	}

	/**
	 * 设置手续费金额.
	 * 
	 * @param procedure_amount
	 */
	public void setProcedure_amount(BigDecimal procedure_amount) {
		this.procedure_amount = procedure_amount;
	}

	/**
	 * 获取商城分成金额.
	 * 
	 * @return storeshare_amount
	 */
	public BigDecimal getStoreshare_amount() {
		return storeshare_amount;
	}

	/**
	 * 设置商城分成金额.
	 * 
	 * @param storeshare_amount
	 */
	public void setStoreshare_amount(BigDecimal storeshare_amount) {
		this.storeshare_amount = storeshare_amount;
	}

	/**
	 * 获取结算金额（商家结算金额）.
	 * 
	 * @return sellershare_amount
	 */
	public BigDecimal getSellershare_amount() {
		return sellershare_amount;
	}

	/**
	 * 设置结算金额（商家结算金额）.
	 * 
	 * @param sellershare_amount
	 */
	public void setSellershare_amount(BigDecimal sellershare_amount) {
		this.sellershare_amount = sellershare_amount;
	}

	/**
	 * 获取卖家编号(商家编号).
	 * 
	 * @return seller_code
	 */
	public String getSeller_code() {
		return seller_code;
	}

	/**
	 * 设置卖家编号(商家编号).
	 * 
	 * @param seller_code
	 */
	public void setSeller_code(String seller_code) {
		this.seller_code = seller_code;
	}

	/**
	 * 获取结算起始日期.
	 * 
	 * @return start_time
	 */
	public String getStart_time() {
		return start_time;
	}

	/**
	 * 设置结算起始日期.
	 * 
	 * @param start_time
	 */
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	/**
	 * 获取结算结束日期.
	 * 
	 * @return end_time
	 */
	public String getEnd_time() {
		return end_time;
	}

	/**
	 * 设置结算结束日期.
	 * 
	 * @param end_time
	 */
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	/**
	 * 获取结算单状态.
	 * 
	 * @return account_status
	 */
	public String getAccount_status() {
		return account_status;
	}

	/**
	 * 设置结算单状态.
	 * 
	 * @param account_status
	 */
	public void setAccount_status(String account_status) {
		this.account_status = account_status;
	}

	/**
	 * 获取结算单创建时间.
	 * 
	 * @return create_time
	 */
	public String getCreate_time() {
		return create_time;
	}

	/**
	 * 设置结算单创建时间.
	 * 
	 * @param create_time
	 */
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	/**
	 * 获取结算单创建者.
	 * 
	 * @return create_user
	 */
	public String getCreate_user() {
		return create_user;
	}

	/**
	 * 设置结算单创建者.
	 * 
	 * @param create_user
	 */
	public void setCreate_user(String create_user) {
		this.create_user = create_user;
	}

	public List<AccountDetail> getList() {
		return list;
	}

	public void setList(List<AccountDetail> list) {
		this.list = list;
	}
	/**
	 * 获取退款订单Map<退货单号,accountDetail>
	 * 
	 * @return reOrders
	 */
	public List<AccountDetail> getReOrders() {
		return reOrders;
	}

	public void setReOrders(List<AccountDetail> reOrders) {
		this.reOrders = reOrders;
	}

}
