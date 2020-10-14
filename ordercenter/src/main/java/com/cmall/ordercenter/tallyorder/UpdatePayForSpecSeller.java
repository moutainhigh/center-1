package com.cmall.ordercenter.tallyorder;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 
 * @author zht
 *
 */
public class UpdatePayForSpecSeller extends RootFunc {

	@Override
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		String uid=mDataMap.get("uid");
		DbUp.upTable("oc_bill_merchant_new_spec").dataUpdate(new MDataMap("uid",uid,"flag","4497476900040009"),"flag", "uid");
		mResult.setResultMessage("操作成功!");
		return mResult;
	}
}
