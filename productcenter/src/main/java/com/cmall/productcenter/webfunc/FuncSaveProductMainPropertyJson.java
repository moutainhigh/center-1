package com.cmall.productcenter.webfunc;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.MProduct;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.service.MProductService;

import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;

import com.srnpr.zapweb.webdo.WebConst;

import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;


public class FuncSaveProductMainPropertyJson extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		try {
			if (mResult.upFlagTrue()) {
				MProductService pService = new MProductService();
				MDataMap mSubDataMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
				MProduct pp = new MProduct();
				pp = new JsonHelper<MProduct>().StringToObj(
						mSubDataMap.get("json"), pp);
				StringBuffer error = new StringBuffer();
				RootResult rootr = pService.SaveProductPropertySort(pp);
				
				mResult.setResultCode(rootr.getResultCode());
				mResult.setResultMessage(rootr.getResultMessage());
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(909701007);
		}
		return mResult;
		
	}
}
