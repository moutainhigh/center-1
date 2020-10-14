package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigCouponForYth;
import com.cmall.groupcenter.homehas.model.RsyncRequestCouponForYth;
import com.cmall.groupcenter.homehas.model.RsyncResponseCouponForYth;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 同步优惠券信息-一体化
 * @remark 
 * @author 任宏斌
 * @date 2020年5月13日
 */
public class RsyncCouponForYth extends
		RsyncHomeHas<RsyncConfigCouponForYth, RsyncRequestCouponForYth, RsyncResponseCouponForYth> {

	private RsyncConfigCouponForYth config = new RsyncConfigCouponForYth();
	private RsyncRequestCouponForYth request = new RsyncRequestCouponForYth();
	private RsyncResponseCouponForYth response = new RsyncResponseCouponForYth();
	
	public RsyncConfigCouponForYth upConfig() {
		return config;
	}

	public RsyncRequestCouponForYth upRsyncRequest() {
		return request;
	}

	public RsyncResult doProcess(RsyncRequestCouponForYth tRequest, RsyncResponseCouponForYth tResponse) {
		return new RsyncResult();
	}

	public RsyncResponseCouponForYth upResponseObject() {
		return response;
	}

}
