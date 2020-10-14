package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class AccountDetailInput extends RootInput {

	/**
	 *店铺编号 
	 */
	@ZapcomApi(value="店铺编号")
	private String seller_code = "";
	
	/**
	 *起始时间 
	 */
	@ZapcomApi(value="起始时间 ")
	private String start_time = "";
	 
	/**
	 *结束时间 
	 */
	@ZapcomApi(value="结束时间 ")
	private String end_time = "";

	public String getSeller_code() {
		return seller_code;
	}

	public void setSeller_code(String seller_code) {
		this.seller_code = seller_code;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	
}
