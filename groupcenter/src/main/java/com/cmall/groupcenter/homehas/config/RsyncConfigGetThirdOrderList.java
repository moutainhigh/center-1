package com.cmall.groupcenter.homehas.config;

/**
 * 5.2.6 LD订单列表
 * @author cc
 *
 */
public class RsyncConfigGetThirdOrderList extends RsyncConfigRsyncBase {

	@Override
	public String getRsyncTarget() {
		
		return "getThirdOrderInfo";
	}

}
