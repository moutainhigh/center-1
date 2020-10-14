package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 活动 -报名列表输出类
 * @author yangrong
 * date 2014-8-21
 * @version 1.0
 */
public class SecurityCodeInput extends RootInput{
	
	@ZapcomApi(value="商品编号",require=1)
	private String productCode = "";
	
	@ZapcomApi(value="渠道名称",require=1)
	private String channelName = "";
	
	@ZapcomApi(value="生成数量",require=1)
	private int securityNum = 0;

	@ZapcomApi(value="客户编号")
	private String customerNumber = "";
	
	@ZapcomApi(value="订单日期",remark="2015-01-16 10:14:59")
	private String orderTime = "";
	
	@ZapcomApi(value="物流单号")
	private String logisticsNumber = "";

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public String getLogisticsNumber() {
		return logisticsNumber;
	}

	public void setLogisticsNumber(String logisticsNumber) {
		this.logisticsNumber = logisticsNumber;
	}

	public int getSecurityNum() {
		return securityNum;
	}

	public void setSecurityNum(int securityNum) {
		this.securityNum = securityNum;
	}
	
}

