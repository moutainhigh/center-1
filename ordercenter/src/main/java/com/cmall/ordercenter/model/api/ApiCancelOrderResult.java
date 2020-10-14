package com.cmall.ordercenter.model.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.srnpr.zapcom.topapi.RootResult;

public class ApiCancelOrderResult extends RootResult{
	
	private List<ApiCancelModel> list = new ArrayList<ApiCancelModel>();

	public List<ApiCancelModel> getList() {
		return list;
	}

	public void setList(List<ApiCancelModel> list) {
		this.list = list;
	}
	
	private List<GiftVoucherInfo> reWriteLD = new ArrayList<GiftVoucherInfo>();

	public List<GiftVoucherInfo> getReWriteLD() {
		return reWriteLD;
	}

	public void setReWriteLD(List<GiftVoucherInfo> reWriteLD) {
		this.reWriteLD = reWriteLD;
	}

}
