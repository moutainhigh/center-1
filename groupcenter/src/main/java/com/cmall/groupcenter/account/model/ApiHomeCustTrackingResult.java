package com.cmall.groupcenter.account.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 用户订单轨迹
 * @author wz
 *
 */
public class ApiHomeCustTrackingResult extends RootResultWeb{
	@ZapcomApi(value="用户订单轨迹信息")
	private List<ApiHomeCustTrackingListResult> apiHomeCustTrackingListResult = new ArrayList<ApiHomeCustTrackingListResult>();
	@ZapcomApi(value="签收未评价的订单数据")
	private List<ApiHomeNoEvaluationOrderListResult> apiHomeNoEvaluationOrderListResult = new ArrayList<ApiHomeNoEvaluationOrderListResult>();

	public List<ApiHomeCustTrackingListResult> getApiHomeCustTrackingListResult() {
		return apiHomeCustTrackingListResult;
	}

	public void setApiHomeCustTrackingListResult(List<ApiHomeCustTrackingListResult> apiHomeCustTrackingListResult) {
		this.apiHomeCustTrackingListResult = apiHomeCustTrackingListResult;
	}

	public List<ApiHomeNoEvaluationOrderListResult> getApiHomeNoEvaluationOrderListResult() {
		return apiHomeNoEvaluationOrderListResult;
	}

	public void setApiHomeNoEvaluationOrderListResult(
			List<ApiHomeNoEvaluationOrderListResult> apiHomeNoEvaluationOrderListResult) {
		this.apiHomeNoEvaluationOrderListResult = apiHomeNoEvaluationOrderListResult;
	}
	
	

}
