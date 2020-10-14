package com.cmall.productcenter.service;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapzero.root.RootJmsListenser;

/***
 * 20160301  solr5.2.1的单个商品修改
 * @author zhouguohui
 *
 */
public class SolrDataUpdateService extends RootJmsListenser{

	public boolean onReceiveText(String sMessage, MDataMap mPropMap) {
		
		if(mPropMap!=null && !mPropMap.isEmpty()){
			try {
				WebClientSupport.upPost(TopUp.upConfig("productcenter.webclienturladdone"), mPropMap);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		/*if(StringUtils.isNotEmpty(sMessage.trim())){
			*//**20151012 添加沙皮狗的单个商品更新5.2.1版本信息**//*
			*//**20160317添加惠家有的单个商品更新5.2.1版本信息**//*
			MDataMap dataMap = new MDataMap();
			dataMap.put("productCode", sMessage);
			try {
				WebClientSupport.upPost(TopUp.upConfig("productcenter.webclienturladdone"), dataMap);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
			
		
		return true;
	}
	

}
