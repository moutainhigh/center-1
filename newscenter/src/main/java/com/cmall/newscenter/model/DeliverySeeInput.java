package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 发货单查看
 * @author shiyz
 * date 2016-03-19
 */
public class DeliverySeeInput extends RootInput {

	@ZapcomApi(value="查询发货单参数",demo="手机号，微信号，代理商名称",require=1)
	private String agentCode = "";

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}
	
	
}
