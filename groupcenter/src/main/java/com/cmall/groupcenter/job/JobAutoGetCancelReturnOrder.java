package com.cmall.groupcenter.job;

import com.cmall.groupcenter.homehas.RsyncDoCancelReturnOrder;
import com.cmall.groupcenter.support.GroupAccountSupport;
import com.srnpr.zapweb.rootweb.RootJob;
import org.quartz.JobExecutionContext;

/**
 *获得需要取消退货的订单的流程的数据
 * 
 * @author srnpr
 *
 */
public class JobAutoGetCancelReturnOrder extends RootJob {

	public void doExecute(JobExecutionContext context) {
		RsyncDoCancelReturnOrder doCancelReturnOrder = new RsyncDoCancelReturnOrder();
		doCancelReturnOrder.doRsync();
	}

}
