package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigIntegralRelation;
import com.cmall.groupcenter.homehas.model.RsyncRequestIntegralRelation;
import com.cmall.groupcenter.homehas.model.RsyncResponseIntegralRelation;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 同步积分共享关系
 * @remark 
 * @author 任宏斌
 * @date 2019年3月15日
 */
public class RsyncIntegralRelation
		extends
		RsyncHomeHas<RsyncConfigIntegralRelation, RsyncRequestIntegralRelation, RsyncResponseIntegralRelation> {

	private final static RsyncConfigIntegralRelation rsyncConfigIntegralRelation = new RsyncConfigIntegralRelation();

	public RsyncConfigIntegralRelation upConfig() {

		return rsyncConfigIntegralRelation;
	}

	private RsyncRequestIntegralRelation rsyncRequestIntegralRelation = new RsyncRequestIntegralRelation();

	public RsyncRequestIntegralRelation upRsyncRequest() {
		return rsyncRequestIntegralRelation;
	}

	public RsyncResult doProcess(RsyncRequestIntegralRelation tRequest,
			RsyncResponseIntegralRelation tResponse) {
		return new RsyncResult();
	}

	public RsyncResponseIntegralRelation upResponseObject() {

		return new RsyncResponseIntegralRelation();
	}

}
