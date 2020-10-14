package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncBase;
import com.cmall.groupcenter.homehas.model.RsyncRequestGetCustRelHb;
import com.cmall.groupcenter.homehas.model.RsyncResponseGetCustRelHb;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 查询客户积分、储值金、暂存款、惠币查询接口
 */
public class RsyncGetCustRelHb extends RsyncHomeHas<RsyncGetCustRelHb.RsyncConfigGetCustRelHb, RsyncRequestGetCustRelHb, RsyncResponseGetCustRelHb> {

	final static RsyncConfigGetCustRelHb CONFIG = new RsyncConfigGetCustRelHb();
	
	private RsyncRequestGetCustRelHb tRequest = new RsyncRequestGetCustRelHb();
	private RsyncResponseGetCustRelHb tResponse = new RsyncResponseGetCustRelHb();

	public RsyncConfigGetCustRelHb upConfig() {
		return CONFIG;
	}
	
	public RsyncRequestGetCustRelHb upRsyncRequest() {
		return tRequest;
	}
	
	public RsyncResult doProcess(RsyncRequestGetCustRelHb tRequest, RsyncResponseGetCustRelHb tResponse) {
		this.tResponse = tResponse;
		
		RsyncResult result = new RsyncResult();
		if (!tResponse.isSuccess()) {
			result.inErrorMessage(918501003);
		}
		return result;
	}

	public RsyncResponseGetCustRelHb upResponseObject() {
		return tResponse;
	}
	
	public static class RsyncConfigGetCustRelHb extends RsyncConfigRsyncBase {

		@Override
		public String getRsyncTarget() {
			return "getCustRelHb";
		}
	}

}
