package com.cmall.ordercenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.cmall.ordercenter.service.AccountInfoService;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class UpdateAccount extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		try {
			if (mResult.upFlagTrue() == true) {
				AccountInfoService service = new AccountInfoService();
				boolean flag = service.updateAccountInfo(mDataMap);
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
