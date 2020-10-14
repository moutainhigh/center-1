package com.cmall.groupcenter.homehas.config;

import com.cmall.groupcenter.groupface.IRsyncDateCheck;

/**
 * 违禁品同步配置信息
 * @author cc
 *
 */
public class RsyncConfigSyncContraband extends RsyncConfigRsyncBase implements
IRsyncDateCheck{

	@Override
	public String getRsyncTarget() {
		return "getWjpRule";
	}

	@Override
	public String getBaseStartTime() {
		return "2018-07-01 00:00:00";
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
