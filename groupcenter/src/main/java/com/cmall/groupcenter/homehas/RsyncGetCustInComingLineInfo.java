package com.cmall.groupcenter.homehas;


import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncBase;
import com.cmall.groupcenter.homehas.model.RsyncGetCustInComingLineInfoRequest;
import com.cmall.groupcenter.homehas.model.RsyncGetCustInComingLineInfoRespone;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 查询客户进线信息
 */
public class RsyncGetCustInComingLineInfo extends RsyncHomeHas<RsyncGetCustInComingLineInfo.RsyncGetCustInComingLineInfoConfig, RsyncGetCustInComingLineInfoRequest, RsyncGetCustInComingLineInfoRespone> {

	final static RsyncGetCustInComingLineInfoConfig CONFIG = new RsyncGetCustInComingLineInfoConfig();
	
	private RsyncGetCustInComingLineInfoRequest tRequest = new RsyncGetCustInComingLineInfoRequest();
	private RsyncGetCustInComingLineInfoRespone tResponse = new RsyncGetCustInComingLineInfoRespone();

	public RsyncGetCustInComingLineInfoConfig upConfig() {
		return CONFIG;
	}
	
	public RsyncGetCustInComingLineInfoRequest upRsyncRequest() {
		return tRequest;
	}
	
	public RsyncResult doProcess(RsyncGetCustInComingLineInfoRequest tRequest, RsyncGetCustInComingLineInfoRespone tResponse) {
		this.tResponse = tResponse;
		
		RsyncResult result = new RsyncResult();
		if (!tResponse.isSuccess()) {
			result.inErrorMessage(918501003);
		}
		return result;
	}

	public RsyncGetCustInComingLineInfoRespone upResponseObject() {
		return tResponse;
	}
	
	public static class RsyncGetCustInComingLineInfoConfig extends RsyncConfigRsyncBase {

		@Override
		public String getRsyncTarget() {
			return "getFormInboundInfo";
		}
	}
	

	


}
