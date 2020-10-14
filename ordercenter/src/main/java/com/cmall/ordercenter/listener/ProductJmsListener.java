package com.cmall.ordercenter.listener;

import java.util.List;

import com.cmall.ordercenter.service.cache.ProductCacheManage;
import com.cmall.productcenter.model.PcProductInfoForI;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapzero.root.RootJmsListenser;

public class ProductJmsListener extends RootJmsListenser{
	
	public boolean onReceiveText(String sMessage,MDataMap mDataMap) {
		boolean ret = true;
		
		if(sMessage!=null){
			ProductCacheManage pcm = new ProductCacheManage();
			
			ProductService ps = new ProductService();
			
			List<PcProductInfoForI> list = ps.getProductListForI(sMessage);
			
			if(list == null || list.size()  == 0){
				
				
			}else{
				PcProductInfoForI ppfi = list.get(0);
				
				if(ppfi.getProductSkuInfoList() ==null || ppfi.getProductSkuInfoList().size() == 0){
					
				}else{
					for(ProductSkuInfo sku : ppfi.getProductSkuInfoList()){
						pcm.removeCache(sku.getSkuCode());
					}
				}
			}
		}
		
		return ret;
	}
}
