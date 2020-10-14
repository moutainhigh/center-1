package com.cmall.ordercenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapweb.rootweb.RootJob;

public class OrderJob extends RootJob {
	
	public void doExecute(JobExecutionContext context) {

		OrderService os = new OrderService();
		
		os.autoChangeToOverFor24hour();
		
		os.autoChangToSuccessFor14Days();
		
	}
}
