package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.txservice.TxTraderFoundsService;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 预存款预警提醒
 * @author panwei
 *
 */
public class JobPreWithdrawNotify extends RootJob{

	public void doExecute(JobExecutionContext context) {
		
		TxTraderFoundsService foundService=new TxTraderFoundsService();
		
		foundService.preWithdrawNotify();
		
		
	}
	
}
