package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncPlusCustInfo;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * plus会员变动信息
 */
public class JobForPlusCustInfo extends RootJob {

	public void doExecute(JobExecutionContext context) {
		 new RsyncPlusCustInfo().doRsync();
	}
}