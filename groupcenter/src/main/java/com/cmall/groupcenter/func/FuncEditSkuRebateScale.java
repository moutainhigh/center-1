package com.cmall.groupcenter.func;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncEditSkuRebateScale extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mWebResult=new MWebResult();
		String zid = mDataMap.get("zw_f_zid");
		String flagEnable= mDataMap.get("zw_f_flag_enable");
		
		if("0".equals(flagEnable)){
			mWebResult.inErrorMessage(918508001);
		}
		
		if(mWebResult.upFlagTrue()){
			MDataMap whereDataMap  = new MDataMap();
			whereDataMap.put("flag_enable", "0");
			whereDataMap.put("zid", zid);
			whereDataMap.put("update_user", UserFactory.INSTANCE.create().getLoginName());
			whereDataMap.put("update_time", FormatHelper.upDateTime());
			DbUp.upTable("gc_sku_rebate_scale").dataUpdate(whereDataMap, "flag_enable,update_user,update_time", "zid");
		}
		return mWebResult;
	}

}
