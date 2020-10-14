package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncResponse;

/**
 * 同步配送信息返回接口
 * 
 * @author srnpr
 * 
 */
public class RsyncResponseGetShipmentStat extends RsyncResponseBase {

	private List<RsyncModelShipmentStat> result = new ArrayList<RsyncModelShipmentStat>();

	public List<RsyncModelShipmentStat> getResult() {
		return result;
	}

	public void setResult(List<RsyncModelShipmentStat> result) {
		this.result = result;
	}

}
