package com.cmall.groupcenter.jd.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.jd.JdSyncOrderStatusSupport;
import com.srnpr.zapweb.rootweb.RootJob;
/**
 * 更新京东订单状态接口
 * @author 张圣瑞
 *
 */
public class JdSyncOrderStatus extends RootJob{

	@Override
	public void doExecute(JobExecutionContext context) {
		JdSyncOrderStatusSupport jdSyncOrderStatusSupport = new JdSyncOrderStatusSupport();
		jdSyncOrderStatusSupport.syncOrderStatus(null);
	}

}
