package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncBase;
import com.cmall.groupcenter.homehas.model.RsyncRequestModRtnBankStatus;
import com.cmall.groupcenter.homehas.model.RsyncResponseModRtnBankStatus;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 修改LD系统退款单状态
 */
public class RsyncModRtnBankStatus extends RsyncHomeHas<RsyncModRtnBankStatus.RsyncConfig, RsyncRequestModRtnBankStatus, RsyncResponseModRtnBankStatus> {

	final static RsyncConfig CONFIG = new RsyncConfig();
	
	private RsyncRequestModRtnBankStatus tRequest = new RsyncRequestModRtnBankStatus();
	private RsyncResponseModRtnBankStatus tResponse = new RsyncResponseModRtnBankStatus();

	public RsyncConfig upConfig() {
		return CONFIG;
	}
	
	public RsyncRequestModRtnBankStatus upRsyncRequest() {
		return tRequest;
	}
	
	public RsyncResult doProcess(RsyncRequestModRtnBankStatus tRequest, RsyncResponseModRtnBankStatus tResponse) {
		this.tResponse = tResponse;
		
		RsyncResult result = new RsyncResult();
		if (!tResponse.isSuccess()) {
			result.inErrorMessage(918501003);
		}
		return result;
	}

	public RsyncResponseModRtnBankStatus upResponseObject() {
		return tResponse;
	}
	
	public static class RsyncConfig extends RsyncConfigRsyncBase {

		@Override
		public String getRsyncTarget() {
			return "modRtnBankStatus";
		}
	}

}
