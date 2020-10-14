package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigHjyRtn;
import com.cmall.groupcenter.homehas.model.RsyncRequestHjyRtn;
import com.cmall.groupcenter.homehas.model.RsyncResponseIntegralRelation;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 同步惠家有商户品退货单
 * @remark 
 * @author sunyan
 * @date 2019年8月1日
 */
public class RsyncHjyRtns
		extends
		RsyncHomeHas<RsyncConfigHjyRtn, RsyncRequestHjyRtn, RsyncResponseIntegralRelation> {

	private final static RsyncConfigHjyRtn rsyncConfigHjyRtn = new RsyncConfigHjyRtn();

	public RsyncConfigHjyRtn upConfig() {

		return rsyncConfigHjyRtn;
	}

	private RsyncRequestHjyRtn rsyncRequestHjyRtn = new RsyncRequestHjyRtn();

	public RsyncRequestHjyRtn upRsyncRequest() {
		return rsyncRequestHjyRtn;
	}

	public RsyncResult doProcess(RsyncRequestHjyRtn tRequest,
			RsyncResponseIntegralRelation tResponse) {
		return new RsyncResult();
	}

	public RsyncResponseIntegralRelation upResponseObject() {

		return new RsyncResponseIntegralRelation();
	}

}
