package com.cmall.ordercenter.service.cache;

import com.cmall.ordercenter.model.ActivitySkuEntity;
import com.cmall.ordercenter.model.SkuForCache;
import com.cmall.ordercenter.service.ActivityService;
import com.srnpr.zapcom.rootclass.RootCache;

public class ProductSkuCache extends RootCache<String, SkuForCache>{

	public void refresh() {
				
	}

	@Override
	public SkuForCache upOne(String k) {
		
		SkuForCache sfc = new SkuForCache();
		
		ActivityService as = new ActivityService();
		
		sfc = as.getSkuForCache(k);
		
		return sfc;
	}

}
