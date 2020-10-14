package com.cmall.groupcenter.duohuozhu.job;

import com.cmall.groupcenter.duohuozhu.support.DuohzAfterSaleSupport;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.rootweb.RootJobForExec;
import com.srnpr.zapweb.webmodel.ConfigJobExec;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 异步创建多货主售后单
 * @remark 
 * @author 任宏斌
 * @date 2019年6月18日
 */
public class JobForApplyAfterSale extends RootJobForExec {

	@Override
	public IBaseResult execByInfo(String sInfo) {
		MWebResult mWebResult = new MWebResult();
		RootResult rootResult = new DuohzAfterSaleSupport().execApplyAfterSale(sInfo);
		mWebResult.inOtherResult(rootResult);
		return mWebResult;
	}

	@Override
	public ConfigJobExec getConfig() {
		ConfigJobExec config = new ConfigJobExec();
		config.setExecType("449746990020");
		config.setMaxExecNumber(30);
		return config;
	}

}
