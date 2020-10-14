package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncBase;
import com.cmall.groupcenter.homehas.model.RsyncRequestModOrdMedia;
import com.cmall.groupcenter.homehas.model.RsyncResponseModOrdMedia;
import com.cmall.groupcenter.homehas.model.RsyncResult;


/***
 * 修改订单通路为小程序通路
 */
public class RsyncModOrdMedia extends RsyncHomeHas<RsyncModOrdMedia.RsyncConfigModOrdMedia, RsyncRequestModOrdMedia, RsyncResponseModOrdMedia> {

	private final static RsyncConfigModOrdMedia rsyncConfig = new RsyncModOrdMedia.RsyncConfigModOrdMedia();

	public RsyncConfigModOrdMedia upConfig() {
		return rsyncConfig;
	}

	private RsyncRequestModOrdMedia rsyncRequest = new RsyncRequestModOrdMedia();

	public RsyncRequestModOrdMedia upRsyncRequest() {
		return rsyncRequest;
	}

	public RsyncResult doProcess(RsyncRequestModOrdMedia tRequest, RsyncResponseModOrdMedia tResponse) {
		resRsync = tResponse;
		
		RsyncResult res = new RsyncResult();
		if(!tResponse.isSuccess()) {
			res.setResultCode(0);
			res.setResultMessage(tResponse.getMessage());
		}
		return res;
	}

	public RsyncResponseModOrdMedia upResponseObject() {
		return new RsyncResponseModOrdMedia();
	}

	private RsyncResponseModOrdMedia resRsync = new RsyncResponseModOrdMedia();
	
	public RsyncResponseModOrdMedia getResponseObject() {
		return resRsync;
	}
	
	public static class RsyncConfigModOrdMedia extends RsyncConfigRsyncBase {
		public String getRsyncTarget() {
			return "modOrdMedia";
		}
	}
	
}
