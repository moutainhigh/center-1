package com.cmall.productcenter.webfunc;

import com.cmall.productcenter.service.ProductDraftBoxService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class DeleteDrafts extends RootFunc{
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap){
		MWebResult mResult = new MWebResult();
		MDataMap mDelMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		//获取当前操作人
		MUserInfo userInfo = null;
		String userCode = "";
		if (UserFactory.INSTANCE != null) {
			try {
				userInfo = UserFactory.INSTANCE.create();
			} catch (Exception e) {
			}
			if (userInfo != null) {
				userCode = userInfo.getUserCode();
			}
		}

		try {
			if (mResult.upFlagTrue()) {
				new ProductDraftBoxService().delDraftBoxProduct(mDelMaps.get("uid"),"", userCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mResult;
	}
}
