package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 收货输入类
 * @author shiyz
 * date 2016-03-20
 */
public class ReceivingScanInput extends RootInput {

	@ZapcomApi(value="二维码",demo="@http://-",require=1)
	private String securityCode = "";
	@ZapcomApi(value="扫描数量",require=1)
	private int delivery_number  = 0;
	@ZapcomApi(value="代理商编号",require=1)
	private String agent_code="";
	@ZapcomApi(value="代理品牌分类")
	private String category_code = "";
	public String getSecurityCode() {
		return securityCode;
	}
	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
	public int getDelivery_number() {
		return delivery_number;
	}
	public void setDelivery_number(int delivery_number) {
		this.delivery_number = delivery_number;
	}
	public String getCategory_code() {
		return category_code;
	}
	public void setCategory_code(String category_code) {
		this.category_code = category_code;
	}
	public String getAgent_code() {
		return agent_code;
	}
	public void setAgent_code(String agent_code) {
		this.agent_code = agent_code;
	}
}
