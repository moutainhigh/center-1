package com.cmall.groupcenter.job;


import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncGetRtnOrdDate;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 同步LD系统拒收退货相关信息
 */
public class JobForGetRtnOrdDate extends RootJob {

	private static Object lock = new Object();
	
	public void doExecute(JobExecutionContext context) {
		synchronized (lock) {
			new RsyncGetRtnOrdDate().doRsync();
		}
	}
	
	
}