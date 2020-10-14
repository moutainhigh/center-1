package com.cmall.ordercenter.model;

import java.math.BigDecimal;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**   
*    
* 项目名称：ordercenter 
* 类名称：ProductSkuInfoForCC   
* 类描述：   
* 创建人：zhaoxq 
* 修改备注：   
* @version    
*    
*/
public class ProductSkuInfoForCC  {

	/**
	 * 产品编号
	 */
	@ZapcomApi(value = "产品编号")
    private String skuCode  = "";
	
	/**
	 * 商品sku名称
	 */
	@ZapcomApi(value = "商品sku名称")
    private String skuName  = "";
	
	/**
	 * 规格
	 */
	@ZapcomApi(value = "规格")
    private String keyValue  = "";
	
	/**
	 * 销售价
	 */
	@ZapcomApi(value = "销售价")
	private BigDecimal sellPrice=new BigDecimal(0.00);
	
	/**
	 * 库存数
	 */
	@ZapcomApi(value = "库存数")
    private int stockNumSum  = 0;
	
	/**
	 * 安全库存数
	 */
	@ZapcomApi(value = "安全库存数")
	private int securityStockNum = 0;
	
	/**
	 * 起订数量
	 */
	@ZapcomApi(value = "起订数量")
	private int miniOrder = 1;
	
	/**
	 * 商品sku图片
	 */
	@ZapcomApi(value="商品sku图片")
	private String skuPicurl = "";
	
	/**
	 * 是否可售
	 */
	@ZapcomApi(value="是否可售",remark="Y:可卖  N:不可卖")
	private String saleYn = "";
	/**
	 * 广告语
	 */
	@ZapcomApi(value="广告语")
	private String skuAdv = "";
	
	/**
	 * sku货号
	 */
	@ZapcomApi(value="sku货号")
	private String sellProductcode = "";

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

	public String getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	public BigDecimal getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}

	public int getStockNumSum() {
		return stockNumSum;
	}

	public void setStockNumSum(int stockNumSum) {
		this.stockNumSum = stockNumSum;
	}

	public int getSecurityStockNum() {
		return securityStockNum;
	}

	public void setSecurityStockNum(int securityStockNum) {
		this.securityStockNum = securityStockNum;
	}

	public int getMiniOrder() {
		return miniOrder;
	}

	public void setMiniOrder(int miniOrder) {
		this.miniOrder = miniOrder;
	}

	public String getSkuPicurl() {
		return skuPicurl;
	}

	public void setSkuPicurl(String skuPicurl) {
		this.skuPicurl = skuPicurl;
	}

	public String getSaleYn() {
		return saleYn;
	}

	public void setSaleYn(String saleYn) {
		this.saleYn = saleYn;
	}

	public String getSkuAdv() {
		return skuAdv;
	}

	public void setSkuAdv(String skuAdv) {
		this.skuAdv = skuAdv;
	}

	public String getSellProductcode() {
		return sellProductcode;
	}

	public void setSellProductcode(String sellProductcode) {
		this.sellProductcode = sellProductcode;
	}
}
