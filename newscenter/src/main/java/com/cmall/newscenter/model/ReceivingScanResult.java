package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 收货输出类
 * @author shiyz
 * date 2016-03-20
 *
 */
public class ReceivingScanResult extends RootResultWeb {


	@ZapcomApi(value="上级代理名称",demo="张三",require=1)
	private String superior_agent = "";
	@ZapcomApi(value="上级代理手机号")
	private String  agent_phone = "";
	@ZapcomApi(value="上级代理微信")
	private String agent_wx="";
	@ZapcomApi(value="数量")
	private int num = 0;
	public String getSuperior_agent() {
		return superior_agent;
	}
	public void setSuperior_agent(String superior_agent) {
		this.superior_agent = superior_agent;
	}
	public String getAgent_phone() {
		return agent_phone;
	}
	public void setAgent_phone(String agent_phone) {
		this.agent_phone = agent_phone;
	}
	public String getAgent_wx() {
		return agent_wx;
	}
	public void setAgent_wx(String agent_wx) {
		this.agent_wx = agent_wx;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	
}
