package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.txservice.TxReckonOrderService;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 微公社未清分到账检测
 * 
 * @author panwei
 *
 */
public class JobWithdrawVertify extends RootJob {

	public void doExecute(JobExecutionContext context) {
		
		TxReckonOrderService service=new TxReckonOrderService();
		service.noReckonOrderVertify();
	}

}
