package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;
/**
 * 使用商品返回数据
 * @author jl
 *
 */
public class ApiTryOrderServiceResult extends RootResult {

	@ZapcomApi(value = "ID")
	private String id= "";
	@ZapcomApi(value = "订单号",demo="DD140119100002")
	private String order_id="";// 订单号
	@ZapcomApi(value = "订单描述",demo="")
	private String order_description="";//订单描述
	@ZapcomApi(value = "商品信息")
	private List<ProductGroup> products=new ArrayList<ProductGroup>();// 商品及数量
	@ZapcomApi(value = "使用积分的个数",demo="3")
	private int total=0;// 总价
	
//	"DELIVERING"		: 0,	// 发货中
//	"DELIVERED"			: 1,	// 待评价
//	"DONE"				: 2		// 完成
	@ZapcomApi(value = "状态",demo="0")
	private String state="0";// 状态
	@ZapcomApi(value = "创建时间	",demo="2009/07/07 21:51:22")
	private String create_time="";//创建时间	
	
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

	public String getOrder_description() {
		return order_description;
	}

	public void setOrder_description(String order_description) {
		this.order_description = order_description;
	}

	public List<ProductGroup> getProducts() {
		return products;
	}

	public void setProducts(List<ProductGroup> products) {
		this.products = products;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	
}
