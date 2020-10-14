package com.cmall.groupcenter.homehas.config;

import com.cmall.groupcenter.groupface.IRsyncDateCheck;

/**
 * 参考RsyncConfigRsyncCustInfo
 * @author cc
 *
 */
public class RsyncConfigRsyncCouponInfo extends RsyncConfigRsyncBase implements
IRsyncDateCheck{

	public String getRsyncTarget() {

		return "hjyDiscountOrds";
	}

	@Override
	public String getBaseStartTime() {
		return "2014-05-12 00:00:00";
	}

	@Override
	public int getMaxStepSecond() {
		return 3600 * 24;
	}

	@Override
	public int getBackSecond() {
		return 3600;
	}
	
}