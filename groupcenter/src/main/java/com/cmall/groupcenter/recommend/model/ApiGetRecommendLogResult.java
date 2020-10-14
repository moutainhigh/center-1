package com.cmall.groupcenter.recommend.model;

import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetRecommendLogResult extends RootResult{
	
	@ZapcomApi(value="是否有推荐",remark="1:推荐过,0:没有推荐过")
	private String bound_status;
	
	@ZapcomApi(value="绑定状态",remark="会员手机号")
	private List<String> mobile;

	public String getBound_status() {
		return bound_status;
	}

	public void setBound_status(String bound_status) {
		this.bound_status = bound_status;
	}

	public List<String> getMobile() {
		return mobile;
	}

	public void setMobile(List<String> mobile) {
		this.mobile = mobile;
	}
	
	
}
