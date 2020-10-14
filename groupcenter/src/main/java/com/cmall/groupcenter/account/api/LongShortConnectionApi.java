package com.cmall.groupcenter.account.api;

import com.cmall.groupcenter.account.model.LongShortConnectionInput;
import com.cmall.groupcenter.account.model.LongShortConnectionResult;
import com.cmall.groupcenter.accountmarketing.util.LongShortUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.url.ShortUrl;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;
/**
 * 长连接转换短连接
 * @author shiyz
 *
 */
public class LongShortConnectionApi extends RootApiForManage<LongShortConnectionResult, LongShortConnectionInput>{

	public LongShortConnectionResult Process(
			LongShortConnectionInput inputParam, MDataMap mRequestMap) {
		
		LongShortConnectionResult result = new LongShortConnectionResult();
		
		if(result.upFlagTrue()){
			
				result.setShortUrl(new LongShortUtil().getShortUrl(inputParam.getLongUrl()));
				
		}
		
		return result;
	}

}
