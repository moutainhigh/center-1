package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品信息查询接口的响应信息
 * @author jlin
 *
 */
public class RsyncResponseSyncGoodsById extends RsyncResponseBase {

	
	private List<RsyncModelGoods> result = new ArrayList<RsyncModelGoods>();

	public List<RsyncModelGoods> getResult() {
		return result;
	}

	public void setResult(List<RsyncModelGoods> result) {
		this.result = result;
	}

}
