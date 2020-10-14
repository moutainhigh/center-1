package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

public class RsyncRequestCancelOrder implements IRsyncRequest {

	private String subsystem = "app"; //调用子系统
	
	private String ord_id = ""; //订单编号
	
	private String can_rsn_cd = "C4"; //取消_顾客_改变心意
	
	private String mdf_id = "app"; //订单编号
	
	private String is_pay = "N"; //是否支付
	
	public String getSubsystem() {
		return subsystem;
	}
	public void setSubsystem(String subsystem) {
		this.subsystem = subsystem;
	}
	public String getOrd_id() {
		return ord_id;
	}
	public void setOrd_id(String ord_id) {
		this.ord_id = ord_id;
	}
	public String getCan_rsn_cd() {
		return can_rsn_cd;
	}
	public void setCan_rsn_cd(String can_rsn_cd) {
		this.can_rsn_cd = can_rsn_cd;
	}
	public String getMdf_id() {
		return mdf_id;
	}
	public void setMdf_id(String mdf_id) {
		this.mdf_id = mdf_id;
	}
	public String getIs_pay() {
		return is_pay;
	}
	public void setIs_pay(String is_pay) {
		this.is_pay = is_pay;
	}
	
	
	
}
