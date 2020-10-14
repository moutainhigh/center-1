package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigGetThirdOrderList;
import com.cmall.groupcenter.homehas.model.RsyncRequestGetThirdOrderList;
import com.cmall.groupcenter.homehas.model.RsyncResponseGetThirdOrderList;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 5.2.6 获取LD订单列表
 * @author cc
 *
 */
public class RsyncGetThirdOrderList extends RsyncHomeHas<RsyncConfigGetThirdOrderList, RsyncRequestGetThirdOrderList, RsyncResponseGetThirdOrderList>{

	private final static RsyncConfigGetThirdOrderList rsyncConfigGetThirdOrderList = new RsyncConfigGetThirdOrderList();
	@Override
	public RsyncConfigGetThirdOrderList upConfig() {
		return rsyncConfigGetThirdOrderList;
	}

	private RsyncRequestGetThirdOrderList rsyncRequestGetThirdOrderList = new RsyncRequestGetThirdOrderList();
	
	@Override
	public RsyncRequestGetThirdOrderList upRsyncRequest() {
		return rsyncRequestGetThirdOrderList;
	}

	@Override
	public RsyncResult doProcess(RsyncRequestGetThirdOrderList tRequest, RsyncResponseGetThirdOrderList tResponse) {
		rsyncResponseGetThirdOrderList=tResponse;		
		return new RsyncResult();
	}
	
	private RsyncResponseGetThirdOrderList rsyncResponseGetThirdOrderList = new RsyncResponseGetThirdOrderList();

	@Override
	public RsyncResponseGetThirdOrderList upResponseObject() {
		return new RsyncResponseGetThirdOrderList();
	}

	/**
	 * 获取响应信息
	 * @return
	 */
	public RsyncResponseGetThirdOrderList getResponseObject() {
		return rsyncResponseGetThirdOrderList;
	}
}
