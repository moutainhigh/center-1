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
public class RsyncResponseGetCustByDate extends RsyncResponseBase {

	private List<RsyncModelCustInfo> result = new ArrayList<RsyncModelCustInfo>();

	public List<RsyncModelCustInfo> getResult() {
		return result;
	}

	public void setResult(List<RsyncModelCustInfo> result) {
		this.result = result;
	}

}
