package com.cmall.groupcenter.job;


import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncPlusEventInfo;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 同步plus活动
 */
public class JobForRsyncPlusEvent extends RootJob {

	public void doExecute(JobExecutionContext context) {
		new RsyncPlusEventInfo().doRsync();
	}
}
