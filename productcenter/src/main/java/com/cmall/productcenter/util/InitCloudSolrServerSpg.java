package com.cmall.productcenter.util;

import com.srnpr.zapcom.baseface.IBaseInit;
import com.srnpr.zapcom.rootclass.RootInit;
import com.srnpr.zapcom.topdo.TopUp;

/**
 * 沙皮狗初始化
 * @author zhouguohui
 *
 */
public class InitCloudSolrServerSpg extends RootInit implements IBaseInit{

	public static final String ZKHOST=TopUp.upConfig("productcenter.zkHost");
	public static final String CLUSTER=TopUp.upConfig("productcenter.cluster").trim();
	public static final String SPG = "SI3003";
	@Override
	public  boolean onInit() {
		
		if(CLUSTER.equals("yes")){
			new SolrQueryUtil();
			/****初始化沙皮狗*****/
			SolrQueryUtil.getSolrServer(SPG);
		}	
		return true;
	}

	@Override
	public boolean onDestory() {
		// TODO Auto-generated method stub
		return true;
	}
}
