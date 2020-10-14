package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 代理商发货基本信息类
 * @author Administrator
 *
 */
public class AgentEntityClass {
	
	@ZapcomApi(value="代理商名称")
	private String agent_name = "";
	
	@ZapcomApi(value="代理商级别")
	private String agent_level = "";
	
	@ZapcomApi(value="微信号")
	private String agent_wchat = "";
	
	@ZapcomApi(value="手机号")
	private String agent_phone = "";
	
	@ZapcomApi(value="商品名称")
	private String product_name = "";
	
	@ZapcomApi(value="商品数量")
	private int product_num = 0;
	
	@ZapcomApi(value="快递公司")
	private String express_company = "";
	
	@ZapcomApi(value="快递单号")
	private String express_number = "";
	
	@ZapcomApi(value="收货地址")
	private String address = "";

	public String getAgent_name() {
		return agent_name;
	}

	public void setAgent_name(String agent_name) {
		this.agent_name = agent_name;
	}

	public String getAgent_level() {
		return agent_level;
	}

	public void setAgent_level(String agent_level) {
		this.agent_level = agent_level;
	}

	public String getAgent_wchat() {
		return agent_wchat;
	}

	public void setAgent_wchat(String agent_wchat) {
		this.agent_wchat = agent_wchat;
	}

	public String getAgent_phone() {
		return agent_phone;
	}

	public void setAgent_phone(String agent_phone) {
		this.agent_phone = agent_phone;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public int getProduct_num() {
		return product_num;
	}

	public void setProduct_num(int product_num) {
		this.product_num = product_num;
	}

	public String getExpress_company() {
		return express_company;
	}

	public void setExpress_company(String express_company) {
		this.express_company = express_company;
	}

	public String getExpress_number() {
		return express_number;
	}

	public void setExpress_number(String express_number) {
		this.express_number = express_number;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
}