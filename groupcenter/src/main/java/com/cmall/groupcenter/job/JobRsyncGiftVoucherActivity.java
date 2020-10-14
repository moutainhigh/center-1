package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncGiftVoucherActivity;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 5.2.0 同步LD礼金券活动
 * @author cc
 *
 */
public class JobRsyncGiftVoucherActivity  extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		/**此处已停掉 礼金券所有操作都走异步推送项目 -rhb 20180927*/
		RsyncGiftVoucherActivity rsyncGiftVoucherActivity = new RsyncGiftVoucherActivity();
		rsyncGiftVoucherActivity.doRsync();
	}

}
