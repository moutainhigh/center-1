package com.cmall.ordercenter.service.cache;

import java.util.ArrayList;
import java.util.List;

import com.cmall.ordercenter.model.ActivitySkuEntity;
import com.cmall.ordercenter.model.OcActivity;
import com.cmall.ordercenter.model.SkuForCache;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;

/**   
* 
* 商品缓存的管理
* 项目名称：ordercenter   
* 类名称：ProductCacheManage   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-11-12 上午11:31:32   
* 修改人：yanzj
* 修改时间：2013-11-12 上午11:31:32   
* 修改备注：   
* @version    
*    
*/
public class ProductCacheManage {
	
	private static ProductSkuCache psc = new ProductSkuCache();
	
	
	/**
	 * 取得所有需要缓存的数据
	 * @return
	 */
	public List<SkuForCache> getSkuForCacheList(String skuStr){
		
		List<SkuForCache> retList = new ArrayList<SkuForCache>();
		if(skuStr == null || skuStr.equals("")){
			return retList;
		}else{
			
			String[] ary = skuStr.split(",");
			
			for(int i=0;i<ary.length;i++) {
				SkuForCache sfc = psc.upValue(ary[i]);
				
				//if(sfc.getAse()!=null){
				//	sfc = psc.upOne(ary[i]);
				//}
				
				retList.add(sfc);
			}
		}
		
		return retList;
	}
	
	/**
	 * 清除缓存
	 * @param skuStr
	 */
	public void removeCache(String skuStr){
		if(skuStr == null || skuStr.equals("")){
			return ;
		}else{
			String[] ary = skuStr.split(",");
			
			for(int i=0;i<ary.length;i++) {
				psc.removeByKey(ary[i]);
				
				//SkuForCache sfc = psc.upOne(ary[i]);
				//psc.inElement(ary[i], sfc);
			}
		}
	}
	

}
