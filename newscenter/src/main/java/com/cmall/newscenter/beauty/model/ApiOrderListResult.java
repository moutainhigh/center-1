package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.ordercenter.model.MicroMessagePayment;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 *  我的订单列表输出类
 * @author houwen	
 * date 2014-10-10
 * @version 1.0
 */
public class ApiOrderListResult extends RootResultWeb{

	@ZapcomApi(value="总页数")
	private int countPage;
	
	@ZapcomApi(value="当前页数")
	private int nowPage;
	
	@ZapcomApi(value="商品订单信息")
	private List<ApiSellerOrderListResult> sellerOrderList = new ArrayList<ApiSellerOrderListResult>();
	
	
	public List<ApiSellerOrderListResult> getSellerOrderList() {
		return sellerOrderList;
	}
	public void setSellerOrderList(List<ApiSellerOrderListResult> sellerOrderList) {
		this.sellerOrderList = sellerOrderList;
	}
	public int getCountPage() {
		return countPage;
	}
	public void setCountPage(int countPage) {
		this.countPage = countPage;
	}
	public int getNowPage() {
		return nowPage;
	}
	public void setNowPage(int nowPage) {
		this.nowPage = nowPage;
	}
	
}
