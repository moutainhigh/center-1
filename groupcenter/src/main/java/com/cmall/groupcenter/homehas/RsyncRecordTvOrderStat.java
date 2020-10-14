package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigRecordTvOrderStat;
import com.cmall.groupcenter.homehas.model.RsyncRequestRecordTvOrderStat;
import com.cmall.groupcenter.homehas.model.RsyncResponseRecordTvOrderStat;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 5.2.6 取消TV品订单
 * @author cc
 *
 */
public class RsyncRecordTvOrderStat extends RsyncHomeHas<RsyncConfigRecordTvOrderStat, RsyncRequestRecordTvOrderStat, RsyncResponseRecordTvOrderStat> {

	private final static RsyncConfigRecordTvOrderStat rsyncConfigRecordTvOrderStat = new RsyncConfigRecordTvOrderStat();
	 
	@Override
	public RsyncConfigRecordTvOrderStat upConfig() {
		return rsyncConfigRecordTvOrderStat;
	}

	private RsyncRequestRecordTvOrderStat rsyncRequestRecordTvOrderStat = new RsyncRequestRecordTvOrderStat();
	
	@Override
	public RsyncRequestRecordTvOrderStat upRsyncRequest() {
		return rsyncRequestRecordTvOrderStat;
	}

	@Override
	public RsyncResult doProcess(RsyncRequestRecordTvOrderStat tRequest, RsyncResponseRecordTvOrderStat tResponse) {
		rsyncResponseRecordTvOrderStat = tResponse;
		return new RsyncResult();
	}

	private RsyncResponseRecordTvOrderStat rsyncResponseRecordTvOrderStat = new RsyncResponseRecordTvOrderStat();
	
	@Override
	public RsyncResponseRecordTvOrderStat upResponseObject() {
		return new RsyncResponseRecordTvOrderStat();
	}

	/**
	 * 获取响应信息
	 * @return
	 */
	public RsyncResponseRecordTvOrderStat getResponseObject() {
		return rsyncResponseRecordTvOrderStat;
	}
}
