package com.cmall.ordercenter.model;

import java.math.BigDecimal;


/**
 * 网易考拉确认订单接口请求参数
 * @author cc
 *
 */
public class OrderItemList {
	
	/**
	 * 商品id	该id为商品在考拉的商品id
	 */
	private String goodsId;
	
	/**
	 * 商品skuId	该skuId为商品在考拉的skuId
	 */
	private String skuId;
	
	/**
	 * 购买数量
	 */
	private int buyAmount;
	
	/**
	 * 税前价
	 */
	private BigDecimal channelSalePrice;
	
	/**
	 * 仓库ID
	 */
	private String warehouseId;
	


	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}

	public int getBuyAmount() {
		return buyAmount;
	}

	public void setBuyAmount(int buyAmount) {
		this.buyAmount = buyAmount;
	}

	public BigDecimal getChannelSalePrice() {
		return channelSalePrice;
	}

	public void setChannelSalePrice(BigDecimal channelSalePrice) {
		this.channelSalePrice = channelSalePrice;
	}

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}
	
}
