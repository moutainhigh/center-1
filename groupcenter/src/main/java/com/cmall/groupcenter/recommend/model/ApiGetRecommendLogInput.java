package com.cmall.groupcenter.recommend.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 推荐连接跳转下载页
 * @author fq
 *
 */
public class ApiGetRecommendLogInput  extends RootInput{
	
	@ZapcomApi(value="手机号",remark="会员手机号",require= 1)
	private String mobile = "";

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	
	
}
