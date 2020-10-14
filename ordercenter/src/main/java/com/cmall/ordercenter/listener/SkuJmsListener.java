package com.cmall.ordercenter.listener;

import com.cmall.ordercenter.service.cache.ProductCacheManage;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapzero.root.RootJmsListenser;

public class SkuJmsListener extends RootJmsListenser{
	
	public boolean onReceiveText(String sMessage,MDataMap mDataMap) {
		boolean ret = true;
		
		if(sMessage!=null){
			ProductCacheManage pcm = new ProductCacheManage();
			pcm.removeCache(sMessage);
		}
		
		return ret;
	}
}

