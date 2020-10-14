package com.cmall.groupcenter.recommend.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 推荐联系人输入参数
 * @author fq
 *
 */
public class ApiRecommendContactsInput extends RootInput{
	
	@ZapcomApi(value="推荐人手机号",remark="会员手机号",require= 1)
	private String mobile = "";
	
	@ZapcomApi(value="被推荐人手机号",remark="多个手机号用‘，’号分割",require= 1,demo="13111111111,13111111111")
	private String tels = "";
	
	@ZapcomApi(value="app标识")
	private String app = "";

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTels() {
		return tels;
	}

	public void setTels(String tels) {
		this.tels = tels;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}
	
	
	
	
}
