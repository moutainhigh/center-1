package com.cmall.newscenter.beauty.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.SaleProductGroup;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class OrderForLastResult extends RootResultWeb{
	@ZapcomApi(value="订单ID",demo="1")
	private String id = "";
	
	@ZapcomApi(value="订单号",demo="14245")
	private String order_id ="";

	@ZapcomApi(value="商品及数量")
	private List<SaleProductGroup> products = new ArrayList<SaleProductGroup>();
	
	@ZapcomApi(value="总价",demo="￥529.00")
	private BigDecimal total = new BigDecimal(0.00);
	
	@ZapcomApi(value="状态")
	private int state = 0;

	@ZapcomApi(value="创建时间",demo="2009/07/07 21:51:22")
    private String create_time ="";
	
	@ZapcomApi(value="订单描述")
	private String order_description="";

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

	public List<SaleProductGroup> getProducts() {
		return products;
	}

	public void setProducts(List<SaleProductGroup> products) {
		this.products = products;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getOrder_description() {
		return order_description;
	}

	public void setOrder_description(String order_description) {
		this.order_description = order_description;
	}
}
