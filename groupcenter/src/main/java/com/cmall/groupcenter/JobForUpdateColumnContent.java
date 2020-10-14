package com.cmall.groupcenter;

import org.quartz.JobExecutionContext;
import com.cmall.groupcenter.service.ApiForUpdateColumnContentService;
import com.srnpr.zapweb.rootweb.RootJob;


/** 
* @ClassName: JobForUpdateColumnContent 
* @Description: 同步更新 配置栏目内容 （目前只针对三栏两行和两栏两行）
*  
*/
public class JobForUpdateColumnContent  extends RootJob{

	@Override
	public void doExecute(JobExecutionContext context) {    
		new ApiForUpdateColumnContentService(null,null).run();
	}
	
}
