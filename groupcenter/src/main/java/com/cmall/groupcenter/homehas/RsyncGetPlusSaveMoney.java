package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigAddOrder;
import com.cmall.groupcenter.homehas.config.RsyncConfigGetPlusSaveMoney;
import com.cmall.groupcenter.homehas.model.RsyncRequestAddOrder;
import com.cmall.groupcenter.homehas.model.RsyncRequestGetPlusSaveMoney;
import com.cmall.groupcenter.homehas.model.RsyncResponseAddOrder;
import com.cmall.groupcenter.homehas.model.RsyncResponseGetPlusSaveMoney;
import com.cmall.groupcenter.homehas.model.RsyncResponseRsyncCustInfo;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 获取plus会员LD订单节约金额
 * @author Angel Joy
 * @date 2020年6月29日 下午5:39:56
 * @version 
 * @desc TODO
 */
public class RsyncGetPlusSaveMoney extends RsyncHomeHas<RsyncConfigGetPlusSaveMoney, RsyncRequestGetPlusSaveMoney, RsyncResponseGetPlusSaveMoney> {
	
	private final static RsyncConfigGetPlusSaveMoney rsyncConfigGetPlusSaveMoney = new RsyncConfigGetPlusSaveMoney();
	private String statusCode = "";
	@Override
	public RsyncConfigGetPlusSaveMoney upConfig() {
		return rsyncConfigGetPlusSaveMoney;
	}

	private RsyncRequestGetPlusSaveMoney rsyncRequestGetPlusSaveMoney = new RsyncRequestGetPlusSaveMoney();
	
	@Override
	public RsyncRequestGetPlusSaveMoney upRsyncRequest() {
		
		return rsyncRequestGetPlusSaveMoney;
	}

	@Override
	public RsyncResult doProcess(RsyncRequestGetPlusSaveMoney tRequest, RsyncResponseGetPlusSaveMoney tResponse) {
		statusCode=tResponse.getStatus();//状态码
		rsyncResponseGetPlusSaveMoney = tResponse;
		return new RsyncResult();
	}
	private RsyncResponseGetPlusSaveMoney rsyncResponseGetPlusSaveMoney = new RsyncResponseGetPlusSaveMoney();

	@Override
	public RsyncResponseGetPlusSaveMoney upResponseObject() {
		return rsyncResponseGetPlusSaveMoney;
	}

	/**
	 * 返回状态码
	 * @return
	 */
	public String getStatusCode(){
		return statusCode;
	}
}
