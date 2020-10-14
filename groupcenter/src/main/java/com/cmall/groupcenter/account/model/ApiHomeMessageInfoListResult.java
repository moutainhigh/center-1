package com.cmall.groupcenter.account.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 用户客户端消息列表信息
 * @author wz
 *
 */
public class ApiHomeMessageInfoListResult extends RootResultWeb{
	@ZapcomApi(value="用户客户端消息列表信息")
	private List<ApiHomeMessageInfoResult> apiHomeCustTrackingListResult = new ArrayList<ApiHomeMessageInfoResult>();
	@ZapcomApi(value="总页数",remark="返回总页数")
	private int pageNum;

	
	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public List<ApiHomeMessageInfoResult> getApiHomeCustTrackingListResult() {
		return apiHomeCustTrackingListResult;
	}

	public void setApiHomeCustTrackingListResult(List<ApiHomeMessageInfoResult> apiHomeCustTrackingListResult) {
		this.apiHomeCustTrackingListResult = apiHomeCustTrackingListResult;
	}
	
	public void add(ApiHomeMessageInfoResult info){
		apiHomeCustTrackingListResult.add(info);
	}

	
	
	

}
