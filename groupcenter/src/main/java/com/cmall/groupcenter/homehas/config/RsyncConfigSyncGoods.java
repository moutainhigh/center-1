package com.cmall.groupcenter.homehas.config;

import com.cmall.groupcenter.groupface.IRsyncDateCheck;

public class RsyncConfigSyncGoods extends RsyncConfigRsyncBase implements
IRsyncDateCheck {

	public String getRsyncTarget() {

		return "getSYGoods";
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
