package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncResponse;

/**
 * 查询客户积分、储值金、暂存款查询接口返回数据
 */
public class RsyncResponseGetCustRelAmt implements IRsyncResponse {

	private boolean success;
	private RsyncModelCustRelAmt result; 
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public RsyncModelCustRelAmt getResult() {
		return result;
	}

	public void setResult(RsyncModelCustRelAmt result) {
		this.result = result;
	}
	
}
