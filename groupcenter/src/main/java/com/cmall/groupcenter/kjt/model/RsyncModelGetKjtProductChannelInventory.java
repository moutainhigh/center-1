package com.cmall.groupcenter.kjt.model;

import java.io.Serializable;

/**
 * 商品分销渠道库存批量获取
 * 
 * @author zmm
 *
 */
public class RsyncModelGetKjtProductChannelInventory implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 商品库存列表
	 */
	// private List<ProductDetaily> productList = new
	// ArrayList<ProductDetaily>();
	/**
	 * 商品编号
	 */
	private String ProductID = "";
	/**
	 * 可销售库存
	 */
	private int OnlineQty = 0;
	/**
	 * 出库仓 仓库编号
	 */
	private int WareHouseID = 0;

	public String getProductID() {
		return ProductID;
	}

	public void setProductID(String productID) {
		ProductID = productID;
	}

	public int getOnlineQty() {
		return OnlineQty;
	}

	public void setOnlineQty(int onlineQty) {
		OnlineQty = onlineQty;
	}

	public int getWareHouseID() {
		return WareHouseID;
	}

	public void setWareHouseID(int wareHouseID) {
		WareHouseID = wareHouseID;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
