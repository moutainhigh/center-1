package com.cmall.newscenter.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
/**
 * 订单-已试用订单类
 * @author liqiang
 * date 2014-7-22
 * @version 1.0
 */
public class TrialOrder {
 
	@ZapcomApi(value="订单ID",demo="1")
	private String id = "";
	
	@ZapcomApi(value="订单号",demo="14245")
	private String order_id = "";

	@ZapcomApi(value="商品及数量")
	private List<TrialProductGroup> products = new ArrayList<TrialProductGroup>();
	
	@ZapcomApi(value="总价",demo="￥529.00")
	private double total = 0;
	
	@ZapcomApi(value="状态")
	private int state = 0;

	@ZapcomApi(value="创建时间",demo="2009/07/07 21:51:22")
	private String create_time = "";
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public List<TrialProductGroup> getProducts() {
		return products;
	}

	public void setProducts(List<TrialProductGroup> products) {
		this.products = products;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}
	

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}


}
