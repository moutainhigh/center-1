package com.cmall.productcenter.service.api;

import com.cmall.productcenter.model.api.ApiForSearchSolrDataInput;
import com.cmall.productcenter.model.api.ApiForSearchSolrDataResult;
import com.cmall.productcenter.util.SolrDataUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 惠家友商品信息缓存到词库
 * @author zhouguohui
 *
 */
public class ApiForSearchSolrData extends RootApiForManage<ApiForSearchSolrDataResult,ApiForSearchSolrDataInput>{

	public ApiForSearchSolrDataResult Process(
			ApiForSearchSolrDataInput inputParam, MDataMap mRequestMap) {
		ApiForSearchSolrDataResult assd = new ApiForSearchSolrDataResult();
		
		try{
			
			/**20160307添加 新版solr5.2.1**/
			if(TopUp.upConfig("productcenter.hjywebclient").equals("yes")){
				 MDataMap mDataMap = new MDataMap();
				 mDataMap.put("sellercode", getManageCode());
				
			    String num =  WebClientSupport.upPost(TopUp.upConfig("productcenter.webclienturladdlist"), mDataMap);
				if(num.equals("1")){
					assd.setNum(1);
				}else{
					assd.setNum(0);
				}
			}else{
				new SolrDataUtil().addSolrData(getManageCode());
				assd.setNum(1);
			}
			
			
		}catch(Exception e){
			assd.setNum(0);
			e.printStackTrace();
		}
		
		return assd;
		
		
	}
}
