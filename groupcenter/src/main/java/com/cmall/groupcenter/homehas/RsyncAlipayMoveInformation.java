package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigAlipayMoveInformation;
import com.cmall.groupcenter.homehas.model.RsyncRequestAlipayMoveInformation;
import com.cmall.groupcenter.homehas.model.RsyncResponseAlipayMoveInformation;
import com.cmall.groupcenter.homehas.model.RsyncResult;

public class RsyncAlipayMoveInformation
		extends
		RsyncHomeHas<RsyncConfigAlipayMoveInformation, RsyncRequestAlipayMoveInformation, RsyncResponseAlipayMoveInformation> {

	private final static RsyncConfigAlipayMoveInformation rsyncConfigAlipayMoveInformation = new RsyncConfigAlipayMoveInformation();
	private boolean status=false;//同步状态

	public RsyncConfigAlipayMoveInformation upConfig() {
		return rsyncConfigAlipayMoveInformation;
	}

	private RsyncRequestAlipayMoveInformation rsyncRequestAlipayMoveInformation = new RsyncRequestAlipayMoveInformation();

	public RsyncRequestAlipayMoveInformation upRsyncRequest() {
		return rsyncRequestAlipayMoveInformation;
	}

	public RsyncResult doProcess(RsyncRequestAlipayMoveInformation tRequest,
			RsyncResponseAlipayMoveInformation tResponse) {
		
		status=tResponse.isSend_result();
		
		return new RsyncResult();
	}

	public RsyncResponseAlipayMoveInformation upResponseObject() {
		return new RsyncResponseAlipayMoveInformation();
	}

	
	/**
	 * 返回状态
	 * @return
	 */
	public boolean getStatus(){
		return status;
	}
}
