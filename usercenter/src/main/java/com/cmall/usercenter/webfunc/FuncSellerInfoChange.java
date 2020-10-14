package com.cmall.usercenter.webfunc;

import org.apache.commons.lang.StringUtils;

import com.cmall.systemcenter.service.FlowService;
import com.cmall.usercenter.service.SellerInfoService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncSellerInfoChange extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		// TODO Auto-generated method stub
		// return null;

		MWebResult mResult = new MWebResult();

		if (StringUtils.isNotEmpty(mDataMap.get("selleruid"))) {

			String sUidString = mDataMap.get("selleruid");

			mResult = new SellerInfoService().changeStatusByUid(sUidString,
					"4497172300040004", UserFactory.INSTANCE.create()
							.getUserCode());

		}

		return mResult;

	}

}
