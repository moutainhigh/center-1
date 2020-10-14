package com.cmall.productcenter.model.api;

import com.cmall.productcenter.model.GoodsProduct;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

import java.util.ArrayList;
import java.util.List;

public class ApiForEventGoodsProductResult extends RootResultWeb {
	@ZapcomApi(value="商品基本数据")
	List<GoodsProduct> listPro = new ArrayList<GoodsProduct>();
	
	/**
	 * @return the listPro
	 */
	public List<GoodsProduct> getListPro() {
		return listPro;
	}

	/**
	 * @param listPro the listPro to set
	 */
	public void setListPro(List<GoodsProduct> listPro) {
		this.listPro = listPro;
	}
	
	
}
