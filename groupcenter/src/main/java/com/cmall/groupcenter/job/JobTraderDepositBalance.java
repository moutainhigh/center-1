package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.service.TraderDepositBalanceService;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 预存款结算
 * @author panwei
 *
 */
public class JobTraderDepositBalance extends RootJob{

	@Override
	public void doExecute(JobExecutionContext context) {
		TraderDepositBalanceService service=new TraderDepositBalanceService();
		service.doDepositBalance();
	}

}
