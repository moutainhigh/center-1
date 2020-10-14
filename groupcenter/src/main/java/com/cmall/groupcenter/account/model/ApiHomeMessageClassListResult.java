package com.cmall.groupcenter.account.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 客户端消息分类列表
 * @author sunyan
 *
 */
public class ApiHomeMessageClassListResult extends RootResultWeb{
	@ZapcomApi(value="客户端消息分类列表")
	private List<ApiHomeMessageClassResult> apiHomeCustTrackingListResult = new ArrayList<ApiHomeMessageClassResult>();

	public List<ApiHomeMessageClassResult> getApiHomeCustTrackingListResult() {
		return apiHomeCustTrackingListResult;
	}

	public void setApiHomeCustTrackingListResult(List<ApiHomeMessageClassResult> apiHomeCustTrackingListResult) {
		this.apiHomeCustTrackingListResult = apiHomeCustTrackingListResult;
	}
	
	public void add(ApiHomeMessageClassResult info){
		apiHomeCustTrackingListResult.add(info);
	}

	
	
	

}
