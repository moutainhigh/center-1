package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 发货单查看输出类
 * @author shiyz
 * date 2016-03-19
 *
 */
public class DeliverySeeResult extends RootResultWeb {
	
	@ZapcomApi(value="代理商名称")
	private String agent_name = "";
	@ZapcomApi(value="代理商微信号")
	private String agent_wechat = "";
	@ZapcomApi(value="代理商手机号")
	private String agent_mobilephone = "";
	@ZapcomApi(value="快递公司")
	private String express_company = "";
	@ZapcomApi(value="快递单号")
	private String express_number = "";
	@ZapcomApi(value="发货数量")
	private int delivery_number  = 0;
	@ZapcomApi(value="代理等级")
	private String agent_grade="";
	@ZapcomApi(value="收货地址")
	private String address = "";
	public String getAgent_name() {
		return agent_name;
	}
	public void setAgent_name(String agent_name) {
		this.agent_name = agent_name;
	}
	public String getAgent_wechat() {
		return agent_wechat;
	}
	public void setAgent_wechat(String agent_wechat) {
		this.agent_wechat = agent_wechat;
	}
	public String getAgent_mobilephone() {
		return agent_mobilephone;
	}
	public void setAgent_mobilephone(String agent_mobilephone) {
		this.agent_mobilephone = agent_mobilephone;
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
	public int getDelivery_number() {
		return delivery_number;
	}
	public void setDelivery_number(int delivery_number) {
		this.delivery_number = delivery_number;
	}
	public String getAgent_grade() {
		return agent_grade;
	}
	public void setAgent_grade(String agent_grade) {
		this.agent_grade = agent_grade;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
}
