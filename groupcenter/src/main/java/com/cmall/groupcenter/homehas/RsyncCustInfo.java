package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncCustInfo;
import com.cmall.groupcenter.homehas.model.RsyncRequestRsyncCustInfo;
import com.cmall.groupcenter.homehas.model.RsyncResponseRsyncCustInfo;
import com.cmall.groupcenter.homehas.model.RsyncResult;


/***
 * 同步家有会员信息
 * @author jlin
 *
 */
public class RsyncCustInfo extends RsyncHomeHas<RsyncConfigRsyncCustInfo, RsyncRequestRsyncCustInfo, RsyncResponseRsyncCustInfo> {

	private final static RsyncConfigRsyncCustInfo rsyncConfigRsyncCustInfo = new RsyncConfigRsyncCustInfo();

	public RsyncConfigRsyncCustInfo upConfig() {

		return rsyncConfigRsyncCustInfo;
	}

	private RsyncRequestRsyncCustInfo rsyncRequestRsyncCustInfo = new RsyncRequestRsyncCustInfo();

	public RsyncRequestRsyncCustInfo upRsyncRequest() {
		return rsyncRequestRsyncCustInfo;
	}

	public RsyncResult doProcess(RsyncRequestRsyncCustInfo tRequest, RsyncResponseRsyncCustInfo tResponse) {
		
		resRsyncCustInfo=tResponse;
		
		return new RsyncResult();
	}

	public RsyncResponseRsyncCustInfo upResponseObject() {

		return new RsyncResponseRsyncCustInfo();
	}

	private RsyncResponseRsyncCustInfo resRsyncCustInfo = new RsyncResponseRsyncCustInfo();
	
	/**
	 * 获取响应信息
	 * @return
	 */
	public RsyncResponseRsyncCustInfo getResponseObject() {

		return resRsyncCustInfo;
	}
	
}
