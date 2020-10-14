package com.cmall.groupcenter.homehas.model;

import java.math.BigDecimal;

/**
 * 储备金、暂存款子订单信息
 * @author pang_jhui
 *
 */
public class AccountSubOrderInfo {
	
	/*子订单编号*/
	private String app_child_ord_id = "";
	
	/*商品编号*/
	private String good_id = "";
	
	/*商品名称*/
	private String good_nm = "";
	
	/*商品价格*/
	private BigDecimal good_price = BigDecimal.ZERO;
	
	/*商品款式*/
	private String good_sytle = "";
	
	/*子订单积分*/
	private BigDecimal child_accm_amt = BigDecimal.ZERO;
	
	/*子订单暂存款*/
	private BigDecimal child_crdt_amt = BigDecimal.ZERO;
	
	/*子订单储值金*/
	private BigDecimal child_ppc_amt = BigDecimal.ZERO;

	public String getApp_child_ord_id() {
		return app_child_ord_id;
	}

	public void setApp_child_ord_id(String app_child_ord_id) {
		this.app_child_ord_id = app_child_ord_id;
	}

	public String getGood_id() {
		return good_id;
	}

	public void setGood_id(String good_id) {
		this.good_id = good_id;
	}

	public String getGood_nm() {
		return good_nm;
	}

	public void setGood_nm(String good_nm) {
		this.good_nm = good_nm;
	}

	public BigDecimal getGood_price() {
		return good_price;
	}

	public void setGood_price(BigDecimal good_price) {
		this.good_price = good_price;
	}

	public String getGood_sytle() {
		return good_sytle;
	}

	public void setGood_sytle(String good_sytle) {
		this.good_sytle = good_sytle;
	}

	public BigDecimal getChild_accm_amt() {
		return child_accm_amt;
	}

	public void setChild_accm_amt(BigDecimal child_accm_amt) {
		this.child_accm_amt = child_accm_amt;
	}

	public BigDecimal getChild_crdt_amt() {
		return child_crdt_amt;
	}

	public void setChild_crdt_amt(BigDecimal child_crdt_amt) {
		this.child_crdt_amt = child_crdt_amt;
	}

	public BigDecimal getChild_ppc_amt() {
		return child_ppc_amt;
	}

	public void setChild_ppc_amt(BigDecimal child_ppc_amt) {
		this.child_ppc_amt = child_ppc_amt;
	}
	

}
