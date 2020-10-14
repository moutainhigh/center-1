package com.cmall.productcenter.webfunc;

import java.util.List;

import com.cmall.productcenter.service.MProductService;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;


public class FuncRefreshProduct extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();

		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		if (mResult.upFlagTrue()) {
			ProductService ps = new ProductService();
			String productCode=mSubMap.get("product_code");
			ps.RefreshAllByProductCode(productCode);
		}
		
		return mResult;
	}
}
