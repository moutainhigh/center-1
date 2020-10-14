package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 *代理商商品流通输出类
 * @author shiyz
 * date 2016-03-21
 *
 */
public class AgentInformationResult extends RootResultWeb {

	@ZapcomApi(value="商品流通信息")
	private List<AgentInformation> informations = null;

	public List<AgentInformation> getInformations() {
		return informations;
	}

	public void setInformations(List<AgentInformation> informations) {
		this.informations = informations;
	}
	
}
