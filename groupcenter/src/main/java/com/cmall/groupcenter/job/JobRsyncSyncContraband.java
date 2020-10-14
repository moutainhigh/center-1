package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncSyncContraband;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 5.2.4 同步违禁品配置信息
 * @author cc
 *
 */
public class JobRsyncSyncContraband  extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		RsyncSyncContraband rsyncSyncContraband = new RsyncSyncContraband();
		rsyncSyncContraband.doRsync();
		
	}

}
