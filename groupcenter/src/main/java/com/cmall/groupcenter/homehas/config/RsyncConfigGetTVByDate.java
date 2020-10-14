package com.cmall.groupcenter.homehas.config;

import com.cmall.groupcenter.groupface.IRsyncDateCheck;

/**
 * 同步的配置选项
 * 
 * @author xiegj
 * 
 */
public class RsyncConfigGetTVByDate extends RsyncConfigRsyncBase implements
		IRsyncDateCheck {

	public String getRsyncTarget() {

		return "getForm";
	}

	
	public String getBaseStartTime() {
		return "2014-05-12 00:00:00";
	}

	public int getMaxStepSecond() {
		return 3600 * 36;
	}

	public int getBackSecond() {
		return 0;
	}

}
