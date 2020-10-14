package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.groupface.IRsyncProcess;
import com.cmall.groupcenter.homehas.RsyncGetCustByDate;
import com.cmall.groupcenter.homehas.RsyncGetShipmentStat;
import com.cmall.groupcenter.homehas.RsyncGetTVByDate;
import com.cmall.groupcenter.homehas.RsyncSyncGoods;
import com.cmall.groupcenter.homehas.RsyncSyncOrders;
import com.cmall.groupcenter.homehas.RsyncSyncgetSYGoodbyColor;
import com.cmall.groupcenter.support.GroupReckonSupport;
import com.srnpr.zapweb.rootweb.RootJob;

public class RsyncHomeHasMember extends RootJob {

	public void doExecute(JobExecutionContext context) {
		// TODO Auto-generated method stub
		
		IRsyncProcess rsyncGetCustByDate=new RsyncGetCustByDate();
		rsyncGetCustByDate.doRsync();
		
		
		
		
		
		
		
	}

}
