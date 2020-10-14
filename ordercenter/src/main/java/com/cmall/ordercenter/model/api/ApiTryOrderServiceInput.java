package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 试用商品输入参数
 * @author jl
 *
 */
public class ApiTryOrderServiceInput extends RootInput {

	/**
	 * 买家编号
	 */
	@ZapcomApi(value="买家编号",require=1)
	private String buyerCode="";
	
	/**
	 * APP编号
	 */
	@ZapcomApi(value="APP编号")
	private String appCode="";
	
	/**
	 * SKU编号
	 */
	@ZapcomApi(value="SKU编号",require=1)
	private String skuCode="";
	
	/**
	 * 订单地址编号
	 */
	@ZapcomApi(value="地址编号",require=1)
	private String address_id="";
	
	@ZapcomApi(value="商品数量",require=1)
	private int amount=1;

	
	public String getBuyerCode() {
		return buyerCode;
	}

	public void setBuyerCode(String buyerCode) {
		this.buyerCode = buyerCode;
	}

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	public String getAddress_id() {
		return address_id;
	}

	public void setAddress_id(String address_id) {
		this.address_id = address_id;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	
	
//	/**
//	 * 订单地址
//	 */
//	@ZapcomApi(value="订单地址")
//	private OrderAddress address = null;
	
//	/***
//	 * 下单时间
//	 */
//	@ZapcomApi(value="下单时间",demo="2014-06-29 11:07:24")
//	private String orderTime = "";
//	
//	/**
//	 * 订单来源       值				说明
//	 *          449715190001	正常订单
//	 *          449715190002	android订单
//	 *          449715190003	ios订单
//	 *          449715190004	网站手机订单
//	 */
//	@ZapcomApi(value="订单来源值说明",remark = "订单来源,可选值:449715190001(正常订单)，449715190002(android订单),449715190003(ios订单),449715190004(网站手机订单)。", demo = "449715190003", verify = { "in=449715190001,449715190002,449715190003,449715190004" })
//	private String orderSource = "";
//	
//	/**
//	 * 订单类型	值				说明
//	 * 			449715200001	商城订单
//	 * 			449715200002	好物产订单
//	 */
//	@ZapcomApi(value="订单类型值说明",remark = "订单来源,可选值:449715200001(商城订单)，449715200002(好物产订单)。", demo = "449715200001", verify = { "in=449715200001,449715200002" })
//	private String orderType = "";
//	
//	/**
//	 * 配送方式 值  说明
//	 * 449715210001	快递
//	 * 449715210002	邮局
//	 */
//	@ZapcomApi(value="配送方式 值  说明",remark = "订单来源,可选值:449715210001(商城订单)，449715210002(邮局)。", demo = "449715210002", verify = { "in=449715210001,449715210002" })
//	private String sendType = "";

	
	
}
