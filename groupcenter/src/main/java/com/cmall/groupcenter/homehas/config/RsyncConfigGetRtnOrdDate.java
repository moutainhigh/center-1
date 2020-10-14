package com.cmall.groupcenter.homehas.config;

import com.cmall.groupcenter.groupface.IRsyncDateCheck;

public class RsyncConfigGetRtnOrdDate extends RsyncConfigRsyncBase implements IRsyncDateCheck{

	public String getRsyncTarget() {
		return "getRtnOrdDate";
	}

	@Override
	public String getBaseStartTime() {
		return "2018-09-01 00:00:00";
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
