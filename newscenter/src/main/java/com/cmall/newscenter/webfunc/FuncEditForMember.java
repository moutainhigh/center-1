package com.cmall.newscenter.webfunc;

import com.cmall.membercenter.support.MemberLoginSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 用户禁用
 * 
 * @author yangrong
 * 
 */
public class FuncEditForMember extends RootFunc {
	
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();
		
		String tplUid = mDataMap.get("zw_f_uid");
		String member_code = mDataMap.get("zw_f_member_code");
		String status = mDataMap.get("zw_f_status");
		
		if("449746600002".equals(status)){
			mResult.setResultCode(934205116);
			mResult.setResultMessage("已是禁用状态");
			return mResult;
		}else{
			MemberLoginSupport support = new MemberLoginSupport();
			mResult = support.deleteAllTokenByMemberCode(member_code);
			
			if(mResult.getResultCode()==1){
				
				MDataMap whereDataMap  = new MDataMap();
				whereDataMap.put("status", "449746600002");
				whereDataMap.put("uid", tplUid);
				DbUp.upTable("mc_extend_info_star").dataUpdate(whereDataMap, "status", "uid");
			}else{
				
				mResult.setResultMessage("禁用失败");
				return mResult;
			}
			
		}
		
		return mResult;
	    
	}
	
}
