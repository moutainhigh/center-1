package com.cmall.newscenter.beauty.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 惠美丽-提交订单输入类
 * @author houwen
 * date 2014-10-08
 * @version 1.0
 */
public class AddOrderInput extends RootInput {
	
	
	@ZapcomApi(value = "订单类型", remark = "449715200003试用订单、449715200004闪购订单、449715200005普通订单", require=1, demo = "449715200003" )
	private String order_type = "";
	
	@ZapcomApi(value = "订单来源", remark = "订单来源,可选值:449715190001(正常订单)，449715190002(android订单),449715190003(ios订单),449715190004(网站wap手机订单)", demo = "Android",require=1 )
	private String order_souce = "";
	
	@ZapcomApi(value = "商品列表", remark = "不可为空",require=1, demo = "")
	private List<OrderDetail> goods = new ArrayList<OrderDetail>();
	
	@ZapcomApi(value="收货人地址信息",remark="收货人地址信息",require=1,demo="")
	private OrderAddress orderAddress = new OrderAddress();
	
	@ZapcomApi(value = "支付方式", remark = "支付方式",require=1, demo = "449716200001:在线支付,449716200002:货到付款")
	private String pay_type = "";
	
	
	@ZapcomApi(value = "配送方式", remark = "配送方式",require=1, demo = "449715210001：快递,449715210002：邮局")
	private String send_type = "";
	
	/**
	 * 商品运费(实际运费)
	 */
	@ZapcomApi(value="商品运费",remark="实际运费",require=1,demo="10")
	private BigDecimal transportMoney =new BigDecimal(0.00);
	
	
	/**
	 * 订单金额=商品金额+商品运费-商品活动金额-
	 */
	@ZapcomApi(value="订单金额",remark="订单金额",require=1,demo="222")
	private BigDecimal orderMoney = new BigDecimal(0.00);
	
	/**
	 * 微账户支付金额
	 */
	@ZapcomApi(value="已支付金额",remark="已支付金额",demo="324.00",require=1)
	private BigDecimal payedMoney = new BigDecimal(0.00);
	
	/**
	 *  应付款
	 */
	@ZapcomApi(value="应付款",remark="应付款",demo="324.00",require=1)
	private BigDecimal dueMoney = new BigDecimal(0.00); 
	
	@ZapcomApi(value = "app版本信息", remark = "app版本信息",require=1, demo = "1.0.0")
	private String app_vision = "";

	public String getOrder_type() {
		return order_type;
	}

	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}

	public String getOrder_souce() {
		return order_souce;
	}

	public void setOrder_souce(String order_souce) {
		this.order_souce = order_souce;
	}

	public List<OrderDetail> getGoods() {
		return goods;
	}

	public void setGoods(List<OrderDetail> goods) {
		this.goods = goods;
	}

	public OrderAddress getOrderAddress() {
		return orderAddress;
	}

	public void setOrderAddress(OrderAddress orderAddress) {
		this.orderAddress = orderAddress;
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

	public BigDecimal getOrderMoney() {
		return orderMoney;
	}

	public void setOrderMoney(BigDecimal orderMoney) {
		this.orderMoney = orderMoney;
	}

	public BigDecimal getPayedMoney() {
		return payedMoney;
	}

	public void setPayedMoney(BigDecimal payedMoney) {
		this.payedMoney = payedMoney;
	}

	public BigDecimal getDueMoney() {
		return dueMoney;
	}

	public void setDueMoney(BigDecimal dueMoney) {
		this.dueMoney = dueMoney;
	}

	public String getApp_vision() {
		return app_vision;
	}

	public void setApp_vision(String app_vision) {
		this.app_vision = app_vision;
	}

	public BigDecimal getTransportMoney() {
		return transportMoney;
	}

	public void setTransportMoney(BigDecimal transportMoney) {
		this.transportMoney = transportMoney;
	}
	
}
