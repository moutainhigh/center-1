package com.cmall.groupcenter.homehas.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class AccmInfo {
	@ZapcomApi(value="积分金额")
	private String accm_amt;
	
	@ZapcomApi(value="积分产生原因编码")
	private String accm_rsn_cd;
	
	@ZapcomApi(value="积分产生原因描述")
	private String accm_desc;
	
	@ZapcomApi(value="积分发生日期")
	private String accm_cnfm_date;
	
	@ZapcomApi(value="家有订单/退货号")
	private String accm_rel_id;
	
	@ZapcomApi(value="家有订单/退货序号")
	private String accm_rel_seq;
	
	@ZapcomApi(value="惠家有订单号")
	private String app_ord_id;

	@ZapcomApi(value="惠家有子订单号")
	private String app_child_ord_id;

	public String getAccm_amt() {
		return accm_amt;
	}

	public void setAccm_amt(String accm_amt) {
		this.accm_amt = accm_amt;
	}

	public String getAccm_rsn_cd() {
		return accm_rsn_cd;
	}

	public void setAccm_rsn_cd(String accm_rsn_cd) {
		this.accm_rsn_cd = accm_rsn_cd;
	}

	public String getAccm_desc() {
		return accm_desc;
	}

	public void setAccm_desc(String accm_desc) {
		this.accm_desc = accm_desc;
	}

	public String getAccm_cnfm_date() {
		return accm_cnfm_date;
	}

	public void setAccm_cnfm_date(String accm_cnfm_date) {
		this.accm_cnfm_date = accm_cnfm_date;
	}

	public String getAccm_rel_id() {
		return accm_rel_id;
	}

	public void setAccm_rel_id(String accm_rel_id) {
		this.accm_rel_id = accm_rel_id;
	}

	public String getAccm_rel_seq() {
		return accm_rel_seq;
	}

	public void setAccm_rel_seq(String accm_rel_seq) {
		this.accm_rel_seq = accm_rel_seq;
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
