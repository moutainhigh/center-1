package com.cmall.groupcenter.homehas.config;

/**
 * 获取LD订单数量
 * @author cc
 *
 */
public class RsyncConfigGetThirdOrderNumber extends RsyncConfigRsyncBase {

	@Override
	public String getRsyncTarget() {
		
		return "getThirdOrderCnt";
	}

}
