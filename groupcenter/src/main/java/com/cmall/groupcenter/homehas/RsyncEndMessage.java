package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigEndMessage;
import com.cmall.groupcenter.homehas.model.RsyncRequestEndMessage;
import com.cmall.groupcenter.homehas.model.RsyncResponseEndMessage;
import com.cmall.groupcenter.homehas.model.RsyncResult;

public class RsyncEndMessage
		extends
		RsyncHomeHas<RsyncConfigEndMessage, RsyncRequestEndMessage, RsyncResponseEndMessage> {

	private final static RsyncConfigEndMessage rsyncConfigEndMessage = new RsyncConfigEndMessage();

	public RsyncConfigEndMessage upConfig() {

		return rsyncConfigEndMessage;
	}

	private RsyncRequestEndMessage rsyncRequestEndMessage = new RsyncRequestEndMessage();

	public RsyncRequestEndMessage upRsyncRequest() {
		// TODO Auto-generated method stub
		return rsyncRequestEndMessage;
	}

	public RsyncResult doProcess(RsyncRequestEndMessage tRequest,
			RsyncResponseEndMessage tResponse) {
		// TODO Auto-generated method stub
		return new RsyncResult();
	}

	public RsyncResponseEndMessage upResponseObject() {

		return new RsyncResponseEndMessage();
	}

}
