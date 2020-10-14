package com.cmall.groupcenter.recommend.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;

/**
 * 推荐联系人输出参数
 * @author fq
 *
 */
public class ApiRecommendContactsResult extends RootResult{

	@ZapcomApi(value="发送失败的手机号",remark="提现金额")
	private List<String> error_tels= new ArrayList<String>();
	
	@ZapcomApi(value="发送成功的手机号",remark="提现金额")
	private List<String> success_tels= new ArrayList<String>();

	public List<String> getError_tels() {
		return error_tels;
	}

	public void setError_tels(List<String> error_tels) {
		this.error_tels = error_tels;
	}

	public List<String> getSuccess_tels() {
		return success_tels;
	}

	public void setSuccess_tels(List<String> success_tels) {
		this.success_tels = success_tels;
	}
	
	
}
