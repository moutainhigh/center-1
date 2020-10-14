package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class AgentInformation {
	
	@ZapcomApi(value="数量",demo="20",require=1)
	private String receiving_num = "";
	@ZapcomApi(value="状态")
	private String receiving_stats = "";
	@ZapcomApi(value="录入时间")
	private String entry_time="";
	
	public String getEntry_time() {
		return entry_time;
	}
	public void setEntry_time(String entry_time) {
		this.entry_time = entry_time;
	}
	public String getReceiving_num() {
		return receiving_num;
	}
	public void setReceiving_num(String receiving_num) {
		this.receiving_num = receiving_num;
	}
	public String getReceiving_stats() {
		return receiving_stats;
	}
	public void setReceiving_stats(String receiving_stats) {
		this.receiving_stats = receiving_stats;
	}
	
}
