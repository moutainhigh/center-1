package com.cmall.groupcenter.func.groupapp;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncBusinessEmailEdit  extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		MDataMap mWhereMap=new MDataMap();
		String loginName = UserFactory.INSTANCE.create().getLoginName();
		String uid = mAddMaps.get("uid");
		mWhereMap.put("uid", uid);
		
		String remark=mAddMaps.get("remark");
		String businesscode=mAddMaps.get("business_code");
		String email=mAddMaps.get("email").trim();
		if(email==null||email.equals("")){
			mResult.inErrorMessage(918570006);
			return mResult;
		}
		Object value = DbUp.upTable("gc_business_email_config").dataGet("uid", "uid!='" + uid +"' and business_code='"+businesscode+"' and email='"+email+"'",  mWhereMap);
	    if(value==null){
			
			mWhereMap.put("business_code", businesscode);
			mWhereMap.put("email", email);
			mWhereMap.put("recipients_name", mAddMaps.get("recipients_name").trim());
			mWhereMap.put("remark", remark);
			mWhereMap.put("update_time", DateUtil.getSysDateTimeString());
			mWhereMap.put("update_name",loginName);
			DbUp.upTable("gc_business_email_config").dataUpdate(mWhereMap,"","uid");
			mResult.setResultCode(1);
			mResult.setResultMessage("修改成功");
	    }else{
	    	mResult.inErrorMessage(918570005);
	    }
		
		return mResult;
	}
}
