package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncResponse;

/**
 * 会员信息返回接口
 * 
 * @author srnpr
 * 
 */
public class RsyncResponseSyncOrders extends RsyncResponseBase {

	private List<RsyncModelOrderInfo> result = new ArrayList<RsyncModelOrderInfo>();

	public List<RsyncModelOrderInfo> getResult() {
		return result;
	}

	public void setResult(List<RsyncModelOrderInfo> result) {
		this.result = result;
	}

}
