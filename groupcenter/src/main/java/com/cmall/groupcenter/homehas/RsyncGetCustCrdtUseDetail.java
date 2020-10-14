package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncGetCustCrdtUseDetailConfig;
import com.cmall.groupcenter.homehas.model.RsyncGetCustAccmUseDetailRequest;
import com.cmall.groupcenter.homehas.model.RsyncGetCustCrdtUseDetailResponse;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 查询客户储值金使用明细
 * @author zf
 *
 */
public class RsyncGetCustCrdtUseDetail extends RsyncHomeHas<RsyncGetCustCrdtUseDetailConfig, RsyncGetCustAccmUseDetailRequest, RsyncGetCustCrdtUseDetailResponse> {

	private RsyncGetCustCrdtUseDetailConfig config = new RsyncGetCustCrdtUseDetailConfig();
	
	private RsyncGetCustAccmUseDetailRequest request = new RsyncGetCustAccmUseDetailRequest();
	
	private RsyncGetCustCrdtUseDetailResponse response = new RsyncGetCustCrdtUseDetailResponse();

	@Override
	public RsyncGetCustCrdtUseDetailConfig upConfig() {
		
		return config;
	}

	@Override
	public RsyncGetCustAccmUseDetailRequest upRsyncRequest() {
		
		return request;
	}

	@Override
	public RsyncResult doProcess(RsyncGetCustAccmUseDetailRequest tRequest,
			RsyncGetCustCrdtUseDetailResponse tResponse) {
		this.response = tResponse;
		RsyncResult mWebResult = new RsyncResult();
		if(tResponse != null && !tResponse.getSuccess()){
			mWebResult.setResultCode(918501003);
			mWebResult.setResultMessage(bInfo(918501003));
		}
		return mWebResult;
	}

	@Override
	public RsyncGetCustCrdtUseDetailResponse upResponseObject() {
		
		return response;
	}
	
}
