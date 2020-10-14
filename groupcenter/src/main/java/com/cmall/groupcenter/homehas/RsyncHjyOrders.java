package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigHjyOrders;
import com.cmall.groupcenter.homehas.model.RsyncRequestHjyOrders;
import com.cmall.groupcenter.homehas.model.RsyncResponseIntegralRelation;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 同步惠家有商户品订单（签收+N天的订单）
 * @remark 
 * @author sunyan
 * @date 2019年8月1日
 */
public class RsyncHjyOrders
		extends
		RsyncHomeHas<RsyncConfigHjyOrders, RsyncRequestHjyOrders, RsyncResponseIntegralRelation> {

	private final static RsyncConfigHjyOrders rsyncConfigHjyOrders = new RsyncConfigHjyOrders();

	public RsyncConfigHjyOrders upConfig() {

		return rsyncConfigHjyOrders;
	}

	private RsyncRequestHjyOrders rsyncRequestHjyOrders = new RsyncRequestHjyOrders();

	public RsyncRequestHjyOrders upRsyncRequest() {
		return rsyncRequestHjyOrders;
	}

	public RsyncResult doProcess(RsyncRequestHjyOrders tRequest,
			RsyncResponseIntegralRelation tResponse) {
		return new RsyncResult();
	}

	public RsyncResponseIntegralRelation upResponseObject() {

		return new RsyncResponseIntegralRelation();
	}

}
