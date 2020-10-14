package com.cmall.groupcenter.func.groupapp;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncBusinessNameAdd  extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		MDataMap mWhereMap=new MDataMap();
		
		
		String loginName = UserFactory.INSTANCE.create().getLoginName();
		String businessname=mAddMaps.get("business_name");
		mWhereMap.put("business_name", businessname.trim());
		
		Object value = DbUp.upTable("gc_business_name_config").dataGet("uid", null, mWhereMap);
		boolean hasImg=false;
		if(value==null){
			String remark=mAddMaps.get("remark");
			mWhereMap.put("business_code", WebHelper.upCode("BSN"));
			
			mWhereMap.put("remark", remark);
			mWhereMap.put("create_time", DateUtil.getSysDateTimeString());
			mWhereMap.put("create_name",loginName);
			DbUp.upTable("gc_business_name_config").dataInsert(mWhereMap);
			mResult.setResultCode(1);
			mResult.setResultMessage("添加成功");
		}else {
			mResult.inErrorMessage(918570004);
		}


		
		return mResult;
	}
}
