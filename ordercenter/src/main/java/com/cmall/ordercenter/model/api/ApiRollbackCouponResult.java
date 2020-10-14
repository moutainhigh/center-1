package com.cmall.ordercenter.model.api;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.topapi.RootResult;

/**
 * 取消订单回写礼金券到LD
 * @author cc
 *
 */
public class ApiRollbackCouponResult extends RootResult {

	private List<GiftVoucherInfo> reWriteLD = new ArrayList<GiftVoucherInfo>();

	public List<GiftVoucherInfo> getReWriteLD() {
		return reWriteLD;
	}

	public void setReWriteLD(List<GiftVoucherInfo> reWriteLD) {
		this.reWriteLD = reWriteLD;
	}
	
}
