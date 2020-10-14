package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.ordercenter.model.MicroMessagePayment;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ApiOrderDetailsResult  extends RootResultWeb{
	@ZapcomApi(value="订单编号")
	private String order_code = "";
	@ZapcomApi(value="订单状态")
	private String order_status = "";
	@ZapcomApi(value="订单金额")
	private String order_money = "";
	@ZapcomApi(value="下单时间")
	private String create_time = "";
	@ZapcomApi(value="应付款金额")
	private Double due_money;
	@ZapcomApi(value="首单优惠")
	private String firstFavorable = "";
	@ZapcomApi(value="运费")
	private Double freight = 0.00;
	@ZapcomApi(value="满减")
	private Double fullSubtraction = 0.00;
	@ZapcomApi(value="手机下单减少")
	private Double telephoneSubtraction = 0.00;
	@ZapcomApi(value="收货人地址")
	private String consigneeAddress = "";
	@ZapcomApi(value="收货人电话")
	private String consigneeTelephone = "";
	@ZapcomApi(value="支付方式")
	private String pay_type = "";
	@ZapcomApi(value="支付宝移动支付链接",remark="")
	private String alipayUrl = "";
	@ZapcomApi(value="支付宝Sign",remark="签名过的")
	private String alipaySign = "";
	@ZapcomApi(value="收货人姓名")
	private String consigneeName = "";
	@ZapcomApi(value="失效时间提示")
	private String  failureTimeReminder = "";
	@ZapcomApi(value="是否为闪购订单", remark="0:闪购     1:非闪购")
	private String ifFlashSales = "";
	@ZapcomApi(value="发票信息")
	private InvoiceInformationResult invoiceInformation = new InvoiceInformationResult();
	@ZapcomApi(value="订单商品列表")
	private List<ApiOrderSellerDetailsResult>  orderSellerList = new ArrayList<ApiOrderSellerDetailsResult>();
	
	@ZapcomApi(value="订单备注")
	private String remark = "";
	@ZapcomApi(value="微信支付返回参数")
	private MicroMessagePayment micoPayment = new MicroMessagePayment();
	
	public String getAlipayUrl() {
		return alipayUrl;
	}
	public void setAlipayUrl(String alipayUrl) {
		this.alipayUrl = alipayUrl;
	}
	public String getAlipaySign() {
		return alipaySign;
	}
	public void setAlipaySign(String alipaySign) {
		this.alipaySign = alipaySign;
	}
	public Double getFreight() {
		return freight;
	}
	public Double getFullSubtraction() {
		return fullSubtraction;
	}
	public Double getTelephoneSubtraction() {
		return telephoneSubtraction;
	}
	public String getOrder_code() {
		return order_code;
	}
	public void setOrder_code(String order_code) {
		this.order_code = order_code;
	}
	public String getOrder_status() {
		return order_status;
	}
	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}
	public List<ApiOrderSellerDetailsResult> getOrderSellerList() {
		return orderSellerList;
	}
	public void setOrderSellerList(List<ApiOrderSellerDetailsResult> orderSellerList) {
		this.orderSellerList = orderSellerList;
	}
	public String getOrder_money() {
		return order_money;
	}
	public void setOrder_money(String order_money) {
		this.order_money = order_money;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public Double getDue_money() {
		return due_money;
	}
	public void setDue_money(Double due_money) {
		this.due_money = due_money;
	}
	public void setFreight(Double freight) {
		this.freight = freight;
	}
	public void setFullSubtraction(Double fullSubtraction) {
		this.fullSubtraction = fullSubtraction;
	}
	public void setTelephoneSubtraction(Double telephoneSubtraction) {
		this.telephoneSubtraction = telephoneSubtraction;
	}
	public String getFirstFavorable() {
		return firstFavorable;
	}
	public void setFirstFavorable(String firstFavorable) {
		this.firstFavorable = firstFavorable;
	}
	public String getConsigneeAddress() {
		return consigneeAddress;
	}
	public void setConsigneeAddress(String consigneeAddress) {
		this.consigneeAddress = consigneeAddress;
	}
	public String getConsigneeTelephone() {
		return consigneeTelephone;
	}
	public void setConsigneeTelephone(String consigneeTelephone) {
		this.consigneeTelephone = consigneeTelephone;
	}
	public String getPay_type() {
		return pay_type;
	}
	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}
	public InvoiceInformationResult getInvoiceInformation() {
		return invoiceInformation;
	}
	public void setInvoiceInformation(InvoiceInformationResult invoiceInformation) {
		this.invoiceInformation = invoiceInformation;
	}
	public String getConsigneeName() {
		return consigneeName;
	}
	public void setConsigneeName(String consigneeName) {
		this.consigneeName = consigneeName;
	}
	public String getFailureTimeReminder() {
		return failureTimeReminder;
	}
	public void setFailureTimeReminder(String failureTimeReminder) {
		this.failureTimeReminder = failureTimeReminder;
	}
	public String getIfFlashSales() {
		return ifFlashSales;
	}
	public void setIfFlashSales(String ifFlashSales) {
		this.ifFlashSales = ifFlashSales;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public MicroMessagePayment getMicoPayment() {
		return micoPayment;
	}
	public void setMicoPayment(MicroMessagePayment micoPayment) {
		this.micoPayment = micoPayment;
	}
	
}
