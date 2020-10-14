package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigCancelOrder;
import com.cmall.groupcenter.homehas.model.RsyncRequestCancelOrder;
import com.cmall.groupcenter.homehas.model.RsyncResponseCancelOrder;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 实时取消订单
 * @author jlin
 *
 */
public class RsyncCancelOrderReal extends RsyncHomeHas<RsyncConfigCancelOrder, RsyncRequestCancelOrder, RsyncResponseCancelOrder> {

	private final static RsyncConfigCancelOrder CONFIG_ORDER_CANCEL = new RsyncConfigCancelOrder();
	
	public RsyncConfigCancelOrder upConfig() {
		return CONFIG_ORDER_CANCEL;
	}

	private RsyncRequestCancelOrder requestCancelOrder = new RsyncRequestCancelOrder();

	public RsyncRequestCancelOrder upRsyncRequest() {
		
		return requestCancelOrder;
	}

	private MWebResult rsyncResult = new MWebResult();
	
	public RsyncResult doProcess(RsyncRequestCancelOrder tRequest, RsyncResponseCancelOrder tResponse) {
		
		RsyncResult mWebResult = new RsyncResult();
		
		if(!tResponse.isSuccess()){
			rsyncResult.setResultCode(0);
			rsyncResult.setResultMessage(tResponse.getMessage());
		}
		
		return mWebResult;
	}

	public RsyncResponseCancelOrder upResponseObject() {

		return new RsyncResponseCancelOrder();
	}
	
	/**
	 * 获取同步结果
	 * @return
	 */
	public MWebResult getRsyncResult() {
		return rsyncResult;
	}
	
}
