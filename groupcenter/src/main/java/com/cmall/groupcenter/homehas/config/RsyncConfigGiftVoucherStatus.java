package com.cmall.groupcenter.homehas.config;

import com.cmall.groupcenter.groupface.IRsyncDateCheck;

/**
 * 参考RsyncConfigRsyncCustInfo
 * @author cc
 *
 */
public class RsyncConfigGiftVoucherStatus extends RsyncConfigRsyncBase implements
IRsyncDateCheck {

	@Override
	public String getRsyncTarget() {
		
		return "getHjyLjq";
	}

	@Override
	public String getBaseStartTime() {
		return "2018-06-28 00:00:00";
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
