package com.cmall.groupcenter.active;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseclass.BaseClass;

/**
 * sku基础信息类
 * @author jlin
 *
 */
public class BaseSkuInfo extends BaseClass {

	/**
	 * 产品编号
	 */
	private String skuCode ="";
	
	 /**
     * 商品的sku信息
     */
    private String skuName="";
	
	/**
	 * 销售价
	 */
	private BigDecimal sellPrice=new BigDecimal(0.00);
	
	/**
	 * 市场价
	 */
	private BigDecimal marketPrice = new BigDecimal(0.00);
	
	/**
	 * 成交价
	 */
	private BigDecimal transactionPrice = new BigDecimal(0.00);
	
	private List<BaseActive> activeList = new ArrayList<BaseActive>();

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

	public BigDecimal getTransactionPrice() {
		return transactionPrice;
	}

	public void setTransactionPrice(BigDecimal transactionPrice) {
		this.transactionPrice = transactionPrice;
	}

	public List<BaseActive> getActiveList() {
		return activeList;
	}

	public void setActiveList(List<BaseActive> activeList) {
		this.activeList = activeList;
	}
	
	
}
