package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 防伪码输出类
 * @author shiyz
 * date 2014-09-20
 *
 */
public class ReceiptConfirmationResult extends RootResultWeb {

	@ZapcomApi(value="上级名称")
	private String agent_parent = "";
	@ZapcomApi(value="快递公司")
	private String express_company = "";
	@ZapcomApi(value="快递单号")
	private String express_number = "";
	@ZapcomApi(value="手机号")
	private String agent_phone = "";
	@ZapcomApi(value="微信号")
	private String agent_wchat = "";
	@ZapcomApi(value="发货时间")
    private String delivery_time = "";
	
	public String getAgent_parent() {
		return agent_parent;
	}

	public void setAgent_parent(String agent_parent) {
		this.agent_parent = agent_parent;
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

	public String getAgent_phone() {
		return agent_phone;
	}

	public void setAgent_phone(String agent_phone) {
		this.agent_phone = agent_phone;
	}

	public String getAgent_wchat() {
		return agent_wchat;
	}

	public void setAgent_wchat(String agent_wchat) {
		this.agent_wchat = agent_wchat;
	}

	public String getDelivery_time() {
		return delivery_time;
	}

	public void setDelivery_time(String delivery_time) {
		this.delivery_time = delivery_time;
	}
	
}
