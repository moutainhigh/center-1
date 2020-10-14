package com.cmall.productcenter.service;

import org.quartz.JobExecutionContext;

import com.cmall.productcenter.util.SolrDataUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 沙皮狗定时执行索引库
 * @author zhouguohui
 *
 */
public class ApiForSearchSolrDataSpgJob extends RootJob{

	public void doExecute(JobExecutionContext context) {
		String sellerCode ="SI3003";//店铺编号
		try{
			/**20150909添加 新版solr5.2.1**/
			if(TopUp.upConfig("productcenter.spgwebclient").equals("yes")){
				 MDataMap mDataMap = new MDataMap();
				 mDataMap.put("sellercode", sellerCode);
			     WebClientSupport.upPost(TopUp.upConfig("productcenter.webclienturladdlist"), mDataMap);
			}else{
				new SolrDataUtil().addSolrData(sellerCode);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
