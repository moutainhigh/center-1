package com.cmall.groupcenter.express.app.service;

import com.cmall.groupcenter.groupface.IRsyncDateCheck;
import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncBase;

public class RsyncConfigSyncAppGoods extends RsyncConfigRsyncBase implements IRsyncDateCheck {

	public String getRsyncTarget() {

		return "getShipmentStat";
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
