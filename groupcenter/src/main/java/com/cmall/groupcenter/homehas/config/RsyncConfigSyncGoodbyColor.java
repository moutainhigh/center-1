package com.cmall.groupcenter.homehas.config;

import com.cmall.groupcenter.groupface.IRsyncDateCheck;

public class RsyncConfigSyncGoodbyColor extends RsyncConfigRsyncBase implements
IRsyncDateCheck {

	public String getRsyncTarget() {

		return "getSYGoodbyColor";
	}

	
	public String getBaseStartTime() {
		return "2010-10-30 00:00:00";
	}

	public int getMaxStepSecond() {
		return 3600 * 24;
	}

	public int getBackSecond() {
		return 3600;
	}

}
