package com.cmall.ordercenter.job;

import java.util.List;

import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.model.SkuForCache;
import com.cmall.ordercenter.service.ActivityService;
import com.cmall.ordercenter.service.cache.ProductCacheManage;
import com.srnpr.zapweb.rootweb.RootJob;


public class ActivityJob extends RootJob {
	
	public void doExecute(JobExecutionContext context) {

		ActivityService as = new ActivityService();
		
		as.autoRefreshCacheFroXSXL();
		
		/*ProductCacheManage pcm = new ProductCacheManage();
		
		List<SkuForCache> list = pcm.getSkuForCacheList("8019102587");
		
		if(list!=null || list.size() == 0){
			for(SkuForCache sfc:list){
				System.out.println(sfc.getPsi().getStockNum());
			}
		}*/

	}
}
