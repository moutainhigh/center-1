package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigGetThirdOrderDetail;
import com.cmall.groupcenter.homehas.model.RsyncRequestGetThirdOrderDetail;
import com.cmall.groupcenter.homehas.model.RsyncResponseGetThirdOrderDetail;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 5.2.6 LD订单详情
 * @author cc
 *
 */
public class RsyncGetThirdOrderDetail extends RsyncHomeHas<RsyncConfigGetThirdOrderDetail, RsyncRequestGetThirdOrderDetail, RsyncResponseGetThirdOrderDetail>{

	private final static RsyncConfigGetThirdOrderDetail rsyncConfigGetThirdOrderDetail = new RsyncConfigGetThirdOrderDetail();
	@Override
	public RsyncConfigGetThirdOrderDetail upConfig() {
		return rsyncConfigGetThirdOrderDetail;
	}

	private RsyncRequestGetThirdOrderDetail rsyncRequestGetThirdOrderDetail = new RsyncRequestGetThirdOrderDetail();
	
	@Override
	public RsyncRequestGetThirdOrderDetail upRsyncRequest() {
		return rsyncRequestGetThirdOrderDetail;
	}

	@Override
	public RsyncResult doProcess(RsyncRequestGetThirdOrderDetail tRequest, RsyncResponseGetThirdOrderDetail tResponse) {
		rsyncResponseGetThirdOrderDetail = tResponse;
		return new RsyncResult();
	}

	private RsyncResponseGetThirdOrderDetail rsyncResponseGetThirdOrderDetail = new RsyncResponseGetThirdOrderDetail();
	
	@Override
	public RsyncResponseGetThirdOrderDetail upResponseObject() {
		return new RsyncResponseGetThirdOrderDetail();
	}

	/**
	 * 获取响应信息
	 * @return
	 */
	public RsyncResponseGetThirdOrderDetail getResponseObject() {
		return rsyncResponseGetThirdOrderDetail;
	} 
}
