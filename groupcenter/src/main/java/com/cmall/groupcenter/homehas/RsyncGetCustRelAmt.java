package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncBase;
import com.cmall.groupcenter.homehas.model.RsyncRequestGetCustRelAmt;
import com.cmall.groupcenter.homehas.model.RsyncResponseGetCustRelAmt;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 查询客户积分、储值金、暂存款、惠币查询接口
 */
public class RsyncGetCustRelAmt extends RsyncHomeHas<RsyncGetCustRelAmt.RsyncConfigGetCustRelAmt, RsyncRequestGetCustRelAmt, RsyncResponseGetCustRelAmt> {

	final static RsyncConfigGetCustRelAmt CONFIG = new RsyncConfigGetCustRelAmt();
	
	private RsyncRequestGetCustRelAmt tRequest = new RsyncRequestGetCustRelAmt();
	private RsyncResponseGetCustRelAmt tResponse = new RsyncResponseGetCustRelAmt();

	public RsyncConfigGetCustRelAmt upConfig() {
		return CONFIG;
	}
	
	public RsyncRequestGetCustRelAmt upRsyncRequest() {
		return tRequest;
	}
	
	public RsyncResult doProcess(RsyncRequestGetCustRelAmt tRequest, RsyncResponseGetCustRelAmt tResponse) {
		this.tResponse = tResponse;
		
		RsyncResult result = new RsyncResult();
		if (!tResponse.isSuccess()) {
			result.inErrorMessage(918501003);
		}
		return result;
	}

	public RsyncResponseGetCustRelAmt upResponseObject() {
		return tResponse;
	}
	
	public static class RsyncConfigGetCustRelAmt extends RsyncConfigRsyncBase {

		@Override
		public String getRsyncTarget() {
			return "getCustRelAmt";
		}
	}

}
