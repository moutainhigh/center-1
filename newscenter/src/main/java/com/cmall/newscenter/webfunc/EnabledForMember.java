package com.cmall.newscenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 用户解禁
 * 
 * @author yangrong
 * 
 */
public class EnabledForMember extends RootFunc {
	
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();
		
		String tplUid = mDataMap.get("zw_f_uid");
		String member_code = mDataMap.get("zw_f_member_code");
		String status = mDataMap.get("zw_f_status");
		
		if("449746600001".equals(status)){
			
			mResult.setResultCode(934205121);
			mResult.setResultMessage(bInfo(934205121));
			return mResult;
			
		}else{
			mResult = enabledAllTokenByMemberCode(member_code);
			
			if(mResult.getResultCode()==1){
				
				MDataMap whereDataMap  = new MDataMap();
				whereDataMap.put("status", "449746600001");
				whereDataMap.put("uid", tplUid);
				DbUp.upTable("mc_extend_info_star").dataUpdate(whereDataMap, "status", "uid");
			}else{
				
				mResult.setResultCode(934205122);
				mResult.setResultMessage(bInfo(934205122));
				return mResult;
			}
			
		}
		
		return mResult;
	    
	}
	
	
	/**
	 * 启用所有用的登陆token信息
	 * 
	 * @param sMemberCode
	 * @return
	 */
	public MWebResult enabledAllTokenByMemberCode(String sMemberCode) {
		MDataMap mUpDataMap = new MDataMap();
		mUpDataMap.inAllValues("user_code", sMemberCode, "flag_enable", "1");
		DbUp.upTable("za_oauth").dataUpdate(mUpDataMap, "flag_enable",
				"user_code");

		return new MWebResult();
	}
	
}

