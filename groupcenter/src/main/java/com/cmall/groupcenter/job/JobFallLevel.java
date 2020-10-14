package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.support.GroupAccountSupport;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 自动降级
 * 
 * @author srnpr
 *
 */
public class JobFallLevel extends RootJob {

	public void doExecute(JobExecutionContext context) {
		new GroupAccountSupport().autoFallAccountLevel();

	}

}
