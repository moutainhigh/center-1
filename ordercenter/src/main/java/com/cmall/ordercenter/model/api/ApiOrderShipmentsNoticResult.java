package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;

/**
 * 订单发货通知接口响应报文
 * @author renhongibn
 */
public class ApiOrderShipmentsNoticResult extends RootResult {

	@ZapcomApi(value="true成功")
	private boolean success;

	public boolean getSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}
