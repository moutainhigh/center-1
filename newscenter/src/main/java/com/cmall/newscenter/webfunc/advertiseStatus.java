package com.cmall.newscenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 对广告管理上下线状态进行修改
 * @author houwen
 *
 */
public class advertiseStatus extends RootFunc {

	
	private static String TABLE_TPL="nc_advertise"; //运费模板
	/**
	 * 
	 *  (non-Javadoc)
	 * @see com.srnpr.zapweb.webface.IWebFunc#funcDo(java.lang.String, com.srnpr.zapcom.basemodel.MDataMap)
	 */
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		// TODO Auto-generated method stub
		MWebResult mResult = new MWebResult();
		mResult.setResultCode(1);
		
		String tplUid = mDataMap.get("zw_f_uid");
		MDataMap dataMap = new MDataMap();
		dataMap.put("uid", tplUid);
		//上线状态 ：449746690001 ；下线状态：449746690002
		String isDisable = mDataMap.get("zw_f_isDisable");
		if("449746690001".equals(isDisable)){  //
			
				dataMap.put("status", "449746690002");
				DbUp.upTable(TABLE_TPL).dataUpdate(dataMap, "status", "uid");
				
		}else if("449746690002".equals(isDisable)){
			
			dataMap.put("status", "449746690001");
			DbUp.upTable(TABLE_TPL).dataUpdate(dataMap, "status", "uid");
			
		}else{
			mResult.setResultCode(934205104);
			mResult.setResultMessage("上下线状态为空，不能修改！");
		}
		

		return mResult;
	}

}
