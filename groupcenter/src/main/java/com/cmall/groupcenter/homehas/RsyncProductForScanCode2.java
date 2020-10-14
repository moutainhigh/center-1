package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigProductForScanCode2;
import com.cmall.groupcenter.homehas.model.RsyncRequestProductForScanCode;
import com.cmall.groupcenter.homehas.model.RsyncResponseProductForScanCode;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 同步一定时间范围内的TV信息(南京二台)
 * 
 * @author renhongbin
 * 
 */
public class RsyncProductForScanCode2
		extends
		RsyncHomeHas<RsyncConfigProductForScanCode2, RsyncRequestProductForScanCode, RsyncResponseProductForScanCode> {

	public RsyncConfigProductForScanCode2 upConfig() {
		return new RsyncConfigProductForScanCode2();
	}

	private RsyncRequestProductForScanCode rsyncRequestProductForScanCode = new RsyncRequestProductForScanCode();
	
	public RsyncRequestProductForScanCode upRsyncRequest() {
		return rsyncRequestProductForScanCode;
	}

	public RsyncResult doProcess(RsyncRequestProductForScanCode tRequest, RsyncResponseProductForScanCode tResponse) {
		return new RsyncResult();
	}

	public RsyncResponseProductForScanCode upResponseObject() {
		return new RsyncResponseProductForScanCode();
	}

}
