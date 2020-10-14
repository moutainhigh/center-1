package com.cmall.groupcenter.job;


import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncGetAccmDetail;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 积分变化明细(惠家有通路)同步
 */
public class JobForGetAccmDetail extends RootJob {

	private static Object lock = new Object();
	
	public void doExecute(JobExecutionContext context) {
		synchronized (lock) {
			new RsyncGetAccmDetail().doRsync();
		}
	}
	
	
}