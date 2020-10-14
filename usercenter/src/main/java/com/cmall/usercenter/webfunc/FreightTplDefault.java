package com.cmall.usercenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FreightTplDefault extends RootFunc {

	@Override
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		mResult.setResultCode(1);
		
		String tplUid = mDataMap.get("zw_f_uid");
		String is_default = mDataMap.get("zw_f_is_default");
		String isDisable = mDataMap.get("zw_f_isDisable");
		MDataMap dataMap = new MDataMap();
		if("449746250002".equals(is_default) && "449746250002".equals(isDisable)){
			dataMap.put("is_default", "449746250001");
			dataMap.put("isDisable", "449746250002");
			int dataCount = DbUp.upTable("uc_freight_tpl").dataCount("is_default =:is_default and isDisable =:isDisable", dataMap);
			if(dataCount > 0) {
				mResult.setResultCode(959701038);
				mResult.setResultMessage("已存在启用的默认运费模板,不能更改默认!");
				return mResult;
			}
		}else {
			if("449746250002".equals(is_default)) {
				dataMap.put("is_default", "449746250001");
			}else {
				dataMap.put("is_default", "449746250002");
			}
		}
		dataMap.put("uid", tplUid);
		DbUp.upTable("uc_freight_tpl").dataUpdate(dataMap, "is_default", "uid");
		return mResult;
	}

}
