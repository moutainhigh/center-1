package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncGetCustHbUseDetailConfig;
import com.cmall.groupcenter.homehas.config.RsyncGetCustPpcUseDetailConfig;
import com.cmall.groupcenter.homehas.model.RsyncGetCustAccmUseDetailRequest;
import com.cmall.groupcenter.homehas.model.RsyncGetCustHbUseDetailRequest;
import com.cmall.groupcenter.homehas.model.RsyncGetCustHbUseDetailResponse;
import com.cmall.groupcenter.homehas.model.RsyncGetCustPpcUseDetailResponse;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 查询客户惠币使用明细
 * @author zf
 *
 */
public class RsyncGetCustHbUseDetail extends RsyncHomeHas<RsyncGetCustHbUseDetailConfig, RsyncGetCustHbUseDetailRequest, RsyncGetCustHbUseDetailResponse> {

	private RsyncGetCustHbUseDetailConfig config = new RsyncGetCustHbUseDetailConfig();
	
	private RsyncGetCustHbUseDetailRequest request = new RsyncGetCustHbUseDetailRequest();
	
	private RsyncGetCustHbUseDetailResponse response = new RsyncGetCustHbUseDetailResponse();

	@Override
	public RsyncGetCustHbUseDetailConfig upConfig() {
		
		return config;
	}

	@Override
	public RsyncGetCustHbUseDetailRequest upRsyncRequest() {
		
		return request;
	}

	@Override
	public RsyncResult doProcess(RsyncGetCustHbUseDetailRequest tRequest,
			RsyncGetCustHbUseDetailResponse tResponse) {
		this.response = tResponse;
		RsyncResult mWebResult = new RsyncResult();
		if(tResponse != null && !tResponse.getSuccess()){
			mWebResult.setResultCode(918501003);
			mWebResult.setResultMessage(bInfo(918501003));
		}
		return mWebResult;
	}

	@Override
	public RsyncGetCustHbUseDetailResponse upResponseObject() {
		
		return response;
	}
	
}
