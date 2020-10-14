package com.cmall.ordercenter.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 换货信息返回结果（主信息和明细信息）
 * 项目名称：ordercenter 
 * 类名称：     ApiGetExchangegoodsResultChild 
 * 类描述：     换货信息对象
 * 创建人：     gaoy  
 * 创建时间：2013年9月25日下午8:05:24 
 * 修改人：     gaoy
 * 修改时间：2013年9月25日下午8:05:24
 * 修改备注：  
 * @version
 */
public class ApiGetExchangegoodsResultChild  extends ApiGetExchangegoodsResult{

	/**
	 * 换货信息（主信息和明细信息）
	 */
	private List<ExchangegoodsModelChild> exchangeGoods = new ArrayList<ExchangegoodsModelChild>();

	public List<ExchangegoodsModelChild> getExchangeGoods() {
		return exchangeGoods;
	}

	public void setExchangeGoods(List<ExchangegoodsModelChild> exchangeGoods) {
		this.exchangeGoods = exchangeGoods;
	}
}
