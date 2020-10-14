package com.cmall.productcenter.service;

import org.quartz.JobExecutionContext;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapweb.rootweb.RootJob;


/**
 * 惠家有 沙皮狗  solr5.2.1
 * 每日晚上凌晨定时全量更新索引库
 * @author zhouguohui
 * @version 1.0
 */
public class SolrDataIndexService extends RootJob {
	
	//初始化两个系统编号
	private static final String[] sellerCode={"SI2003"};
	
	public void doExecute(JobExecutionContext context) {
		
		synchronized (SolrDataIndexService.class) {
			
			for(int i=0;i<sellerCode.length;i++){
				
				MDataMap dataMap = new MDataMap();
				dataMap.put("sellercode", sellerCode[i]);
				try {
					WebClientSupport.upPost(TopUp.upConfig("productcenter.webclienturladdlist"), dataMap);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			}
			
		}
		
	}
	


}
