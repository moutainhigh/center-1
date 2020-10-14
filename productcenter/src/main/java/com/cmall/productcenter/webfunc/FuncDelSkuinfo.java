package com.cmall.productcenter.webfunc;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncDelSkuinfo extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		ProductService service = new ProductService();
		ProductSkuInfo info = new ProductSkuInfo();
		info.setUid(mAddMaps.get("uid"));
		RootResult rootResult = service.deleteSku(info);
		if(StringUtils.isEmpty(rootResult.getResultMessage())){
			mResult.setResultMessage(bInfo(941901062));
		}else {
			mResult.inErrorMessage(941901063, rootResult.getResultMessage());
		}
		return mResult;
	}

}
