package com.cmall.productcenter.service;

import org.quartz.JobExecutionContext;

import com.cmall.systemcenter.enumer.JmsNameEnumer;
import com.cmall.systemcenter.jms.JmsNoticeSupport;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 以前更新商品后刷缓存,并向AQ的OnProductUpdate队列中放入一条消息
 * 此类监听到后更新solr索引。现改为更新商品后刷缓存，然后调用
 * com.cmall.systemcenter.jms.ProductJmsSupport.updateSolrData(productcode)
 * 更新solr中此商品的索引。
 * 2016/8/3 By Ligj
 * 
 * 2015 12 01 最新索引库
 * 单个商品增量更新方法
 * 用于solr 5.2.1 版本
 * @deprecated
 * @see com.cmall.systemcenter.jms.ProductJmsSupport.updateSolrData(productcode)
 * @author zhouguohui
 *
 */
public class SolrDataUpdateJob extends RootJob{

	private final static SolrDataUpdateService update = new SolrDataUpdateService();
	public void doExecute(JobExecutionContext context) {
		JmsNoticeSupport.INSTANCE.onReveiveQueue(JmsNameEnumer.OnProductUpdate, update);
	}

}
