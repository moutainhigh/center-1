package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.txservice.TxReckonOrderService;
import com.cmall.groupcenter.txservice.TxWithdrawCashService;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 微公社清分检测
 * 
 * @author panwei
 *
 */
public class JobReckonVertify extends RootJob {

	public void doExecute(JobExecutionContext context) {
		
		TxReckonOrderService service=new TxReckonOrderService();
		service.reckonOrderVertify();
	}

}
