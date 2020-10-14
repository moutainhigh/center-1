package com.cmall.ordercenter.model.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.ordercenter.model.FSkuPrice;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ApiGetFpriceBySkucResult extends RootResultWeb {


	@ZapcomApi(value="闪购商品价格集合",demo="[]")
	private List<FSkuPrice> skuPrices=new ArrayList<FSkuPrice>(1);

	public List<FSkuPrice> getSkuPrices() {
		return skuPrices;
	}

	public void setSkuPrices(List<FSkuPrice> skuPrices) {
		this.skuPrices = skuPrices;
	} 
	
	
}
