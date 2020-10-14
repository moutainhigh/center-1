package com.cmall.groupcenter.kjt.response;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.kjt.RsyncKjtResponseBase;
import com.cmall.groupcenter.kjt.model.RsyncModelGetKjtProductChange;

/**
 * 商品信息返回接口
 * 
 * @author liqt
 * 
 */
public class RsyncResponseGetKjtProductChangeById extends RsyncKjtResponseBase {

	private Data Data = new Data();

	public Data getData() {
		return Data;
	}

	public void setData(Data Data) {
		this.Data = Data;
	}
	public static class Data {
		/**
		 * 满足条件的总数，不受LimitRows的限制。
		 */
		private int Total=0;
		public int getTotal() {
			return Total;
		}

		public void setTotal(int total) {
			Total = total;
		}

		private List<RsyncModelGetKjtProductChange> ProductPriceList = new ArrayList<RsyncModelGetKjtProductChange>();
		public List<RsyncModelGetKjtProductChange> getProductPriceList() {
			return ProductPriceList;
		}

		public void setProductPriceList(
				List<RsyncModelGetKjtProductChange> productPriceList) {
			ProductPriceList = productPriceList;
		}


	}
}

