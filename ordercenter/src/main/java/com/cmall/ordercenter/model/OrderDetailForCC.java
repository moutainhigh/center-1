package com.cmall.ordercenter.model;

import java.math.BigDecimal;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**   
* 订单明细表 
*    
* 项目名称：ordercenter
* 类名称：OrderDetailForCC
* 类描述：   
* 创建人：zhaoxq  
* 修改备注：   
* @version    
*    
*/
public class OrderDetailForCC{
	
	/**
	 * 订单编号
	 */
	@ZapcomApi(value="订单编号")
	private String orderCode = "";
	
	/**
	 * 产品编号
	 */
	@ZapcomApi(value="产品编号")
	private String skuCode = "";
	
	/**
	 * 商品编号
	 */
	@ZapcomApi(value="商品编号")
	private String productCode = "";
	
	/**
	 * 商品名称
	 */
	@ZapcomApi(value="商品名称")
	private String productName = "";
	
	/**
	 * 产品名称
	 */
	@ZapcomApi(value="产品名称")
	private String skuName = "";
	
	/**
	 * 产品价格
	 */
	@ZapcomApi(value="产品价格")
	private BigDecimal skuPrice = new BigDecimal(0.00);
	
	/**
	 * 产品数量
	 */
	@ZapcomApi(value="产品数量")
	private int skuNum = 0;
	
	/**
	 * 商品sku规格参数
	 */
	@ZapcomApi(value="商品sku规格参数")
	private String SkuKeyValue = "";

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

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getSkuName() {
		return skuName;
	}

	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}

	public int getSkuNum() {
		return skuNum;
	}

	public void setSkuNum(int skuNum) {
		this.skuNum = skuNum;
	}

	public BigDecimal getSkuPrice() {
		return skuPrice;
	}

	public void setSkuPrice(BigDecimal skuPrice) {
		this.skuPrice = skuPrice;
	}

	public String getSkuKeyValue() {
		return SkuKeyValue;
	}

	public void setSkuKeyValue(String skuKeyValue) {
		SkuKeyValue = skuKeyValue;
	}
}
