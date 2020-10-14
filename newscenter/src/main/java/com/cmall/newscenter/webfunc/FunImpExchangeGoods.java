package com.cmall.newscenter.webfunc;


import com.cmall.newscenter.service.TxExchangeGoodsService;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FunImpExchangeGoods extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mWebResult = new MWebResult();

		MDataMap mInputMap = upFieldMap(mDataMap);

		if (mWebResult.upFlagTrue()) {
			String user = UserFactory.INSTANCE.create().getLoginName();
			TxExchangeGoodsService txExchangeGoodsService = BeansHelper
					.upBean("bean_com_cmall_newscenter_service_TxExchangeGoodsService");

		mWebResult.inOtherResult(txExchangeGoodsService
					.insertExchangeGoods(mInputMap.get("description"),user,UserFactory.INSTANCE.create().getManageCode()));

		}
		return mWebResult;
	}

}
