package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 发货输出类
 * @author shiyz
 * date 2016-03-20
 *
 */
public class DeliveryCharResult extends RootResultWeb {
	
	@ZapcomApi(value="发货单查看")
	private List<AgentEntityClass> agentEntity = new ArrayList<AgentEntityClass>();
	
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();

	public List<AgentEntityClass> getAgentEntity() {
		return agentEntity;
	}

	public void setAgentEntity(List<AgentEntityClass> agentEntity) {
		this.agentEntity = agentEntity;
	}

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}

}
