package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 商品流通输入类
 * @author shiyz
 * date 2016-03-20
 */
public class AgentInformationInput extends RootInput {

	@ZapcomApi(value="代理商编号",demo="@http://-",require=1)
	private String agentCode = "";

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

}
