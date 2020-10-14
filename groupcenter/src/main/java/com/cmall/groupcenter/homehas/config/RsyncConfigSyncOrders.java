package com.cmall.groupcenter.homehas.config;

import com.cmall.groupcenter.groupface.IRsyncDateCheck;

public class RsyncConfigSyncOrders extends RsyncConfigRsyncBase implements
IRsyncDateCheck {

	public String getRsyncTarget() {

		return "syncOrders";
	}

	
	public String getBaseStartTime() {
		return "2014-05-12 00:00:00";
	}

	public int getMaxStepSecond() {
		return 3600 * 24;
	}

	public int getBackSecond() {
		return 3600;
	}

}
