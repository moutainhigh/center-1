package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncBase;
import com.cmall.groupcenter.homehas.model.RsyncRequestCheckDlvPay;
import com.cmall.groupcenter.homehas.model.RsyncResponseCheckDlvPay;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 4.55.查询是否支持货到付款接口
 */
public class RsyncCheckDlvPay extends RsyncHomeHas<RsyncCheckDlvPay.RsyncConfigCheckDlvPay, RsyncRequestCheckDlvPay, RsyncResponseCheckDlvPay> {

	final static RsyncConfigCheckDlvPay CONFIG = new RsyncConfigCheckDlvPay();
	
	private RsyncRequestCheckDlvPay tRequest = new RsyncRequestCheckDlvPay();
	private RsyncResponseCheckDlvPay tResponse = new RsyncResponseCheckDlvPay();

	public RsyncConfigCheckDlvPay upConfig() {
		return CONFIG;
	}
	
	public RsyncRequestCheckDlvPay upRsyncRequest() {
		return tRequest;
	}
	
	public RsyncResult doProcess(RsyncRequestCheckDlvPay tRequest, RsyncResponseCheckDlvPay tResponse) {
		this.tResponse = tResponse;
		
		RsyncResult result = new RsyncResult();
		if (!tResponse.isSuccess()) {
			result.inErrorMessage(918501003);
		}
		return result;
	}

	public RsyncResponseCheckDlvPay upResponseObject() {
		return tResponse;
	}
	
	public static class RsyncConfigCheckDlvPay extends RsyncConfigRsyncBase {

		@Override
		public String getRsyncTarget() {
			return "checkDlvPay";
		}
	}

}
