package com.cmall.groupcenter.homehas.config;

import com.cmall.groupcenter.groupface.IRsyncDateCheck;

public class RsyncConfigRsyncCancelReturnOrder extends RsyncConfigRsyncBase implements
		IRsyncDateCheck {

	public String getRsyncTarget() {

		return "getCancelReturnOrderList";
	}

	public String getBaseStartTime() {
		return "2015-10-01 00:00:00";
	}

	public int getMaxStepSecond() {
		return 3600 * 24;
	}

	public int getBackSecond() {
		return 3600;
	}

}
