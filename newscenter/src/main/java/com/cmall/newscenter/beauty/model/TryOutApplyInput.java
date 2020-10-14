package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 申请试用输入类
 * @author houwen
 * date 2014-10-14
 * @version 1.0
 */
public class TryOutApplyInput extends RootInput {


	@ZapcomApi(value="活动编号",remark="",demo="2243434",require=1)
	private String activityCode = "";
	
	@ZapcomApi(value="商品ID",remark="商品ID",demo="2243434",require=1)
	private String sku_code = "";
	
	@ZapcomApi(value="商品名称",remark="商品名称",demo="手提包",require=1)
	private String sku_name = "";
	
	@ZapcomApi(value="收货地址Id",remark="收货地址Id",demo="2243434",require=1)
	private String address_code = "";
	
	@ZapcomApi(value="订单来源",remark="订单来源:449715190001	正常订单;" +
			"449715190002	android订单;" +
			"449715190003	ios订单;" +
			"449715190004	网站手机订单",demo="2243434",require=1)
	private String orderSource = "";

	
	@ZapcomApi(value = "收货人姓名", remark = "收货人姓名",require=1, demo = "")
	private String buyer_name = "";
	
	@ZapcomApi(value = "收货人省市区", remark = "收货人地址所在地区选择的第三级编号",require=1, demo = "110105")
	private String area_code = "";
	
	@ZapcomApi(value = "收货人地址", remark = "收货人地址",require=1, demo = "北京市通州区XXX")
	private String buyer_address = "";
	
	@ZapcomApi(value = "收货人手机号", remark = "手机号", demo = "13333100204", require = 1, verify = {"base=mobile" })
	private String buyer_mobile = "";
	
	@ZapcomApi(value = "邮政编码", remark = "邮政编码", demo = "100100", require = 1)
	private String postCode="";
	
	@ZapcomApi(value = "试用商品结束时间", remark = "试用商品结束时间", demo = "20147-12-11 16：12：30", require = 1)
	private String end_time = "";
	
	public String getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public String getSku_name() {
		return sku_name;
	}

	public void setSku_name(String sku_name) {
		this.sku_name = sku_name;
	}

	public String getAddress_code() {
		return address_code;
	}

	public void setAddress_code(String address_code) {
		this.address_code = address_code;
	}

	public String getOrderSource() {
		return orderSource;
	}

	public void setOrderSource(String orderSource) {
		this.orderSource = orderSource;
	}

	public String getBuyer_name() {
		return buyer_name;
	}

	public void setBuyer_name(String buyer_name) {
		this.buyer_name = buyer_name;
	}

	public String getArea_code() {
		return area_code;
	}

	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}

	public String getBuyer_address() {
		return buyer_address;
	}

	public void setBuyer_address(String buyer_address) {
		this.buyer_address = buyer_address;
	}

	public String getBuyer_mobile() {
		return buyer_mobile;
	}

	public void setBuyer_mobile(String buyer_mobile) {
		this.buyer_mobile = buyer_mobile;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	
}
