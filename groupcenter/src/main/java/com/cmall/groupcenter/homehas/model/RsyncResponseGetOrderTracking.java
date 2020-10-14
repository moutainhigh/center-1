package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncResponse;
import com.cmall.groupcenter.homehas.model.RsyncResponseGetStock.Stockinfo;
/**
 * 订单配送轨迹查询接口  返回数据
 * @author wz
 *
 */
public class RsyncResponseGetOrderTracking implements IRsyncResponse{
	
	
	private boolean success;
	private String message;
	
	//list名字必须是result
	private List<ResponseGetOrderTrackingList> result = new ArrayList<ResponseGetOrderTrackingList>();

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

	public List<ResponseGetOrderTrackingList> getResult() {
		return result;
	}

	public void setResult(List<ResponseGetOrderTrackingList> result) {
		this.result = result;
	}

	
}
