package com.cmall.groupcenter.account.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 订单返利信息
 * @dyc
 * */
public class OrderRebateInfo extends RootInput{
	@ZapcomApi(value="用户编号",require=1)
	private String userCode="";
	@ZapcomApi(value="订单号",require=1)
	private String orderCode="";
	@ZapcomApi(value="订单总金额",require=1)
	private BigDecimal orderTotalAmount = new BigDecimal(0);
	@ZapcomApi(value="运费",require=0)
	private BigDecimal freight = new BigDecimal(0);
	@ZapcomApi(value="商品信息",require=1)
	private List<ProductInfo> products = new ArrayList<ProductInfo>();
	@ZapcomApi(value="订单创建时间",require=1,verify = "base=datetime")
	private String orderCreateTime="";
	@ZapcomApi(value="是否参与清分",remark="1:参与,0:不参与",require=1)
	private String isReckon="";
	@ZapcomApi(value="渠道",require=0)
	private String channel_code="";
	/**
	 * 获取  userCode
	 */
	public String getUserCode() {
		return userCode;
	}
	/**
	 * 设置 
	 * @param userCode 
	 */
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	/**
	 * 获取  orderCode
	 */
	public String getOrderCode() {
		return orderCode;
	}
	/**
	 * 设置 
	 * @param orderCode 
	 */
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	/**
	 * 获取  orderTotalAmount
	 */
	public BigDecimal getOrderTotalAmount() {
		return orderTotalAmount;
	}
	/**
	 * 设置 
	 * @param orderTotalAmount 
	 */
	public void setOrderTotalAmount(BigDecimal orderTotalAmount) {
		this.orderTotalAmount = orderTotalAmount;
	}
	/**
	 * 获取  freight
	 */
	public BigDecimal getFreight() {
		return freight;
	}
	/**
	 * 设置 
	 * @param freight 
	 */
	public void setFreight(BigDecimal freight) {
		this.freight = freight;
	}
	
	/**
	 * 获取  products
	 */
	public List<ProductInfo> getProducts() {
		return products;
	}
	/**
	 * 设置 
	 * @param products 
	 */
	public void setProducts(List<ProductInfo> products) {
		this.products = products;
	}
	/**
	 * 获取  orderCreateTime
	 */
	public String getOrderCreateTime() {
		return orderCreateTime;
	}
	/**
	 * 设置 
	 * @param orderCreateTime 
	 */
	public void setOrderCreateTime(String orderCreateTime) {
		this.orderCreateTime = orderCreateTime;
	}
	/**
	 * 获取  isReckon
	 */
	public String getIsReckon() {
		return isReckon;
	}
	/**
	 * 设置 
	 * @param isReckon 
	 */
	public void setIsReckon(String isReckon) {
		this.isReckon = isReckon;
	}
	
	public String getChannel_code() {
		return channel_code;
	}
	public void setChannel_code(String channel_code) {
		this.channel_code = channel_code;
	}
	
}
