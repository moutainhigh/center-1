package com.cmall.groupcenter.jd.job;

import com.cmall.groupcenter.jd.JdAfterSaleSupport;
import com.srnpr.xmassystem.Constants;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapweb.rootweb.RootJobForExec;
import com.srnpr.zapweb.webmodel.ConfigJobExec;

/**
 * 定时初始化售后单表
 */
public class JobForCreateAfterSaleService extends RootJobForExec {

	@Override
	public IBaseResult execByInfo(String sInfo) {
		return new JdAfterSaleSupport().initJdAfterSale(sInfo);
	}

	@Override
	public ConfigJobExec getConfig() {
		return config;
	}

	private static ConfigJobExec config = new ConfigJobExec();
	static {
		config.setExecType(Constants.ZA_EXEC_TYPE_JD_AFTER_SALE_CREATE);
		config.setMaxExecNumber(5);
		config.setNoticeOnce(5);
	}
}
