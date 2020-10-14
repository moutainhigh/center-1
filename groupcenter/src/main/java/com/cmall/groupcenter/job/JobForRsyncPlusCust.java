package com.cmall.groupcenter.job;


import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncPlusCustInfo;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 同步plus会员信息
 */
public class JobForRsyncPlusCust extends RootJob {

	public void doExecute(JobExecutionContext context) {
		new RsyncPlusCustInfo().doRsync();
	}
}
