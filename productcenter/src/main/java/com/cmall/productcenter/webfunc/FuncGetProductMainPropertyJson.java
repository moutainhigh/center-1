package com.cmall.productcenter.webfunc;

import java.util.List;

import com.cmall.productcenter.service.MProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;


public class FuncGetProductMainPropertyJson extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();

		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		if (mResult.upFlagTrue()) {
			MProductService ms = new MProductService();
			String productCode=mDataMap.get("product_code");
			mResult.setResultObject(ms.GetMProductList(productCode));
		}
		
		return mResult;
	}
}
