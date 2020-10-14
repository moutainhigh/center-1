package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncPlusEventInfo;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 同步plus活动信息
 */
public class JobForPlusEventInfo extends RootJob {
	
	private static Object lock = new Object();

	public void doExecute(JobExecutionContext context) {
		synchronized (lock) {
			new RsyncPlusEventInfo().doRsync();
		}
		 
	}
	
}