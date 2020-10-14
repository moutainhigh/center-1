package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncGetCustPpcUseDetailConfig;
import com.cmall.groupcenter.homehas.model.RsyncGetCustAccmUseDetailRequest;
import com.cmall.groupcenter.homehas.model.RsyncGetCustPpcUseDetailResponse;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 查询客户储值金使用明细
 * @author zf
 *
 */
public class RsyncGetCustPpcUseDetail extends RsyncHomeHas<RsyncGetCustPpcUseDetailConfig, RsyncGetCustAccmUseDetailRequest, RsyncGetCustPpcUseDetailResponse> {

	private RsyncGetCustPpcUseDetailConfig config = new RsyncGetCustPpcUseDetailConfig();
	
	private RsyncGetCustAccmUseDetailRequest request = new RsyncGetCustAccmUseDetailRequest();
	
	private RsyncGetCustPpcUseDetailResponse response = new RsyncGetCustPpcUseDetailResponse();

	@Override
	public RsyncGetCustPpcUseDetailConfig upConfig() {
		
		return config;
	}

	@Override
	public RsyncGetCustAccmUseDetailRequest upRsyncRequest() {
		
		return request;
	}

	@Override
	public RsyncResult doProcess(RsyncGetCustAccmUseDetailRequest tRequest,
			RsyncGetCustPpcUseDetailResponse tResponse) {
		this.response = tResponse;
		RsyncResult mWebResult = new RsyncResult();
		if(tResponse != null && !tResponse.getSuccess()){
			mWebResult.setResultCode(918501003);
			mWebResult.setResultMessage(bInfo(918501003));
		}
		return mWebResult;
	}

	@Override
	public RsyncGetCustPpcUseDetailResponse upResponseObject() {
		
		return response;
	}
	
}
