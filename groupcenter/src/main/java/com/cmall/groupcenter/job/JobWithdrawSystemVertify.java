package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.txservice.TxWithdrawCashService;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 微公社提现系统审核
 * 
 * @author panwei
 *
 */
public class JobWithdrawSystemVertify extends RootJob {

	public void doExecute(JobExecutionContext context) {
		
		TxWithdrawCashService service=new TxWithdrawCashService();
		service.WithdrawSystemVertify();
	}

}
