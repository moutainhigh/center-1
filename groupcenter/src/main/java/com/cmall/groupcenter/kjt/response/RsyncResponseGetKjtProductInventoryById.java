package com.cmall.groupcenter.kjt.response;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.kjt.RsyncKjtResponseBase;
import com.cmall.groupcenter.kjt.model.RsyncModelGetKjtProductChannelInventory;

/**
 * 商品分销渠道库存批量获取
 * 
 * @author zmm
 *
 */
public class RsyncResponseGetKjtProductInventoryById extends RsyncKjtResponseBase {
	private List<Data> Data = new ArrayList<Data>();

	public List<Data> getData() {
		return Data;
	}

	public void setData(List<Data> data) {
		Data = data;
	}




	public static class Data {
//		private List<RsyncModelGetKjtProductChannelInventory> ItemList = new ArrayList<RsyncModelGetKjtProductChannelInventory>();
//
//		public List<RsyncModelGetKjtProductChannelInventory> getItemList() {
//			return ItemList;
//		}
//
//		public void setItemList(List<RsyncModelGetKjtProductChannelInventory> itemList) {
//			ItemList = itemList;
//		}
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
		private String OnlineQty = "";
		/**
		 * 出库仓 仓库编号
		 */
		private String WareHouseID = "";

		public String getOnlineQty() {
			return OnlineQty;
		}

		public void setOnlineQty(String onlineQty) {
			OnlineQty = onlineQty;
		}

		public String getWareHouseID() {
			return WareHouseID;
		}

		public void setWareHouseID(String wareHouseID) {
			WareHouseID = wareHouseID;
		}

		public String getProductID() {
			return ProductID;
		}

		public void setProductID(String productID) {
			ProductID = productID;
		}

	}
}
