package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;

/**
 * 订单状态改变接口响应报文
 * @author renhongbin
 */
public class ApiOrderStatusChangeNoticResult extends RootResult{

	@ZapcomApi(value="true成功")
	private boolean success = false;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
}
