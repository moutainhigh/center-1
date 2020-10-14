package com.cmall.groupcenter.kjt.response;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.kjt.RsyncKjtResponseBase;
import com.cmall.groupcenter.kjt.model.RsyncModelGetKjtProduct;
import com.cmall.groupcenter.kjt.model.RsyncModelGetKjtProductPrice;

/**
 * 商品价格信息返回接口
 * 
 * @author liqt
 * 
 */
public class RsyncResponseGetKjtProductPriceById extends RsyncKjtResponseBase {

	private Data Data = new Data();

	public Data getData() {
		return Data;
	}

	public void setData(Data Data) {
		this.Data = Data;
	}
	public static class Data {
		private List<RsyncModelGetKjtProductPrice> ProductPriceList = new ArrayList<RsyncModelGetKjtProductPrice>();

		public List<RsyncModelGetKjtProductPrice> getProductPriceList() {
			return ProductPriceList;
		}

		public void setProductPriceList(
				List<RsyncModelGetKjtProductPrice> productPriceList) {
			ProductPriceList = productPriceList;
		}

	
	}
}

