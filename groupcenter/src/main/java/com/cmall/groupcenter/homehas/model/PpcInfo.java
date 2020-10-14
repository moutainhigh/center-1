package com.cmall.groupcenter.homehas.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class PpcInfo {
	@ZapcomApi(value = "储值金金额")
	private String ppc_amt = "";
	
	@ZapcomApi(value="储值金产生原因编码")
	private String ppc_rsn_cd;
	
	@ZapcomApi(value = "储值金产生原因描述")
	private String ppc_desc = "";
	
	@ZapcomApi(value = "储值金发生日期")
	private String ppc_cnfm_date;
	
	@ZapcomApi(value = "家有订单号")
	private String ppc_rel_id = "";
	
	@ZapcomApi(value="惠家有订单号")
	private String app_ord_id;

	@ZapcomApi(value="惠家有子订单号")
	private String app_child_ord_id;

	
	public String getPpc_amt() {
		return ppc_amt;
	}

	public void setPpc_amt(String ppc_amt) {
		this.ppc_amt = ppc_amt;
	}

	public String getPpc_rsn_cd() {
		return ppc_rsn_cd;
	}

	public void setPpc_rsn_cd(String ppc_rsn_cd) {
		this.ppc_rsn_cd = ppc_rsn_cd;
	}

	public String getPpc_desc() {
		return ppc_desc;
	}

	public void setPpc_desc(String ppc_desc) {
		this.ppc_desc = ppc_desc;
	}

	public String getPpc_cnfm_date() {
		return ppc_cnfm_date;
	}

	public void setPpc_cnfm_date(String ppc_cnfm_date) {
		this.ppc_cnfm_date = ppc_cnfm_date;
	}

	public String getPpc_rel_id() {
		return ppc_rel_id;
	}

	public void setPpc_rel_id(String ppc_rel_id) {
		this.ppc_rel_id = ppc_rel_id;
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
