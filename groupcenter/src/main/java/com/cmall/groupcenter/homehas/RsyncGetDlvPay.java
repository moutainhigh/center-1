package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncBase;
import com.cmall.groupcenter.homehas.model.RsyncRequestGetDlvPay;
import com.cmall.groupcenter.homehas.model.RsyncResponseGetDlvPay;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 4.56.查询货到付款地区配置信息接口
 */
public class RsyncGetDlvPay extends RsyncHomeHas<RsyncGetDlvPay.RsyncConfigGetDlvPay, RsyncRequestGetDlvPay, RsyncResponseGetDlvPay> {

	final static RsyncConfigGetDlvPay CONFIG = new RsyncConfigGetDlvPay();
	
	private RsyncRequestGetDlvPay tRequest = new RsyncRequestGetDlvPay();
	private RsyncResponseGetDlvPay tResponse = new RsyncResponseGetDlvPay();

	public RsyncConfigGetDlvPay upConfig() {
		return CONFIG;
	}
	
	public RsyncRequestGetDlvPay upRsyncRequest() {
		return tRequest;
	}
	
	public RsyncResult doProcess(RsyncRequestGetDlvPay tRequest, RsyncResponseGetDlvPay tResponse) {
		this.tResponse = tResponse;
		
		RsyncResult result = new RsyncResult();
		if (!tResponse.isSuccess()) {
			result.inErrorMessage(918501003);
		}
		return result;
	}

	public RsyncResponseGetDlvPay upResponseObject() {
		return tResponse;
	}
	
	public static class RsyncConfigGetDlvPay extends RsyncConfigRsyncBase {

		@Override
		public String getRsyncTarget() {
			return "getDlvPay";
		}
	}

}
