package com.cmall.groupcenter.coupon;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class CouponImportManageService extends RootFunc {
	
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mWebResult = new MWebResult();
		MDataMap mInputMap = upFieldMap(mDataMap);
		mInputMap.put("creator", UserFactory.INSTANCE.create().getLoginName());
		mInputMap.put("create_time", FormatHelper.upDateTime().trim());
		DbUp.upTable("gc_coupon_import_manage").dataInsert(mInputMap);
		return mWebResult;
	}
	
}