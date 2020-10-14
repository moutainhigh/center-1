package com.cmall.groupcenter.duohuozhu.job;

import com.cmall.groupcenter.duohuozhu.support.DuohzAfterSaleSupport;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapweb.rootweb.RootJobForExec;
import com.srnpr.zapweb.webmodel.ConfigJobExec;

/**
 * 定时初始化售后单表
 * @remark 
 * @author 任宏斌
 * @date 2019年6月18日
 */
public class JobForCreateAfterSaleService extends RootJobForExec {

	@Override
	public IBaseResult execByInfo(String sInfo) {
		return new DuohzAfterSaleSupport().initDuohzAfterSale(sInfo);
	}

	@Override
	public ConfigJobExec getConfig() {
		return config;
	}

	private static ConfigJobExec config = new ConfigJobExec();
	static {
		config.setExecType("449746990019");
		config.setMaxExecNumber(5);
		config.setNoticeOnce(5);
	}
}
