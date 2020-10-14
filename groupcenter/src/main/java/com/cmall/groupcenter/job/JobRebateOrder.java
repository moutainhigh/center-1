package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.support.RebateOrderSupport;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 执行返利订单任务
 * @author chenbin
 *
 */
public class JobRebateOrder extends RootJob{

	public void doExecute(JobExecutionContext context) {
		RebateOrderSupport rebateOrderSupport=new RebateOrderSupport();
		rebateOrderSupport.rebateAllOrders();
	}

	
}
