package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 提交订单-》收货人地址信息输出入列表
 * @author houwen
 *
 */
public class OrderAddress {
	
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
	
}
