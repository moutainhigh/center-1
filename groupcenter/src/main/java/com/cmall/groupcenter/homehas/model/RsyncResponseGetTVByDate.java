package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

/**
 * TV信息返回接口
 * 
 * @author xiegj
 * 
 */
public class RsyncResponseGetTVByDate extends RsyncResponseBase {

	private List<RsyncModelTVInfo> result = new ArrayList<RsyncModelTVInfo>();

	public List<RsyncModelTVInfo> getResult() {
		return result;
	}

	public void setResult(List<RsyncModelTVInfo> result) {
		this.result = result;
	}

}
