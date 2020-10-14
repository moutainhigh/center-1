package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncGiftVoucherStatus;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 同步ld用户礼金券状态明细
 * @author cc
 *
 */
public class JobRsyncGiftVoucherStatus  extends RootJob {

	static Object lock = new Object();

	@Override
	public void doExecute(JobExecutionContext context) {
		/**此处停掉 礼金券所有操作都走异步推送项目 -rhb 20180927*/
		synchronized (lock) {
			
			RsyncGiftVoucherStatus rsyncGiftVoucherStatus = new RsyncGiftVoucherStatus();
			rsyncGiftVoucherStatus.doRsync();
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}


	}

}
