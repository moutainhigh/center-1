package com.cmall.groupcenter.jd.job;

import org.quartz.JobExecutionContext;

import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 定时同步京东商品池信息
 */
public class JobForRsyncAllProduct extends RootJob{

	// 防止并发执行
	private static boolean isProcessing = false;
	private static Object lock = new Object();
	
	@Override
	public void doExecute(JobExecutionContext context) {
		synchronized (lock) {
			if(isProcessing) {
				return;
			}
			isProcessing = true;
		}
		
		try {
			doRsync();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		isProcessing = false;
	}
	
	private void doRsync() {
		new RsyncJDProductPool().doRsyncProduct();
	}
	
}
