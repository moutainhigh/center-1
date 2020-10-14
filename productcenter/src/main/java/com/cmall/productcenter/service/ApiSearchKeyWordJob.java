package com.cmall.productcenter.service;

import org.quartz.JobExecutionContext;

import com.cmall.systemcenter.enumer.JmsNameEnumer;
import com.cmall.systemcenter.jms.JmsNoticeSupport;
import com.srnpr.zapweb.rootweb.RootJob;

/***
 * 搜索关键词存储
 * @author zhouguohui
 *
 */
public class ApiSearchKeyWordJob extends RootJob{
	private final static SearchKeyWordService keyWord = new SearchKeyWordService();
	public void doExecute(JobExecutionContext context) {
		JmsNoticeSupport.INSTANCE.onReveiveQueue(JmsNameEnumer.OnSearchKeyWord,keyWord);
	}

}
