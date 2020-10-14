package com.cmall.groupcenter.express.app.service;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.homehas.model.RsyncResponseBase;

public class RsyncResponseSyncAppGoods  extends RsyncResponseBase{
	private List<RsyncModelExpressInfo> result = new ArrayList<RsyncModelExpressInfo>();

	public List<RsyncModelExpressInfo> getResult() {
		return result;
	}

	public void setResult(List<RsyncModelExpressInfo> result) {
		this.result = result;
	}

}
