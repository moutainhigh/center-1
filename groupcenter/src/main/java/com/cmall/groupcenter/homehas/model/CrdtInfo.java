package com.cmall.groupcenter.homehas.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class CrdtInfo {
	@ZapcomApi(value = "暂存款金额")
	private String crdt_amt = "";
	
	@ZapcomApi(value="暂存款产生原因编码")
	private String crdt_cd;
	
	@ZapcomApi(value = "暂存款产生原因描述")
	private String crdt_desc = "";
	
	@ZapcomApi(value = "暂存款发生日期")
	private String crdt_cnfm_date;
	
	@ZapcomApi(value = "家有订单号")
	private String crdt_rel_id = "";
	
	@ZapcomApi(value="惠家有订单号")
	private String app_ord_id;

	@ZapcomApi(value="惠家有子订单号")
	private String app_child_ord_id;
	

	public String getCrdt_amt() {
		return crdt_amt;
	}

	public void setCrdt_amt(String crdt_amt) {
		this.crdt_amt = crdt_amt;
	}

	public String getCrdt_cd() {
		return crdt_cd;
	}

	public void setCrdt_cd(String crdt_cd) {
		this.crdt_cd = crdt_cd;
	}

	public String getCrdt_desc() {
		return crdt_desc;
	}

	public void setCrdt_desc(String crdt_desc) {
		this.crdt_desc = crdt_desc;
	}

	public String getCrdt_cnfm_date() {
		return crdt_cnfm_date;
	}

	public void setCrdt_cnfm_date(String crdt_cnfm_date) {
		this.crdt_cnfm_date = crdt_cnfm_date;
	}

	public String getCrdt_rel_id() {
		return crdt_rel_id;
	}

	public void setCrdt_rel_id(String crdt_rel_id) {
		this.crdt_rel_id = crdt_rel_id;
	}

	public String getApp_ord_id() {
		return app_ord_id;
	}

	public void setApp_ord_id(String app_ord_id) {
		this.app_ord_id = app_ord_id;
	}

	public String getApp_child_ord_id() {
		return app_child_ord_id;
	}

	public void setApp_child_ord_id(String app_child_ord_id) {
		this.app_child_ord_id = app_child_ord_id;
	}			


}
