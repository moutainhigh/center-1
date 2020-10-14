package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncGetTVByDate;
import com.srnpr.zapweb.rootweb.RootJob;

public class RsyncHomeHasTvList extends RootJob {

	public void doExecute(JobExecutionContext context) {

		// 同步TV
		RsyncGetTVByDate getTVByDate = new RsyncGetTVByDate();
		getTVByDate.doRsync();

	}

}
