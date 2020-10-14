package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.support.GroupReckonSupport;
import com.srnpr.zapweb.rootweb.RootJob;

public class GroupReckonJob extends RootJob {

	public void doExecute(JobExecutionContext context) {
		GroupReckonSupport groupReckonSupport = new GroupReckonSupport();
		groupReckonSupport.reckonAllOrders();

	}

}
