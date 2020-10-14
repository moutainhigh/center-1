package com.cmall.ordercenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.listener.DistributeCouponJmsListener;
import com.cmall.systemcenter.enumer.JmsNameEnumer;
import com.cmall.systemcenter.jms.JmsNoticeSupport;
import com.srnpr.zapweb.rootweb.RootJob;


/** 
* @ClassName: DistributeCouponJob 
* @Description: 发放优惠券任务
* @author 张海生
* @date 2015-6-15 下午3:09:56 
*  
*/
public class DistributeCouponJob extends RootJob {
	
	private final static DistributeCouponJmsListener LISTENSER = new DistributeCouponJmsListener();
	
	public void doExecute(JobExecutionContext context) {

		JmsNoticeSupport.INSTANCE.onReveiveQueue(JmsNameEnumer.OnDistributeCoupon, LISTENSER);

	}
}
