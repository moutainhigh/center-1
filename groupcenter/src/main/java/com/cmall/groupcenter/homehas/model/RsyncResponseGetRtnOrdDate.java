package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncResponse;

/**
 * 4.65.惠家有TV品销退拒收退货相关信息同步接口返回数据
 */
public class RsyncResponseGetRtnOrdDate implements IRsyncResponse {

	private boolean success;
	private String message;
	private List<RsyncModelReturnGoods> result = new ArrayList<RsyncModelReturnGoods>();

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<RsyncModelReturnGoods> getResult() {
		return result;
	}

	public void setResult(List<RsyncModelReturnGoods> result) {
		this.result = result;
	}
	
}
