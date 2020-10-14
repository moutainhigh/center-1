package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class APiCreateOrderInput extends RootInput {

	@ZapcomApi(value = "买家编号", remark = "可为空，默认取当前登录人的编号", demo = "123456")
	private String buyer_code = "";
	
	@ZapcomApi(value = "订单类型", remark = "449715200003试用订单、449715200004闪购订单、449715200005普通订单", require=1, demo = "449715200003" )
	private String order_type = "";
	
	@ZapcomApi(value = "订单来源", remark = "订单来源,可选值:449715190001(正常订单)，449715190002(android订单),449715190003(ios订单),449715190004(网站wap手机订单)", demo = "Android" )
	private String order_souce = "";
	
	@ZapcomApi(value = "商品列表", remark = "不可为空",require=1, demo = "")
	private List<GoodsInfoForAdd> goods = new ArrayList<GoodsInfoForAdd>();
	
	@ZapcomApi(value = "收货人姓名", remark = "收货人姓名",require=1, demo = "")
	private String buyer_name = "";
	
	@ZapcomApi(value = "收货人地址编号", remark = "收货人地址所在地区选择的第三级编号",require=1, demo = "")
	private String buyer_address_code = "";
	
	@ZapcomApi(value = "收货人地址", remark = "收货人地址",require=1, demo = "")
	private String buyer_address = "";
	
	@ZapcomApi(value = "收货人手机号", remark = "手机号", demo = "13333100204", require = 1, verify = {"base=mobile" })
	private String buyer_mobile = "";
	
	@ZapcomApi(value = "支付方式", remark = "支付方式",require=1, demo = "449716200001:在线支付,449716200002:货到付款")
	private String pay_type = "";
	
	@ZapcomApi(value = "应付款", remark = "应付款",require=1, demo = "8888.88")
	private double check_pay_money = 0.00;
	
	@ZapcomApi(value = "发票信息", remark = "发票信息",require=1, demo = "")
	private BillInfo billInfo = new BillInfo();
	
	@ZapcomApi(value = "app版本信息", remark = "app版本信息",require=1, demo = "1.0.0")
	private String app_vision = "";
	
	
	public String getBuyer_address_code() {
		return buyer_address_code;
	}

	public void setBuyer_address_code(String buyer_address_code) {
		this.buyer_address_code = buyer_address_code;
	}

	public String getBuyer_code() {
		return buyer_code;
	}

	public void setBuyer_code(String buyer_code) {
		this.buyer_code = buyer_code;
	}

	public String getBuyer_name() {
		return buyer_name;
	}

	public void setBuyer_name(String buyer_name) {
		this.buyer_name = buyer_name;
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

	public String getPay_type() {
		return pay_type;
	}

	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}

	public BillInfo getBillInfo() {
		return billInfo;
	}

	public void setBillInfo(BillInfo billInfo) {
		this.billInfo = billInfo;
	}

	public String getOrder_type() {
		return order_type;
	}

	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}

	public String getOrder_souce() {
		return order_souce;
	}

	public void setOrder_souce(String order_souce) {
		this.order_souce = order_souce;
	}

	public List<GoodsInfoForAdd> getGoods() {
		return goods;
	}

	public void setGoods(List<GoodsInfoForAdd> goods) {
		this.goods = goods;
	}

	public String getApp_vision() {
		return app_vision;
	}

	public void setApp_vision(String app_vision) {
		this.app_vision = app_vision;
	}

	public double getCheck_pay_money() {
		return check_pay_money;
	}

	public void setCheck_pay_money(double check_pay_money) {
		this.check_pay_money = check_pay_money;
	}

}
