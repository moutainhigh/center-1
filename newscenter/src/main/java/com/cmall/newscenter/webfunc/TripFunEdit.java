package com.cmall.newscenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 修改行程头像
 * @author shiyz	
 * date 2014-7-28
 * @version 1.0
 */
public class TripFunEdit extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		
		/*所属分类*/
		String category_code = mAddMaps.get("category_code").replace("'", "").trim();
		
		MDataMap dataMap = DbUp.upTable("nc_category").one("category_code",category_code);
		
		dataMap.put("line_head", mAddMaps.get("line_head"));
		
		try{
			if (mResult.upFlagTrue()) {
				/**将行程头像更新到表中*/
				DbUp.upTable("nc_category").update(dataMap);
			}
		}catch (Exception e) {
			mResult.inErrorMessage(959701033);
		}
	
	return mResult;
	}
}
