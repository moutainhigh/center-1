/**
 * Project Name:ordercenter
 * File Name:RetExchGoodsResult.java
 * Package Name:com.cmall.ordercenter.model
 * Date:2013年11月7日下午1:37:39
 *
*/

package com.cmall.ordercenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.topapi.RootResult;

/**
 * ClassName:RetExchGoodsResult <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2013年11月7日 下午1:37:39 <br/>
 * @author   hxd
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class RetExchGoodsResult extends RootResult{
	private Order order = new Order();
	private List<RetuGoodDetail> returnGoods = new ArrayList<RetuGoodDetail>();
	private List<ExchangegoodsDetailModelChild> exchangeGoodDetail = new ArrayList<ExchangegoodsDetailModelChild>();
	public List<RetuGoodDetail> getReturnGoods() {
		return returnGoods;
	}
	public void setReturnGoods(List<RetuGoodDetail> returnGoods) {
		this.returnGoods = returnGoods;
	}
	public List<ExchangegoodsDetailModelChild> getExchangeGoodDetail() {
		return exchangeGoodDetail;
	}
	public void setExchangeGoodDetail(
			List<ExchangegoodsDetailModelChild> exchangeGoodDetail) {
		this.exchangeGoodDetail = exchangeGoodDetail;
	}
	public RetExchGoodsResult(List<RetuGoodDetail> returnGoods,
			List<ExchangegoodsDetailModelChild> exchangeGoodDetail) {
		super();
		this.returnGoods = returnGoods;
		this.exchangeGoodDetail = exchangeGoodDetail;
	}
	public RetExchGoodsResult() {
		
		super();
		// TODO Auto-generated constructor stub
		
	}
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}
	public RetExchGoodsResult(Order order, List<RetuGoodDetail> returnGoods,
			List<ExchangegoodsDetailModelChild> exchangeGoodDetail) {
		super();
		this.order = order;
		this.returnGoods = returnGoods;
		this.exchangeGoodDetail = exchangeGoodDetail;
	}
	
	
	
}

