package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigGetProductStatus;
import com.cmall.groupcenter.homehas.model.RsyncRequestGetProductStatus;
import com.cmall.groupcenter.homehas.model.RsyncResponseGetProductStatus;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 同步商品状态至LD（惠家有）
 * 
 * @author ligj
 * 
 */
public class RsyncGetProductStatus extends RsyncHomeHas<RsyncConfigGetProductStatus, RsyncRequestGetProductStatus, RsyncResponseGetProductStatus> {

	private final static RsyncConfigGetProductStatus rsyncConfigGetProductStatus = new RsyncConfigGetProductStatus();

	public RsyncConfigGetProductStatus upConfig() {
		return rsyncConfigGetProductStatus;
	}

	private RsyncRequestGetProductStatus rsyncRequestGetProductStatus = new RsyncRequestGetProductStatus();

	public RsyncRequestGetProductStatus upRsyncRequest() {

		return rsyncRequestGetProductStatus;
	}

	public RsyncResult doProcess(RsyncRequestGetProductStatus tRequest,
			RsyncResponseGetProductStatus tResponse) {
		RsyncResult mWebResult = new RsyncResult();

		if (!tResponse.isSuccess()) {
			mWebResult.setResultCode(918501003);
			mWebResult.setResultMessage(bInfo(918501003));
		}
		return mWebResult;
	}

	public RsyncResponseGetProductStatus upResponseObject() {

		return new RsyncResponseGetProductStatus();
	}

}
