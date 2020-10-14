package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncCustExist;
import com.cmall.groupcenter.homehas.model.RsyncRequestRsyncCustExist;
import com.cmall.groupcenter.homehas.model.RsyncResponseRsyncCustExist;
import com.cmall.groupcenter.homehas.model.RsyncResult;


/***
 * 根据手机号判断LD系统中用户是否存在
 * @author 张海生
 *
 */
public class RsyncCustExist extends RsyncHomeHas<RsyncConfigRsyncCustExist, RsyncRequestRsyncCustExist, RsyncResponseRsyncCustExist> {

	private final static RsyncConfigRsyncCustExist rsyncConfigRsyncCustExist = new RsyncConfigRsyncCustExist();

	public RsyncConfigRsyncCustExist upConfig() {

		return rsyncConfigRsyncCustExist;
	}

	private RsyncRequestRsyncCustExist rsyncRequestRsyncCustExist = new RsyncRequestRsyncCustExist();

	public RsyncRequestRsyncCustExist upRsyncRequest() {
		return rsyncRequestRsyncCustExist;
	}

	public RsyncResult doProcess(RsyncRequestRsyncCustExist tRequest, RsyncResponseRsyncCustExist tResponse) {
		
		resRsyncCustInfoExist=tResponse;
		
		return new RsyncResult();
	}

	public RsyncResponseRsyncCustExist upResponseObject() {

		return new RsyncResponseRsyncCustExist();
	}

	private RsyncResponseRsyncCustExist resRsyncCustInfoExist = new RsyncResponseRsyncCustExist();
	
	/**
	 * 获取响应信息
	 * @return
	 */
	public RsyncResponseRsyncCustExist getResponseObject() {

		return resRsyncCustInfoExist;
	}
	
}
