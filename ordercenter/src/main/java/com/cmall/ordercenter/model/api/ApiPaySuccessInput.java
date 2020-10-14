package com.cmall.ordercenter.model.api;

import com.cmall.ordercenter.model.OcOrderPay;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiPaySuccessInput extends RootInput {

	/**
	 * 支付的信息
	 */
	@ZapcomApi(value="支付的信息")
	private OcOrderPay pay =  new OcOrderPay();

	public OcOrderPay getPay() {
		return pay;
	}

	public void setPay(OcOrderPay pay) {
		this.pay = pay;
	}
	
}
