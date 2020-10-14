package com.cmall.ordercenter.tallyorder;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
public class UpdateFinalStatus extends RootFunc{

	/**
	 * 更新数据库财务结算表是否确认的状态
	 */
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		String uid=mSubMap.get("uid");
		
		DbUp.upTable("oc_bill_finance_amount").dataUpdate(new MDataMap("uid",uid,"settle_status","0"),"settle_status", "uid");
		
		mResult.setResultMessage("操作成功");
		return mResult;
	}

}
