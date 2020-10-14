package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.support.GroupAccountSupport;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 定期转换账户
 * 
 * @author srnpr
 *
 */
public class JobAutoChangeReckon extends RootJob {

	public void doExecute(JobExecutionContext context) {
		new GroupAccountSupport().aotoConvertAccount();

	}

}
