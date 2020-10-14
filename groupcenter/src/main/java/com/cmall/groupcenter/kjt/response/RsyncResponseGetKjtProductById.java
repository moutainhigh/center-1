package com.cmall.groupcenter.kjt.response;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.kjt.RsyncKjtResponseBase;
import com.cmall.groupcenter.kjt.model.RsyncModelGetKjtProduct;

/**
 * 商品信息返回接口
 * 
 * @author xiegj
 * 
 */
public class RsyncResponseGetKjtProductById extends RsyncKjtResponseBase {

	private Data Data = new Data();

	public Data getData() {
		return Data;
	}

	public void setData(Data Data) {
		this.Data = Data;
	}
	public static class Data {
		private int Total = 0;
		private List<RsyncModelGetKjtProduct> ProductList = new ArrayList<RsyncModelGetKjtProduct>();
		public int getTotal() {
			return Total;
		}
		public void setTotal(int total) {
			Total = total;
		}
		public List<RsyncModelGetKjtProduct> getProductList() {
			return ProductList;
		}
		public void setProductList(List<RsyncModelGetKjtProduct> productList) {
			ProductList = productList;
		}
	}
}

