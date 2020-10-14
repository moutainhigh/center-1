package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncRequest;

public class RsyncRequestCouponForYth implements IRsyncRequest {

	List<CouponYth> couponList = new ArrayList<CouponYth>();

	public List<CouponYth> getCouponList() {
		return couponList;
	}

	public void setCouponList(List<CouponYth> couponList) {
		this.couponList = couponList;
	}
	
	
	
}
