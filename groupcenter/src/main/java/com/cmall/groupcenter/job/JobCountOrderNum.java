package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.txservice.CountOrderNumService;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 统计前一天微公社的订单数量
 * 
 * @author lijx
 *
 */
public class JobCountOrderNum extends RootJob{
	
	public void doExecute(JobExecutionContext context){
		
		CountOrderNumService service = new CountOrderNumService();
		service.doOrderNum();
		
	} 
	
//public static void main(String[] args) {
//		
//	JobCountOrderNum job = new JobCountOrderNum();
//		job.doExecute(null);	
//	}

}
