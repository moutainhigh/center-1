package com.cmall.ordercenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseclass.BaseClass;

/**
 * 换货主信息
 * @author gaoy
 *
 */
public class ExchangegoodsModel extends BaseClass{

	/**
	 * 换货明细列表(增加换货信息 输入参数时用)
	 */
	private List<ExchangegoodsDetailModel> exgDetailListInput = new ArrayList<ExchangegoodsDetailModel>();
	
	/**
	 * 订单编号
	 */
	private String orderCode = "";
	
	/**
	 * 买家编号
	 */
	private String buyerCode = "";
	
	/**
	 * 卖家编号
	 */
	private String sellerCode = "";
	
	/**
	 * 换货原因
	 */
	private String exchangeReason = "";
	
	/**
	 * 换货状态
	 */
	private String status = "";
	
	/**
	 * 换货运费
	 */
	private double transportMoney = 0.00;
	
	/**
	 * 换货联系人
	 */
	private String contacts = "";
	
	/**
	 * 联系人电话
	 */
	private String mobile = "";
	
	/**
	 * 收货地址
	 */
	private String address = "";
	
	/**
	 * 产品图片路径
	 */
	private String picUrl = "";
	
	/**
	 * 创建人(更新换货日志用)
	 */
	private String createUser = "";

	public List<ExchangegoodsDetailModel> getExgDetailListInput() {
		return exgDetailListInput;
	}

	public void setExgDetailListInput(
			List<ExchangegoodsDetailModel> exgDetailListInput) {
		this.exgDetailListInput = exgDetailListInput;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getBuyerCode() {
		return buyerCode;
	}

	public void setBuyerCode(String buyerCode) {
		this.buyerCode = buyerCode;
	}

	public String getSellerCode() {
		return sellerCode;
	}

	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}

	public String getExchangeReason() {
		return exchangeReason;
	}

	public void setExchangeReason(String exchangeReason) {
		this.exchangeReason = exchangeReason;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getTransportMoney() {
		return transportMoney;
	}

	public void setTransportMoney(double transportMoney) {
		this.transportMoney = transportMoney;
	}

	public String getContacts() {
		return contacts;
	}

	public void setContacts(String contacts) {
		this.contacts = contacts;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	
}
