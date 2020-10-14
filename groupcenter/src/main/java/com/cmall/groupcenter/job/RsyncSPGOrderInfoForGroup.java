package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.service.RsyncOrderBeanFactory;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 定时任务：与微公社同步订单获取返利
 * @author pangjh
 *
 */
public class RsyncSPGOrderInfoForGroup extends RootJob {

	public void doExecute(JobExecutionContext context) {
		
		RsyncOrderBeanFactory.getInstance().getRsyncSellerOrderService().doRsyncSPG();
		
	}

}
