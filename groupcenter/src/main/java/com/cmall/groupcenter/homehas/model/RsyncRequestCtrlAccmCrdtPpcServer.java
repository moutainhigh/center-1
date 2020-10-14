package com.cmall.groupcenter.homehas.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 积分、暂存款、储值金使用
 */
public class RsyncRequestCtrlAccmCrdtPpcServer implements IRsyncRequest {

	/** 调用子系统 */
	private String subsystem = "app";
	/** 调用子系统 */
	private String account = "app";
	/** 操作类型:占用:C 取消:D使用:U 退货:R*/
	private String crud_flag = "";
	/** 客户编号 */
	private String cust_id = "";
	/** 客户名称 */
	private String cust_nm = "";
	/** 地址 */
	private String address = "";
	/** 订单编号 */
	private String app_ord_id = "";
	/** 总积分 */
	private BigDecimal accm_amt = BigDecimal.ZERO;
	/** 总暂存款 */
	private BigDecimal crdt_amt = BigDecimal.ZERO;
	/** 总储备金 */
	private BigDecimal ppc_amt = BigDecimal.ZERO;
	/** 总惠币金额*/
	private BigDecimal hcoin_amt = BigDecimal.ZERO;
	/** 处理惠币类型 10 ：正式，20：预估*/
	private String hcoin_stat_cd = "20";

	/** 子订单列表 */
	private List<ChildOrder> orders = new ArrayList<ChildOrder>();

	
	public BigDecimal getHcoin_amt() {
		return hcoin_amt;
	}

	public void setHcoin_amt(BigDecimal hcoin_amt) {
		this.hcoin_amt = hcoin_amt;
	}
	
	public String getSubsystem() {
		return subsystem;
	}

	public void setSubsystem(String subsystem) {
		this.subsystem = subsystem;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

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

	public List<ChildOrder> getOrders() {
		return orders;
	}

	public void setOrders(List<ChildOrder> orders) {
		this.orders = orders;
	}

	public String getHcoin_stat_cd() {
		return hcoin_stat_cd;
	}

	public void setHcoin_stat_cd(String hcoin_stat_cd) {
		this.hcoin_stat_cd = hcoin_stat_cd;
	}

	/**
	 * 子订单信息
	 */
	public static class ChildOrder {
		/** 子订单编号 */
		private String app_child_ord_id = "";
		/** 商品编号 */
		private String good_id = "";
		/** 商品名称 */
		private String good_nm = "";
		/** 商品价格 */
		private BigDecimal good_price = BigDecimal.ZERO;
		/** 商品款式 */
		private String good_sytle = "";
		/** 子订单积分 */
		private BigDecimal child_accm_amt = BigDecimal.ZERO;
		/** 子订单暂存款 */
		private BigDecimal child_crdt_amt = BigDecimal.ZERO;
		/** 子订单储值金 */
		private BigDecimal child_ppc_amt = BigDecimal.ZERO;
		/** 子订单惠币 */
		private BigDecimal child_hcoin_amt = BigDecimal.ZERO;
		

		public BigDecimal getChild_hcoin_amt() {
			return child_hcoin_amt;
		}

		public void setChild_hcoin_amt(BigDecimal child_hcoin_amt) {
			this.child_hcoin_amt = child_hcoin_amt;
		}

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
}
