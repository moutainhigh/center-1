package com.cmall.groupcenter.recommend.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetRecommendListResult  extends RootResult{

	@ZapcomApi(value="是否有推荐",remark="1:推荐过,0:没有推荐过")
	private String bound_status = "0";
	
	@ZapcomApi(value="推荐手机号") 
	private List<ApiGetRecommendListResultModel> mobileList = new ArrayList<ApiGetRecommendListResultModel>();
	
	@ZapcomApi(value="返利总额度")
	private Integer rtnCoupons = 0;

	@ZapcomApi(value="返卷总数目")
	private Integer rtnSum = 0;
	
	public Integer getRtnSum() {
		return rtnSum;
	}

	public void setRtnSum(Integer rtnSum) {
		this.rtnSum = rtnSum;
	}

	public String getBound_status() {
		return bound_status;
	}

	public void setBound_status(String bound_status) {
		this.bound_status = bound_status;
	}

	public List<ApiGetRecommendListResultModel> getMobileList() {
		return mobileList;
	}

	public void setMobileList(List<ApiGetRecommendListResultModel> mobileList) {
		this.mobileList = mobileList;
	}

	public Integer getRtnCoupons() {
		return rtnCoupons;
	}

	public void setRtnCoupons(Integer rtnCoupons) {
		this.rtnCoupons = rtnCoupons;
	}

	

	
}
