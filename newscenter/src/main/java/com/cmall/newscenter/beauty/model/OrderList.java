package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;


/**
 *  我的订单列表-》订单  输出类
 * @author houwen	
 * date 2014-10-10
 * @version 1.0
 */
public class OrderList{
	
	
	@ZapcomApi(value="订单状态",remark="订单状态")
	private String order_status = "";
	
	@ZapcomApi(value="订单号",remark="订单号")
	private String order_code = "";
	
	@ZapcomApi(value="订单生成时间",remark="订单生成时间")
	private String create_time = "";

	@ZapcomApi(value="订单金额",remark="价格合计")
	private String order_money = "";

	@ZapcomApi(value="订单类型",remark="订单类型")
	private String order_type = "";
	
	@ZapcomApi(value="支付方式",remark="支付方式")
	private String pay_type = "";
	
	@ZapcomApi(value="配送方式",remark="配送方式")
	private String send_type = "";
	
	@ZapcomApi(value="订单商品信息列表",remark="订单商品信息列表")
	private List<GoodsOrderInfoList> goodsOrderInfoLists = new ArrayList<GoodsOrderInfoList>();

	@ZapcomApi(value="订单地址信息列表",remark="订单地址信息列表")
	private OrderAddress orderAddresses = new OrderAddress();

	
	public String getOrder_status() {
		return order_status;
	}
	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}
	public String getOrder_code() {
		return order_code;
	}
	public void setOrder_code(String order_code) {
		this.order_code = order_code;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getOrder_money() {
		return order_money;
	}
	public void setOrder_money(String order_money) {
		this.order_money = order_money;
	}
	public String getOrder_type() {
		return order_type;
	}
	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}
	public String getPay_type() {
		return pay_type;
	}
	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}
	public String getSend_type() {
		return send_type;
	}
	public void setSend_type(String send_type) {
		this.send_type = send_type;
	}
	public List<GoodsOrderInfoList> getGoodsOrderInfoLists() {
		return goodsOrderInfoLists;
	}
	public void setGoodsOrderInfoLists(List<GoodsOrderInfoList> goodsOrderInfoLists) {
		this.goodsOrderInfoLists = goodsOrderInfoLists;
	}
	public OrderAddress getOrderAddresses() {
		return orderAddresses;
	}
	public void setOrderAddresses(OrderAddress orderAddresses) {
		this.orderAddresses = orderAddresses;
	}

}
