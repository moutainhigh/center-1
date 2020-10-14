package com.cmall.ordercenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 
 * 增加换货信息的输入参数
 * 项目名称：ordercenter 
 * 类名称：     ApiAddExchangegoodsInput 
 * 类描述：     换货信息对象
 * 创建人：     gaoy  
 * 创建时间：2013年9月18日上午9:46:06 
 * 修改人：     gaoy
 * 修改时间：2013年9月18日上午9:46:06
 * 修改备注：  
 * @version
 *
 */
public class ApiAddExchangegoodsInput  extends RootInput{

	/**
	 * 换货明细列表(增加换货信息 输入参数时用)
	 */
	@ZapcomApi(value="换货明细列表",remark="增加换货信息 输入参数时用")
	private List<ExchangegoodsDetailModel> exgDetailListInput = new ArrayList<ExchangegoodsDetailModel>();
	
	/**
	 * 订单编号
	 */
	@ZapcomApi(value="订单编号")
	private String orderCode = "";
	
	/**
	 * 买家编号
	 */
	@ZapcomApi(value="买家编号")
	private String buyerCode = "";
	
	/**
	 * 换货原因
	 */
	@ZapcomApi(value="换货原因")
	private String exchangeReason = "";
	
	/**
	 * 换货运费
	 */
	@ZapcomApi(value="换货运费")
	private double transportMoney = 0.00;
	
	/**
	 * 换货联系人
	 */
	@ZapcomApi(value="换货联系人")
	private String contacts = "";
	
	/**
	 * 联系人电话
	 */
	@ZapcomApi(value="联系人电话")
	private String mobile = "";
	
	/**
	 * 收货地址
	 */
	@ZapcomApi(value="收货地址")
	private String address = "";
	
	/**
	 * 产品图片路径
	 */
	@ZapcomApi(value="产品图片路径")
	private String picUrl = "";
	
	/**
	 * 描述
	 */
	@ZapcomApi(value="描述")
	private String  description = "";

	/**
	 * 创建人(更新换货日志用)
	 */
	@ZapcomApi(value="创建人",remark="更新换货日志用")
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

	public String getExchangeReason() {
		return exchangeReason;
	}

	public void setExchangeReason(String exchangeReason) {
		this.exchangeReason = exchangeReason;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	
}
