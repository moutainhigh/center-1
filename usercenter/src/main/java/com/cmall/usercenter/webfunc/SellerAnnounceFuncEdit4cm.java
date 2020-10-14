package com.cmall.usercenter.webfunc;

import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class SellerAnnounceFuncEdit4cm   extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap _mDataMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		String loginname=UserFactory.INSTANCE.create().getLoginName();
		String now=DateUtil.getSysDateTimeString();
		
		_mDataMap.put("update_time", now);   // 更新时间
		_mDataMap.put("update_user", loginname);   // 更新的用户
		
		try {
			DbUp.upTable("uc_seller_announce").dataUpdate(_mDataMap, "", "uid");
			
			mResult.setResultMessage(bInfo(939301009));
		} catch (Exception e) {
			mResult.inErrorMessage(939301010);
		}
		return mResult;
	}
}
