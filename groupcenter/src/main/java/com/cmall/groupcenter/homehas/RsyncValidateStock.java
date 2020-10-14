package com.cmall.groupcenter.homehas;

import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncBase;
import com.cmall.groupcenter.homehas.model.RsyncRequestValidateStock;
import com.cmall.groupcenter.homehas.model.RsyncResponseValidateStock;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 4.67.库存校验接口
 */
public class RsyncValidateStock extends RsyncHomeHas<RsyncValidateStock.RsyncConfigValidateStock, RsyncRequestValidateStock, RsyncResponseValidateStock> {

	private final static RsyncConfigValidateStock rsyncConfig = new RsyncConfigValidateStock();

	public RsyncConfigValidateStock upConfig() {
		return rsyncConfig;
	}

	private RsyncRequestValidateStock rsyncRequest = new RsyncRequestValidateStock();

	public RsyncRequestValidateStock upRsyncRequest() {
		return rsyncRequest;
	}

	public RsyncResult doProcess(RsyncRequestValidateStock tRequest, RsyncResponseValidateStock tResponse) {
		return new RsyncResult();
	}

	public RsyncResponseValidateStock upResponseObject() {
		return new RsyncResponseValidateStock();
	}
	
	public static class RsyncConfigValidateStock extends RsyncConfigRsyncBase {
		public String getRsyncTarget() {
			return "validateStock";
		}
	}

}
