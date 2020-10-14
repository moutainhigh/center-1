package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 短信推荐输入参数
 * @author fq
 *
 */
public class SmsRecommendationInput extends RootInput{
	
	@ZapcomApi(value = "推荐人手机号",verify="regex=^1[0-9]{10}",demo="13000000000",require=1)
	private String mobile = "";
	
	@ZapcomApi(value = "被推荐人手机号",verify="regex=^1[0-9]{10}",demo="13000000000",require=1)
	private String recommendMobile = "";

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getRecommendMobile() {
		return recommendMobile;
	}

	public void setRecommendMobile(String recommendMobile) {
		this.recommendMobile = recommendMobile;
	}
	
	
	
}
