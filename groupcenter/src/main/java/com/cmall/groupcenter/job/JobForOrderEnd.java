package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 15天 订单成功
 * @author Administrator
 *
 */
public class JobForOrderEnd extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		
		OrderService os = new OrderService();
		
		os.autoChangToSuccessFor15Days();
		
	}

	
	public static void main(String[] args) {
		
		JobForOrderEnd jobForOrderEnd = new JobForOrderEnd();
		jobForOrderEnd.doExecute(null);
		
	}
	
}
