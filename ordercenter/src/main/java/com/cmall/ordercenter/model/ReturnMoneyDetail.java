package com.cmall.ordercenter.model;
import com.srnpr.zapcom.baseclass.BaseClass;
/**
 * 退款类
 * @author:     hxd
 * Date:        2013年9月11日
 * project_name:ordercenter
 */
public class ReturnMoneyDetail extends BaseClass
{
	/**
	 * 退款单号
	 */
	private String return_money_code = "";
	/**
	 * 订单编号
	 */
	private String order_code = "";
	/**
	 * 退款金额
	 */
	private float return_money = 0;
	/**
	 * 退款返还方式
	 */
	private String return_channel = "";
	/**
	 * 创建时间
	 */
	private String create_time = "";
	/**
	 * 返还方式
	 */
	private String return_type = "";
	public String getReturn_money_code() {
		return return_money_code;
	}
	public void setReturn_money_code(String return_money_code) {
		this.return_money_code = return_money_code;
	}
	public String getOrder_code() {
		return order_code;
	}
	public void setOrder_code(String order_code) {
		this.order_code = order_code;
	}
	public float getReturn_money() {
		return return_money;
	}
	public void setReturn_money(float return_money) {
		this.return_money = return_money;
	}
	public String getReturn_channel() {
		return return_channel;
	}
	public void setReturn_channel(String return_channel) {
		this.return_channel = return_channel;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getReturn_type() {
		return return_type;
	}
	public void setReturn_type(String return_type) {
		this.return_type = return_type;
	}
	public ReturnMoneyDetail(String return_money_code, String order_code,
			float return_money, String return_channel, String create_time,
			String return_type) {
		super();
		this.return_money_code = return_money_code;
		this.order_code = order_code;
		this.return_money = return_money;
		this.return_channel = return_channel;
		this.create_time = create_time;
		this.return_type = return_type;
	}
	public ReturnMoneyDetail() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
