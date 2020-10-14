package com.cmall.groupcenter.homehas.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 储备金、暂存款（占用 取消 使用）
 * @author pang_jhui
 *
 */
public class RsyncEmployAccountRequest implements IRsyncRequest {
	
	/*调用子系统*/
	private String subsystem = "app";
	
	/*操作类型:占用:C 取消:D使用:U*/
	private String crud_flag = "";
	
	/*客户编号*/
	private String cust_id = "";
	
	/*客户名称*/
	private String cust_nm = "";
	
	/*地址*/
	private String address = "";
	
	/*订单编号*/
	private String app_ord_id = "";
	
	/*总积分*/
	private BigDecimal accm_amt = BigDecimal.ZERO;
	
	/*总暂存款*/
	private BigDecimal crdt_amt = BigDecimal.ZERO; 
	
	/*总储备金*/
	private BigDecimal ppc_amt =  BigDecimal.ZERO;
	
	/*子订单列表*/
	private List<AccountSubOrderInfo> orders = new ArrayList<AccountSubOrderInfo>();

	public String getCrud_flag() {
		return crud_flag;
	}

	public void setCrud_flag(String crud_flag) {
		this.crud_flag = crud_flag;
	}

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	public String getCust_nm() {
		return cust_nm;
	}

	public void setCust_nm(String cust_nm) {
		this.cust_nm = cust_nm;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getApp_ord_id() {
		return app_ord_id;
	}

	public void setApp_ord_id(String app_ord_id) {
		this.app_ord_id = app_ord_id;
	}

	public BigDecimal getAccm_amt() {
		return accm_amt;
	}

	public void setAccm_amt(BigDecimal accm_amt) {
		this.accm_amt = accm_amt;
	}

	public BigDecimal getCrdt_amt() {
		return crdt_amt;
	}

	public void setCrdt_amt(BigDecimal crdt_amt) {
		this.crdt_amt = crdt_amt;
	}

	public BigDecimal getPpc_amt() {
		return ppc_amt;
	}

	public void setPpc_amt(BigDecimal ppc_amt) {
		this.ppc_amt = ppc_amt;
	}

	public String getSubsystem() {
		return subsystem;
	}

	public void setSubsystem(String subsystem) {
		this.subsystem = subsystem;
	}

	public List<AccountSubOrderInfo> getOrders() {
		return orders;
	}

	public void setOrders(List<AccountSubOrderInfo> orders) {
		this.orders = orders;
	}
}
