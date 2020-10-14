package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncResponse;

/**
 * 查询客户积分、储值金、暂存款查询接口返回数据
 */
public class RsyncResponseGetCustRelHb implements IRsyncResponse {

	private boolean success;
	private RsyncModelCustRelHb result; 
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public RsyncModelCustRelHb getResult() {
		return result;
	}

	public void setResult(RsyncModelCustRelHb result) {
		this.result = result;
	}

	
	

	
	
}
