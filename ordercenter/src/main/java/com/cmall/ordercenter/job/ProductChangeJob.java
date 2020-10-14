package com.cmall.ordercenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.listener.ProductJmsListener;
import com.cmall.ordercenter.listener.SkuJmsListener;
import com.cmall.productcenter.listener.IllegalKeywordsListener;
import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapzero.enumer.EJmsMessageType;
import com.srnpr.zapzero.support.JmsSupport;

public class ProductChangeJob extends RootJob {
	
	private final static ProductJmsListener LISTENSER = new ProductJmsListener();
	
	private final static SkuJmsListener LISTENSERSku = new SkuJmsListener();
	
	private final static IllegalKeywordsListener illegalKeywordsListener = new IllegalKeywordsListener();
	
	public void doExecute(JobExecutionContext context) {

		JmsSupport.getInstance().addTopicLisense(ProductJmsSupport.ProductJmsTypeName, ProductJmsSupport.ProductJmsTypeName,
				EJmsMessageType.Toplic, LISTENSER);
		
		JmsSupport.getInstance().addTopicLisense(ProductJmsSupport.SkuJmsTypeName, ProductJmsSupport.SkuJmsTypeName,
				EJmsMessageType.Toplic, LISTENSERSku);
		
		JmsSupport.getInstance().addTopicLisense(ProductJmsSupport.IllegalWordsJmsTypeName, ProductJmsSupport.IllegalWordsJmsTypeName,
				EJmsMessageType.Toplic, illegalKeywordsListener);

	}
}
