package com.cmall.newscenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 修改内容是否已推送首页
 * @author shiyz
 * date 2014-8-12
 * @version 1.0
 */
public class ContentDelivery extends RootFunc {

	
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		// TODO Auto-generated method stub
		MWebResult mResult = new MWebResult();
		mResult.setResultCode(1);
		
		String tplZid = mDataMap.get("zw_f_zid");
		
		MDataMap dataMap = new MDataMap();
		
		dataMap.put("zid", tplZid);
		
		String show_type = mDataMap.get("zw_f_show_type");
		
		
		if("4497464900010001".equals(show_type)){  //未推送
			//推送
			dataMap.put("show_type", "4497464900010002");
			 
			 DbUp.upTable("nc_info").dataUpdate(dataMap, "show_type", "zid");
			

	}else{
		//未推送
		dataMap.put("show_type", "4497464900010001");
		 
		 DbUp.upTable("nc_info").dataUpdate(dataMap, "show_type", "zid");
		
	}
		return mResult;
		
	}
}
