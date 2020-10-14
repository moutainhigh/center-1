package com.cmall.groupcenter.homehas.config;

/**
 * 暂存款、储备金（占用 取消 使用）配置信息
 * @author pang_jhui
 *
 */
public class RsyncGetCustAccmUseDetailConfig extends RsyncConfigRsyncBase {

	@Override
	public String getRsyncTarget() {
		
		return "getCustAccmUseDetail";
	}

}
