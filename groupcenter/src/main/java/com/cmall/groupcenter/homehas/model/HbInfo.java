package com.cmall.groupcenter.homehas.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class HbInfo {
	@ZapcomApi(value = "惠币金额")
	private String hb_amt = "";
	
	@ZapcomApi(value="惠币产生原因编码")
	private String hb_rsn_cd;
	
	@ZapcomApi(value = "惠币产生原因描述")
	private String hb_desc = "";
	
	@ZapcomApi(value = "惠币发生日期")
	private String hb_cnfm_date;
	
	@ZapcomApi(value = "家有订单号")
	private String hb_rel_id = "";
	
	@ZapcomApi(value="惠家有订单号")
	private String app_ord_id;

	@ZapcomApi(value="惠家有子订单号")
	private String app_child_ord_id;

	public String getHb_amt() {
		return hb_amt;
	}

	public void setHb_amt(String hb_amt) {
		this.hb_amt = hb_amt;
	}

	public String getHb_rsn_cd() {
		return hb_rsn_cd;
	}

	public void setHb_rsn_cd(String hb_rsn_cd) {
		this.hb_rsn_cd = hb_rsn_cd;
	}

	public String getHb_desc() {
		return hb_desc;
	}

	public void setHb_desc(String hb_desc) {
		this.hb_desc = hb_desc;
	}

	public String getHb_cnfm_date() {
		return hb_cnfm_date;
	}

	public void setHb_cnfm_date(String hb_cnfm_date) {
		this.hb_cnfm_date = hb_cnfm_date;
	}

	public String getHb_rel_id() {
		return hb_rel_id;
	}

	public void setHb_rel_id(String hb_rel_id) {
		this.hb_rel_id = hb_rel_id;
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
