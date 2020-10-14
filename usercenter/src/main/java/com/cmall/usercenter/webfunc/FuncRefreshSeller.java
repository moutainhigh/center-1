package com.cmall.usercenter.webfunc;

import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncRefreshSeller extends RootFunc {

public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();

		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		if (mResult.upFlagTrue()) {
			
			String sellerCode=mSubMap.get("seller_code");
			
			//通知前端生成静态页面
			ProductJmsSupport pjs = new ProductJmsSupport();
			
		
			
			String jsonData="{\"type\":\"shop.index\",\"data\":\""+sellerCode+"\"}";
			pjs.OnChangeSku(jsonData);
		
			
			
		}
		
		return mResult;
	}

}
