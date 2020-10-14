package com.cmall.productcenter.webfunc;

import com.cmall.productcenter.service.LogisticsService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class LogisticsFunc  extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		MDataMap _mDataMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		LogisticsService service = new LogisticsService();
		try {
			if (mResult.upFlagTrue() == true) {
				//boolean flag = service.add(mDataMap);
				//boolean flag = service.insert(mDataMap);
				boolean flag = service.addCompany(_mDataMap);
				if (flag == false) {
					// 异常处理待定
					mResult.inErrorMessage(939301010);
				}
			}
			mResult.setResultMessage(bInfo(939301009));
		} catch (Exception e) {
			mResult.inErrorMessage(939301010);
		}
		return mResult;
	}

}
