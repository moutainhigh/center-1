package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncBase;
import com.cmall.groupcenter.homehas.model.RsyncRequestGetCustRelAmt;
import com.cmall.groupcenter.homehas.model.RsyncResponseGetCustExpireAccm;
import com.cmall.groupcenter.homehas.model.RsyncResponseGetCustRelAmt;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 查询客户将要过期的积分
 */
public class RsyncGetCustExpireAccm extends RsyncHomeHas<RsyncGetCustExpireAccm.RsyncConfigGetCustExpireAccm, RsyncRequestGetCustRelAmt, RsyncResponseGetCustExpireAccm> {

	final static RsyncConfigGetCustExpireAccm CONFIG = new RsyncConfigGetCustExpireAccm();
	
	private RsyncRequestGetCustRelAmt tRequest = new RsyncRequestGetCustRelAmt();
	private RsyncResponseGetCustExpireAccm tResponse = new RsyncResponseGetCustExpireAccm();

	public RsyncConfigGetCustExpireAccm upConfig() {
		return CONFIG;
	}
	
	public RsyncRequestGetCustRelAmt upRsyncRequest() {
		return tRequest;
	}
	
	public RsyncResult doProcess(RsyncRequestGetCustRelAmt tRequest, RsyncResponseGetCustExpireAccm tResponse) {
		this.tResponse = tResponse;
		
		RsyncResult result = new RsyncResult();
		if (!tResponse.isSuccess()) {
			result.inErrorMessage(918501003);
		}
		return result;
	}

	public RsyncResponseGetCustExpireAccm upResponseObject() {
		return tResponse;
	}
	
	public static class RsyncConfigGetCustExpireAccm extends RsyncConfigRsyncBase {

		@Override
		public String getRsyncTarget() {
			return "getCustExpireAccm";
		}
	}

}
