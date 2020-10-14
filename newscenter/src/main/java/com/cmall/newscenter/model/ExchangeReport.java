package com.cmall.newscenter.model;

/**
 * 换货报表VO
 * 
 * @author lilei
 * 
 */
public class ExchangeReport {
	// 订单编号(第三方订单编号) 后加字段 商品表
	private String thirdOrderCode;
	// 外部平台单号(表内的订单编号) 商品表
	private String orderCode;
	// 产品编号(表内的商品编号) 商品明细表
	private String skuCode;
	// 产品名称(表内的商品名称)商品明细表
	private String skuName;
	//规格 excel字段 不插入数据库
	private String standard;
	// 成交单价(表内的当前价格)商品明细表
	private String currentPrice;
	// 订货数量(表内的数量) 商品明细表
	private String count;
	//换货数量  excel字段 不插入数据库
	private String exchangeGoodscount;
	//换货时间 
	private String createTime;
	public String getThirdOrderCode() {
		return thirdOrderCode;
	}
	public void setThirdOrderCode(String thirdOrderCode) {
		this.thirdOrderCode = thirdOrderCode;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String getSkuCode() {
		return skuCode;
	}
	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}
	public String getSkuName() {
		return skuName;
	}
	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}
	public String getStandard() {
		return standard;
	}
	public void setStandard(String standard) {
		this.standard = standard;
	}
	public String getCurrentPrice() {
		return currentPrice;
	}
	public void setCurrentPrice(String currentPrice) {
		this.currentPrice = currentPrice;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getExchangeGoodscount() {
		return exchangeGoodscount;
	}
	public void setExchangeGoodscount(String exchangeGoodscount) {
		this.exchangeGoodscount = exchangeGoodscount;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	

}
