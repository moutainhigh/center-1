package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigOpenStoreCard;
import com.cmall.groupcenter.homehas.model.RsyncRequestOpenStoreCard;
import com.cmall.groupcenter.homehas.model.RsyncResponseOpenStoreCard;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 调用LD储值卡开卡接口
 * @remark 
 * @author 任宏斌
 * @date 2019年3月12日
 */
public class RsyncOpenStoreCard
		extends
		RsyncHomeHas<RsyncConfigOpenStoreCard, RsyncRequestOpenStoreCard, RsyncResponseOpenStoreCard> {

	private final static RsyncConfigOpenStoreCard rsyncConfigOpenStoreCard = new RsyncConfigOpenStoreCard();

	public RsyncConfigOpenStoreCard upConfig() {

		return rsyncConfigOpenStoreCard;
	}

	private RsyncRequestOpenStoreCard rsyncRequestOpenStoreCard = new RsyncRequestOpenStoreCard();

	public RsyncRequestOpenStoreCard upRsyncRequest() {
		return rsyncRequestOpenStoreCard;
	}

	public RsyncResult doProcess(RsyncRequestOpenStoreCard tRequest,
			RsyncResponseOpenStoreCard tResponse) {
		return new RsyncResult();
	}

	public RsyncResponseOpenStoreCard upResponseObject() {

		return new RsyncResponseOpenStoreCard();
	}

}
