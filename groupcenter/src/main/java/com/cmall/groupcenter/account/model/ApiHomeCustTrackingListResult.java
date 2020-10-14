package com.cmall.groupcenter.account.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 用户批量订单轨迹
 * @author wz
 *
 */
public class ApiHomeCustTrackingListResult{
	@ZapcomApi(value="运单号")
	private String yc_express_num="";
	@ZapcomApi(value="送货商名称")
	private String yc_delivergoods_user_name="";
	@ZapcomApi(value="物流信息温馨提示")
	private String logisticsTips="";
	@ZapcomApi(value="订单跟踪信息")
	private List<ApiHomeOrderTrackingListResult> apiHomeOrderTrackingListResult = new ArrayList<ApiHomeOrderTrackingListResult>();
	@ZapcomApi(value="订单号",remark="")
	private String order_code = "";
	@ZapcomApi(value="订单来源",remark="0:非LD下单订单 1:LD下单订单")
	private String order_source = "0";
	@ZapcomApi(value="每个订单的商品信息",remark="")
	private List<ApiSellerListResult> apiSellerList = new ArrayList<ApiSellerListResult>();
	
	
	public String getOrder_source() {
		return order_source;
	}

	public void setOrder_source(String order_source) {
		this.order_source = order_source;
	}

	public String getYc_express_num() {
		return yc_express_num;
	}

	public void setYc_express_num(String yc_express_num) {
		this.yc_express_num = yc_express_num;
	}

	public String getYc_delivergoods_user_name() {
		return yc_delivergoods_user_name;
	}

	public void setYc_delivergoods_user_name(String yc_delivergoods_user_name) {
		this.yc_delivergoods_user_name = yc_delivergoods_user_name;
	}

	public List<ApiHomeOrderTrackingListResult> getApiHomeOrderTrackingListResult() {
		return apiHomeOrderTrackingListResult;
	}

	public void setApiHomeOrderTrackingListResult(
			List<ApiHomeOrderTrackingListResult> apiHomeOrderTrackingListResult) {
		this.apiHomeOrderTrackingListResult = apiHomeOrderTrackingListResult;
	}

	public String getLogisticsTips() {
		return logisticsTips;
	}

	public void setLogisticsTips(String logisticsTips) {
		this.logisticsTips = logisticsTips;
	}

	public String getOrder_code() {
		return order_code;
	}

	public void setOrder_code(String order_code) {
		this.order_code = order_code;
	}

	public List<ApiSellerListResult> getApiSellerList() {
		return apiSellerList;
	}

	public void setApiSellerList(List<ApiSellerListResult> apiSellerList) {
		this.apiSellerList = apiSellerList;
	}

}
