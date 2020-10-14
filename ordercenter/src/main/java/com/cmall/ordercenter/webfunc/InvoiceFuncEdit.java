package com.cmall.ordercenter.webfunc;

import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.FuncEdit;
import com.srnpr.zapweb.webmodel.MWebResult;

public class InvoiceFuncEdit extends FuncEdit {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();
		String content = mDataMap.get("zw_f_invoice_content");
		String manageCode = mDataMap.get("zw_f_manage_code");
		String uid = mDataMap.get("zw_f_uid");
		MDataMap mData = new MDataMap();
		mData.put("content", content);
		mData.put("manageCode", manageCode);
		mData.put("uid", uid);
		int count = DbUp
				.upTable("oc_invoice")
				.dataCount(
						"invoice_content=:content and manage_code=:manageCode and uid<>:uid",
						mData);
		if (count > 0) {
			mResult.inErrorMessage(939301229);
			return mResult;
		}
		/* 系统当前时间 */
		String update_time = DateUtil.getSysDateTimeString();
		/* 获取当前登录人 */
		String update_user = UserFactory.INSTANCE.create().getLoginName();
		mDataMap.put("zw_f_update_time", update_time);
		mDataMap.put("zw_f_update_user", update_user);
		try {
			if (mResult.upFlagTrue()) {
				mResult = super.funcDo(sOperateUid, mDataMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(939301228);
		}
		return mResult;
	}

}
