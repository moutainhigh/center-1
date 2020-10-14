package com.cmall.groupcenter.homehas.config;

/**
 * 4.61. 同步LD礼金券活动接口
 * @author cc
 *
 */
public class RsyncConfigGiftVoucherActivity extends RsyncConfigRsyncBase {

	@Override
	public String getRsyncTarget() {
		
		return "getLjqEventInfo";
	}

}
