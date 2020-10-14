package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigProductForScanCode;
import com.cmall.groupcenter.homehas.model.RsyncRequestProductForScanCode;
import com.cmall.groupcenter.homehas.model.RsyncResponseProductForScanCode;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 同步一定时间范围内的TV信息
 * 
 * @author renhongbin
 * 
 */
public class RsyncProductForScanCode
		extends
		RsyncHomeHas<RsyncConfigProductForScanCode, RsyncRequestProductForScanCode, RsyncResponseProductForScanCode> {

	public RsyncConfigProductForScanCode upConfig() {
		return new RsyncConfigProductForScanCode();
	}

	public RsyncRequestProductForScanCode upRsyncRequest() {
		return new RsyncRequestProductForScanCode();
	}

	public RsyncResult doProcess(RsyncRequestProductForScanCode tRequest, RsyncResponseProductForScanCode tResponse) {
		return new RsyncResult();
	}

	public RsyncResponseProductForScanCode upResponseObject() {
		return new RsyncResponseProductForScanCode();
	}

}
