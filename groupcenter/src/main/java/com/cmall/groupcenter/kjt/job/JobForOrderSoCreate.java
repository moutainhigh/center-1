package com.cmall.groupcenter.kjt.job;

import com.cmall.groupcenter.service.OrderForKJT;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapweb.rootweb.RootJobForExec;
import com.srnpr.zapweb.webmodel.ConfigJobExec;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 异步定时生成发货单
 * @author jlin
 *
 */
public class JobForOrderSoCreate extends RootJobForExec {

	@Override
	public IBaseResult execByInfo(String sInfo) {
		
		MWebResult mWebResult = new MWebResult();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		OrderForKJT forKJT = new OrderForKJT();
		if (!forKJT.rsyncOrder(sInfo)) {
			mWebResult.inErrorMessage(918519135);
		}
		
		return mWebResult;
	}

	
	
	private static ConfigJobExec config = new ConfigJobExec();
	@Override
	public ConfigJobExec getConfig() {
		config.setExecType("449746990003");
		config.setMaxExecNumber(20);
		return config;
	}
}
