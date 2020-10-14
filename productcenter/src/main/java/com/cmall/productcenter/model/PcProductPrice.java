package com.cmall.productcenter.model;

import java.math.BigDecimal;


/**   
*    
* 项目名称：productcenter   
* 类名称：PcProductPrice   
* 类描述：   
* 创建人：李国杰
* 创建时间：2014-09-20
* 修改人：yanzj
* 修改时间：2014-09-20
* 修改备注：   
* @version    
*    
*/
public class PcProductPrice  {

	/**
	 * sku编码
	 */
	private String skuCode = "";
	
	/**
	 * product编码
	 */
	private String productCode = "";
	/**
	 * 销售价
	 */
	private BigDecimal sellPrice=new BigDecimal(0.00);
	/**
	 * 市场价
	 */
	private BigDecimal marketPrice = new BigDecimal(0.00);
	/**
	 * 库存数
	 */
	private int stockNum= 0;
	/**
	 * 活动开始时间
	 */
	private String startTime = "";
	/**
	 * 活动结束时间
	 */
	private String endTime = "";
	/**
	 * 折扣
	 */
	private String discount = "";
	/**
	 * 活动价格
	 */
	private String vipPrice = "";
	
	public BigDecimal getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}
	public BigDecimal getMarketPrice() {
		return marketPrice;
	}
	public void setMarketPrice(BigDecimal marketPrice) {
		this.marketPrice = marketPrice;
	}
	public int getStockNum() {
		return stockNum;
	}
	public void setStockNum(int stockNum) {
		this.stockNum = stockNum;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getSkuCode() {
		return skuCode;
	}
	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public String getVipPrice() {
		return vipPrice;
	}
	public void setVipPrice(String vipPrice) {
		this.vipPrice = vipPrice;
	}
	
	
}

