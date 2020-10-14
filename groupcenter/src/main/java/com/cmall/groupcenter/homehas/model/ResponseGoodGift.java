package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 返回接口
 * 
 * @author xiegj
 * 
 */
public class ResponseGoodGift extends RsyncResponseBase {

	private List<ModelGoodGiftInfo> result = new ArrayList<ModelGoodGiftInfo>();

	public List<ModelGoodGiftInfo> getResult() {
		return result;
	}

	public void setResult(List<ModelGoodGiftInfo> result) {
		this.result = result;
	}

}
