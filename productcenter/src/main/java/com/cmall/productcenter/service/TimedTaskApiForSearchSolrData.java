package com.cmall.productcenter.service;

import org.quartz.JobExecutionContext;

import com.cmall.productcenter.util.SolrDataUtil;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 惠家友定时更新索引库
 * @author zhouguohui
 *
 */
public class TimedTaskApiForSearchSolrData extends RootJob {

	public void doExecute(JobExecutionContext context) {
		String sellerCode ="SI2003";//店铺编号
		new SolrDataUtil().addSolrData(sellerCode);
		/*String cluster =  TopUp.upConfig("productcenter.cluster");
		SearchSolrDataService ss = new SearchSolrDataService();
		ProductService ps  =  new  ProductService();
		try{
			ss.insertSolrData(sellerCode,cluster,null,ps.getMinProductActivity(null));
		}catch(Exception e){
			e.printStackTrace();
		}*/
	}
}
