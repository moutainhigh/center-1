package com.cmall.groupcenter.kjt.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class RsyncModelGetKjtProductPrice implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 商品库存列表
	 */
//	private List<ProductDetaily> productList = new ArrayList<ProductDetaily>();
	/**
	 * 商品ID
	 */
	private String ProductId = "";
	/**
	 * 商品状态，0,仅展示 1, 上架。2-不展示，-2-已终止，-1,已作废
	 */
	private int Status;
	/**
	 * 渠道销售价格
	 */
	private BigDecimal ProductPrice;
	/**
	 * 发生商品创建、修改或订阅的最后时间
	 */
	private String PriceChangedDate="";

	
	public String getProductId() {
		return ProductId;
	}
	public void setProductId(String productId) {
		ProductId = productId;
	}
	public int getStatus() {
		return Status;
	}
	public void setStatus(int status) {
		Status = status;
	}
	public BigDecimal getProductPrice() {
		return ProductPrice;
	}
	public void setProductPrice(BigDecimal productPrice) {
		ProductPrice = productPrice;
	}
	public String getPriceChangedDate() {
		return PriceChangedDate;
	}
	public void setPriceChangedDate(String priceChangedDate) {
		PriceChangedDate = priceChangedDate;
	}
	
	
}
