package com.cmall.productcenter.util;

import com.srnpr.zapcom.baseface.IBaseInit;
import com.srnpr.zapcom.rootclass.RootInit;
import com.srnpr.zapcom.topdo.TopUp;


/**
 * 初始化solr集群响应时间过长的问题
 * @author zhouguohui
 *
 */
public class InitCloudSolrServerJyh extends RootInit implements IBaseInit {

	public static final String ZKHOST=TopUp.upConfig("productcenter.zkHost");
	public static final String CLUSTER=TopUp.upConfig("productcenter.cluster").trim();
	public static final String JYH = "SI2009";
	public static final String ASSOCIATE = "Associate2009";
	@Override
	public  boolean onInit() {
		if(CLUSTER.equals("yes")){
			
			new SolrQueryUtil();
			/****初始化家有汇*****/
			SolrQueryUtil.getSolrServer(JYH);
			/****初始化家有汇自动联想*****/
			SolrQueryUtil.getSolrServer(ASSOCIATE);
		}	
	
		return true;
	}

	@Override
	public boolean onDestory() {
		// TODO Auto-generated method stub
		return true;
	}

}
