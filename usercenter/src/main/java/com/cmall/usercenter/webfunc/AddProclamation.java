package com.cmall.usercenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 公告添加方法
 * @author liqt
 *
 */
public class AddProclamation extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mDataMap2 = new MDataMap();
		
		String createUser = UserFactory.INSTANCE.create().getLoginName();
		String proclamationTitle = mDataMap.get("zw_f_proclamation_title");
		String possessProject = mDataMap.get("zw_f_possess_project");
		String releaseTime = mDataMap.get("zw_f_release_time");
		String proclamationText = mDataMap.get("zw_f_proclamation_text");
		String openingMerchantConfirmation = mDataMap.get("zw_f_opening_merchant_confirmation");
		if(null!=openingMerchantConfirmation&&"449746250001".equals(openingMerchantConfirmation)) {
			mDataMap2.put("opening_merchant_confirmation", openingMerchantConfirmation);
			String proclamationTitleConfirmation = mDataMap.get("zw_f_proclamation_title_confirmation");
			mDataMap2.put("proclamation_title_confirmation", proclamationTitleConfirmation);
		}
		mDataMap2.put("create_user", createUser);
		mDataMap2.put("proclamation_title", proclamationTitle);
		mDataMap2.put("possess_project", possessProject);
		mDataMap2.put("release_time", releaseTime);
		mDataMap2.put("proclamation_text", proclamationText);
		mDataMap2.put("proclamation_code", WebHelper.upCode("GG"));
		
		DbUp.upTable("fh_proclamation_manage").dataInsert(mDataMap2);
		
		return mResult;
	}

}
