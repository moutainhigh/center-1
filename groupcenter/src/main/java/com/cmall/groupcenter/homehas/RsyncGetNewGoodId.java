package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncBase;
import com.cmall.groupcenter.homehas.model.RsyncRequestGetNewGoodId;
import com.cmall.groupcenter.homehas.model.RsyncResponseGetNewGoodId;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/***
 * 4.59.获取调编后商品编码接口
 */
public class RsyncGetNewGoodId extends RsyncHomeHas<RsyncGetNewGoodId.TConfig, RsyncRequestGetNewGoodId, RsyncResponseGetNewGoodId> {

	public TConfig upConfig() {
		return new TConfig();
	}

	private RsyncRequestGetNewGoodId request = new RsyncRequestGetNewGoodId();

	public RsyncRequestGetNewGoodId upRsyncRequest() {
		
		return request;
	}

	public RsyncResult doProcess(RsyncRequestGetNewGoodId tRequest, RsyncResponseGetNewGoodId tResponse) {
		RsyncResult mWebResult = new RsyncResult();
		
		return mWebResult;
	}

	public RsyncResponseGetNewGoodId upResponseObject() {

		return new RsyncResponseGetNewGoodId();
	}
	
	public static class TConfig extends RsyncConfigRsyncBase{
		@Override
		public String getRsyncTarget() {
			return "getNewGoodId";
		}
	}

}
