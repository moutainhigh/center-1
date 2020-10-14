package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.groupface.IRsyncProcess;
import com.cmall.groupcenter.homehas.RsyncGetShipmentStat;
import com.cmall.groupcenter.homehas.RsyncSyncOrdersV2;
import com.srnpr.zapweb.rootweb.RootJob;

public class RsyncHomeHasOrder extends RootJob {

	public void doExecute(JobExecutionContext context) {
		//IRsyncProcess rsyncGetOrders = new RsyncSyncOrders();
		IRsyncProcess rsyncGetOrders = new RsyncSyncOrdersV2();
		rsyncGetOrders.doRsync();

		IRsyncProcess rsyncShipMent = new RsyncGetShipmentStat();
		rsyncShipMent.doRsync();

	}

}
