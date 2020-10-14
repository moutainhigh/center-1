package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigGetThirdOrderNumber;
import com.cmall.groupcenter.homehas.model.RsyncRequestGetThirdOrderNumber;
import com.cmall.groupcenter.homehas.model.RsyncResponseGetThirdOrderNumber;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 获取LD订单的数量
 * @author cc
 *
 */
public class RsyncGetThirdOrderNumber extends RsyncHomeHas<RsyncConfigGetThirdOrderNumber, RsyncRequestGetThirdOrderNumber, RsyncResponseGetThirdOrderNumber>{

	private final static RsyncConfigGetThirdOrderNumber rsyncConfigGetThirdOrderNumber = new RsyncConfigGetThirdOrderNumber();
	
	@Override
	public RsyncConfigGetThirdOrderNumber upConfig() {
		return rsyncConfigGetThirdOrderNumber;
	}

	private RsyncRequestGetThirdOrderNumber rsyncRequestGetThirdOrderNumber = new RsyncRequestGetThirdOrderNumber();
	
	@Override
	public RsyncRequestGetThirdOrderNumber upRsyncRequest() {
		return rsyncRequestGetThirdOrderNumber;
	}	

	@Override
	public RsyncResult doProcess(RsyncRequestGetThirdOrderNumber tRequest, RsyncResponseGetThirdOrderNumber tResponse) {
		rsyncResponseGetThirdOrderNumber = tResponse;
		return new RsyncResult();
	}

	private RsyncResponseGetThirdOrderNumber rsyncResponseGetThirdOrderNumber = new RsyncResponseGetThirdOrderNumber();
	
	@Override
	public RsyncResponseGetThirdOrderNumber upResponseObject() {
		return new RsyncResponseGetThirdOrderNumber();
	}

	public RsyncResponseGetThirdOrderNumber getResponseObject() {
		return rsyncResponseGetThirdOrderNumber;
	}
	
}
