package com.cmall.groupcenter.account.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 订单轨迹
 * @author wz
 *
 */
public class ApiHomeOrderTrackingResult extends RootResultWeb{
	@ZapcomApi(value="运单号")
	private String yc_express_num="";
	@ZapcomApi(value="送货商名称")
	private String yc_delivergoods_user_name="";
	@ZapcomApi(value="物流信息温馨提示")
	private String logisticsTips="";
	@ZapcomApi(value="订单跟踪信息")
	private List<ApiHomeOrderTrackingListResult> apiHomeOrderTrackingListResult = new ArrayList<ApiHomeOrderTrackingListResult>();
	
	
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

}
