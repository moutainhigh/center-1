package com.cmall.ordercenter.model;

/**
 * 退款扩展信息
 * 
 * @author jlin
 *
 */
public class ReturnMoneyExtStr extends ReturnMoneyExt {

	public String getOrder_payed_money1() {
		return super.getOrder_payed_money().toString();
	}
	
	public String getOrder_returned_money1() {
		return super.getOrder_returned_money().toString();
	}
	
	public String getOrder_rr_money1() {
		return super.getOrder_rr_money().toString();
	}
	
	public String getOrder_rr_transport_money1() {
		return super.getOrder_rr_transport_money().toString();
	}
	
	public String getOrder_current_money1() {
		return super.getOrder_current_money().toString();
	}
	
	public String getOrder_money1() {
		return super.getOrder_money().toString();
	}
	
}
