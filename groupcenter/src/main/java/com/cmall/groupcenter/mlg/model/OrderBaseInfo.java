package com.cmall.groupcenter.mlg.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class OrderBaseInfo{

	@ZapcomApi(value = "收货人姓名 ", remark = "")
	String consignee = "";

	@ZapcomApi(value = "所在地区", remark = "第三级编号")
	String region = "";

	@ZapcomApi(value = "详细地址 ", remark = "")
	String address = "";

	@ZapcomApi(value = "手机号码 ", remark = "")
	String phone = "";
	
	@ZapcomApi(value = "姓名 ", remark = "")
	String name = "";
	
	@ZapcomApi(value = "证件号 ", remark = "")
	String IDNumber = "";

	@ZapcomApi(value = "订单号 ", remark = "")
	String order_id = "";

	@ZapcomApi(value = "购买时间 ", remark = "")
	String ctime = "";

	@ZapcomApi(value = "支付时间 ", remark = "yyyy-MM-dd hh:mm:ss")
	String pay_time = "";

	@ZapcomApi(value = "订单总金额 ", remark = "")
	Double order_amount = 0.0;
	
	@ZapcomApi(value = "订单商品 ", remark = "")
	List<OrderDetailInfo> goods = new ArrayList<OrderDetailInfo>();

	@ZapcomApi(value = "省", remark = "名称")
	String province = "";
	@ZapcomApi(value = "市", remark = "名称")
	String city = "";
	@ZapcomApi(value = "区县", remark = "名称")
	String area = "";
	
	public String getIDNumber() {
		return IDNumber;
	}

	public void setIDNumber(String iDNumber) {
		IDNumber = iDNumber;
	}

	public String getConsignee() {
		return consignee;
	}

	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public String getPay_time() {
		return pay_time;
	}

	public void setPay_time(String pay_time) {
		this.pay_time = pay_time;
	}

	public Double getOrder_amount() {
		return order_amount;
	}

	public void setOrder_amount(Double order_amount) {
		this.order_amount = order_amount;
	}

	public List<OrderDetailInfo> getGoods() {
		return goods;
	}

	public void setGoods(List<OrderDetailInfo> goods) {
		this.goods = goods;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}
	
}
