package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncInnerCustInfo;
import com.cmall.groupcenter.homehas.RsyncInnerCustInfo_h;
import com.srnpr.zapweb.rootweb.RootJob;

public class JobForRsyncInnerCustInfo extends RootJob {

	public void doExecute(JobExecutionContext context) {
		
		RsyncInnerCustInfo rsyncInnerCustInfo = new RsyncInnerCustInfo();
		rsyncInnerCustInfo.doRsync();
		
		
//		RsyncInnerCustInfo_h custInfo_h = new RsyncInnerCustInfo_h();
//		custInfo_h.doRsync();
		
	}
	
}
