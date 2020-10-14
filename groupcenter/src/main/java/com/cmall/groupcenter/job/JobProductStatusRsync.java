package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.api.ApiForRsyncProductStatus;
import com.cmall.systemcenter.enumer.JmsNameEnumer;
import com.cmall.systemcenter.jms.JmsNoticeSupport;
import com.srnpr.zapweb.rootweb.RootJob;


/**
 * 商品状态同步Job
 * @author ligj
 *
 */
public class JobProductStatusRsync extends RootJob {

	private final static ApiForRsyncProductStatus rsynProductStatus = new ApiForRsyncProductStatus();
	public void doExecute(JobExecutionContext context) {
		JmsNoticeSupport.INSTANCE.onReveiveToplic(JmsNameEnumer.OnProductStatusChange,rsynProductStatus);
	}

}
