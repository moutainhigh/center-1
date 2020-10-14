package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class CirculationiInformation {
	
	@ZapcomApi(value="代理名称",demo="张三",require=1)
	private String superior_agent = "";
	@ZapcomApi(value="代理级别")
	private String  agent_level = "";
	@ZapcomApi(value="录入时间")
	private String entry_time="";
	public String getSuperior_agent() {
		return superior_agent;
	}
	public void setSuperior_agent(String superior_agent) {
		this.superior_agent = superior_agent;
	}
	public String getAgent_level() {
		return agent_level;
	}
	public void setAgent_level(String agent_level) {
		this.agent_level = agent_level;
	}
	public String getEntry_time() {
		return entry_time;
	}
	public void setEntry_time(String entry_time) {
		this.entry_time = entry_time;
	}

}
