package com.cmall.membercenter.model;

import java.math.BigInteger;

import com.cmall.systemcenter.model.AppPhoto;
import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class AgentMemberInfo {

	@ZapcomApi(value = "用户编号")
	private String member_code = "";

	@ZapcomApi(value = "手机号")
	private String agent_password = "";

	@ZapcomApi(value = "微信号")
	private String agent_wechat = "";
	
	@ZapcomApi(value="代理商级别")
	private String agent_level = "";
	
	@ZapcomApi(value="授权码")
	private String auth_code = "";
	
	@ZapcomApi(value="代理商姓名")
	private String agent_name = "";
	
	@ZapcomApi(value="是否手机验证")
	private String isnot_proving="";
	
	@ZapcomApi(value="是否最高级别",demo="1代表是0代表不是")
	private String highest_level="0";

	public String getMember_code() {
		return member_code;
	}

	public void setMember_code(String member_code) {
		this.member_code = member_code;
	}

	public String getAgent_password() {
		return agent_password;
	}

	public void setAgent_password(String agent_password) {
		this.agent_password = agent_password;
	}

	public String getAgent_wechat() {
		return agent_wechat;
	}

	public void setAgent_wechat(String agent_wechat) {
		this.agent_wechat = agent_wechat;
	}

	public String getAgent_level() {
		return agent_level;
	}

	public void setAgent_level(String agent_level) {
		this.agent_level = agent_level;
	}

	public String getAuth_code() {
		return auth_code;
	}

	public void setAuth_code(String auth_code) {
		this.auth_code = auth_code;
	}

	public String getAgent_name() {
		return agent_name;
	}

	public void setAgent_name(String agent_name) {
		this.agent_name = agent_name;
	}

	public String getIsnot_proving() {
		return isnot_proving;
	}

	public void setIsnot_proving(String isnot_proving) {
		this.isnot_proving = isnot_proving;
	}

	public String getHighest_level() {
		return highest_level;
	}

	public void setHighest_level(String highest_level) {
		this.highest_level = highest_level;
	}
	
}
