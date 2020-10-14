package com.cmall.groupcenter.jd.job;


import org.apache.commons.logging.LogFactory;
import org.apache.log4j.helpers.LogLog;
import org.quartz.JobExecutionContext;


import com.srnpr.zapweb.rootweb.RootJob;
/** 
* @ClassName: JobForSyncJDProductInfo 
* @Description: 同步京东对接的商品
*  
*/
public class JobForSyncJDProductInfo  extends RootJob{

	@Override
	public void doExecute(JobExecutionContext context) {
		// TODO Auto-generated method stub
    	LogFactory.getLog(getClass()).warn("京东定时方法 "+this.getClass().getName()+"启动");
		new RsyncJDProductsInfo().doProcess();
	}

}
