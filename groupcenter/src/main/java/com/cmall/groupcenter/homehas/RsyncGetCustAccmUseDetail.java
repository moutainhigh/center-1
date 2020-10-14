package com.cmall.groupcenter.homehas;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.cmall.groupcenter.homehas.config.RsyncEmployAccountConfig;
import com.cmall.groupcenter.homehas.config.RsyncGetCustAccmUseDetailConfig;
import com.cmall.groupcenter.homehas.model.AccountSubOrderInfo;
import com.cmall.groupcenter.homehas.model.RsyncEmployAccountRequest;
import com.cmall.groupcenter.homehas.model.RsyncEmployAccountResponse;
import com.cmall.groupcenter.homehas.model.RsyncGetCustAccmUseDetailRequest;
import com.cmall.groupcenter.homehas.model.RsyncGetCustAccmUseDetailResponse;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.srnpr.xmasorder.model.TeslaModelOrderDetail;
import com.srnpr.xmasorder.model.TeslaModelOrderPay;
import com.srnpr.xmasorder.service.TeslaEmployAmtService;
import com.srnpr.xmasorder.x.TeslaXOrder;
import com.srnpr.xmaspay.util.BeanComponent;
import com.srnpr.zapcom.basemodel.MDataMap;

/**
 * 查询客户积分使用明细
 * @author zf
 *
 */
public class RsyncGetCustAccmUseDetail extends RsyncHomeHas<RsyncGetCustAccmUseDetailConfig, RsyncGetCustAccmUseDetailRequest, RsyncGetCustAccmUseDetailResponse> {

	private RsyncGetCustAccmUseDetailConfig config = new RsyncGetCustAccmUseDetailConfig();
	
	private RsyncGetCustAccmUseDetailRequest request = new RsyncGetCustAccmUseDetailRequest();
	
	private RsyncGetCustAccmUseDetailResponse response = new RsyncGetCustAccmUseDetailResponse();

	@Override
	public RsyncGetCustAccmUseDetailConfig upConfig() {
		
		return config;
	}

	@Override
	public RsyncGetCustAccmUseDetailRequest upRsyncRequest() {
		
		return request;
	}

	@Override
	public RsyncResult doProcess(RsyncGetCustAccmUseDetailRequest tRequest,
			RsyncGetCustAccmUseDetailResponse tResponse) {
		this.response = tResponse;
		RsyncResult mWebResult = new RsyncResult();
		if(tResponse != null && !tResponse.getSuccess()){
			mWebResult.setResultCode(918501003);
			mWebResult.setResultMessage(bInfo(918501003));
		}
		return mWebResult;
	}

	@Override
	public RsyncGetCustAccmUseDetailResponse upResponseObject() {
		
		return response;
	}
	
}
