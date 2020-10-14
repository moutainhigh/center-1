package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.service.CheckUserMoneyService;
import com.cmall.groupcenter.txservice.CountOrderNumService;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 每日用户金额检测定时
 * 
 * @author lijx
 *
 */

public class JobCheckUserMoney extends RootJob{
	
public void doExecute(JobExecutionContext context){
		
	CheckUserMoneyService service = new CheckUserMoneyService();
		service.doUserMoney();
		
	} 
	
	 public static void main(String[] args) {
	
	 JobCheckUserMoney job = new JobCheckUserMoney();
	 job.doExecute(null);
	 }
}
