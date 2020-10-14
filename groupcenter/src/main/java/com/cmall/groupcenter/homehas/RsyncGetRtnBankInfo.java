package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncBase;
import com.cmall.groupcenter.homehas.model.RsyncRequestGetRtnBankInfo;
import com.cmall.groupcenter.homehas.model.RsyncResponseGetRtnBankInfo;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 查询LD订单待退款的明细
 */
public class RsyncGetRtnBankInfo extends RsyncHomeHas<RsyncGetRtnBankInfo.RsyncConfig, RsyncRequestGetRtnBankInfo, RsyncResponseGetRtnBankInfo> {

	final static RsyncConfig CONFIG = new RsyncConfig();
	
	private RsyncRequestGetRtnBankInfo tRequest = new RsyncRequestGetRtnBankInfo();
	private RsyncResponseGetRtnBankInfo tResponse = new RsyncResponseGetRtnBankInfo();

	public RsyncConfig upConfig() {
		return CONFIG;
	}
	
	public RsyncRequestGetRtnBankInfo upRsyncRequest() {
		return tRequest;
	}
	
	public RsyncResult doProcess(RsyncRequestGetRtnBankInfo tRequest, RsyncResponseGetRtnBankInfo tResponse) {
		this.tResponse = tResponse;
		
		RsyncResult result = new RsyncResult();
		if (!tResponse.isSuccess()) {
			result.inErrorMessage(918501003);
		}
		return result;
	}

	public RsyncResponseGetRtnBankInfo upResponseObject() {
		return tResponse;
	}
	
	public static class RsyncConfig extends RsyncConfigRsyncBase {

		@Override
		public String getRsyncTarget() {
			return "getRtnBankInfo";
		}
	}

}
