package com.cmall.ordercenter.webfunc;

import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.FuncAdd;
import com.srnpr.zapweb.webmodel.MWebResult;

public class InvoiceFuncAdd extends FuncAdd{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		String content = mDataMap.get("zw_f_invoice_content");
		String manageCode = mDataMap.get("zw_f_manage_code");
		int count = DbUp.upTable("oc_invoice").count("invoice_content",content,"manage_code",manageCode);
		if(count > 0){
			mResult.inErrorMessage(939301229);
			return mResult;
		}
		/*系统当前时间*/
		String create_time = DateUtil.getSysDateTimeString();
		/*获取当前登录人*/
		String create_user = UserFactory.INSTANCE.create().getLoginName();
		mDataMap.put("zw_f_create_time", create_time);
		mDataMap.put("zw_f_create_user", create_user);
		mDataMap.put("zw_f_update_time", create_time);
		mDataMap.put("zw_f_update_user", create_user);
		try{
			if (mResult.upFlagTrue()) {
				mResult = super.funcDo(sOperateUid, mDataMap);
			}
		}catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(939301228);
		}
		return mResult;
	}

}
