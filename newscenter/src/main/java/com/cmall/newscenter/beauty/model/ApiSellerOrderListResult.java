package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.ordercenter.model.MicroMessagePayment;
import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 商品订单信息
 * @author yangrong
 *
 */
public class ApiSellerOrderListResult {
	
	@ZapcomApi(value="订单状态",remark="")
	private String order_status = "";
	
	@ZapcomApi(value="订单号",remark="")
	private String order_code = "";
	
	@ZapcomApi(value="订单生成时间",remark="")
	private String create_time = "";
	
	@ZapcomApi(value="订单里的商品数量",remark="")
	private int orderStatusNumber;
	
	@ZapcomApi(value="实付总价",remark="")
	private String due_money = "";
	
	@ZapcomApi(value="支付宝移动支付链接",remark="")
	private String alipayUrl = "";
	
	@ZapcomApi(value="支付宝Sign",remark="签名过的")
	private String alipaySign = "";
	
	@ZapcomApi(value="是否为闪购订单", remark="0:闪购     1:非闪购")
	private String ifFlashSales = "";
	
	@ZapcomApi(value="每个订单的商品信息",remark="")
	private List<ApiSellerListResult> apiSellerList = new ArrayList<ApiSellerListResult>();
	
	@ZapcomApi(value="微信支付返回参数")
	private MicroMessagePayment micoPayment = new MicroMessagePayment();
	
	@ZapcomApi(value="支付方式")
	private String payType = "";
	
	public String getIfFlashSales() {
		return ifFlashSales;
	}
	public void setIfFlashSales(String ifFlashSales) {
		this.ifFlashSales = ifFlashSales;
	}
	public String getAlipaySign() {
		return alipaySign;
	}
	public void setAlipaySign(String alipaySign) {
		this.alipaySign = alipaySign;
	}
	public String getAlipayUrl() {
		return alipayUrl;
	}
	public void setAlipayUrl(String alipayUrl) {
		this.alipayUrl = alipayUrl;
	}
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
	public int getOrderStatusNumber() {
		return orderStatusNumber;
	}
	public void setOrderStatusNumber(int orderStatusNumber) {
		this.orderStatusNumber = orderStatusNumber;
	}
	public String getDue_money() {
		return due_money;
	}
	public void setDue_money(String due_money) {
		this.due_money = due_money;
	}
	public List<ApiSellerListResult> getApiSellerList() {
		return apiSellerList;
	}
	public void setApiSellerList(List<ApiSellerListResult> apiSellerList) {
		this.apiSellerList = apiSellerList;
	}
	public MicroMessagePayment getMicoPayment() {
		return micoPayment;
	}
	public void setMicoPayment(MicroMessagePayment micoPayment) {
		this.micoPayment = micoPayment;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	
}
