package com.cmall.newscenter.webfunc;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncEditForMemberLevel extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult result = new MWebResult();
		MDataMap mInputMap = upFieldMap(mDataMap);
		mInputMap.put("update_user", UserFactory.INSTANCE.create().getLoginName());
		mInputMap.put("update_time", FormatHelper.upDateTime());
		if (result.upFlagTrue()) {
			DbUp.upTable("mc_member_level").update(mInputMap);
		}
		return result;
	}

}
