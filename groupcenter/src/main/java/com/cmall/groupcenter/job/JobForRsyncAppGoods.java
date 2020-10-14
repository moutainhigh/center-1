package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.cmall.groupcenter.express.app.service.RsyncSyncAppGoods;
import com.srnpr.zapweb.rootweb.RootJob;
/**
 * 配送状态同步接口
 * @author zmm
 *
 */
public class JobForRsyncAppGoods extends RootJob{

	@Override
	public void doExecute(JobExecutionContext context) {
		RsyncSyncAppGoods rsyncAppGoods = new RsyncSyncAppGoods();
		rsyncAppGoods.doRsync();
	}
//only for test
	public static void main(String[] args) {
		JobForRsyncAppGoods job = new JobForRsyncAppGoods();
		JobExecutionContext context = null;
		try {
			job.execute(context);
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
