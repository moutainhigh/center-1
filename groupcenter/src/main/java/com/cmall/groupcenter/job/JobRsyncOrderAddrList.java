package com.cmall.groupcenter.job;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncOrderAddrList;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.rootweb.RootJob;

/***
 * 定时同步LD区域信息
 * @author jlin
 *
 */
public class JobRsyncOrderAddrList extends RootJob {

	public void doExecute(JobExecutionContext context) {
			
		
		 String lockuuid=WebHelper.addLock(300, "RsyncOrderAddrList");
		 if(StringUtils.isBlank(lockuuid)){
			 return ;
		 }
		 
		RsyncOrderAddrList rsyncOrderAddrList = new RsyncOrderAddrList();
		rsyncOrderAddrList.doRsync();
		
		
		WebHelper.unLock(lockuuid);
	}
	
}
