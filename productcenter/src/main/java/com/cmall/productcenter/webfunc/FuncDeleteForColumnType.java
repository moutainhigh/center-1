package com.cmall.productcenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 删除栏目类型
 * @author 李国杰
 *
 */
public class FuncDeleteForColumnType extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		if (mResult.upFlagTrue()){
			try{
				//栏目类型的uid
				String uid=mSubMap.get("uid");
				
				//根据uid查出column_code
				String column_code = getBoutidByUid(uid);
				
				//根据column_code校验在中间表中是否有数据存在
				if(checkInfo(column_code)){
					//返回提示信息
					mResult.setResultCode(941901075);
					mResult.setResultMessage(bInfo(941901075));
					return mResult;
					
				}else{
					deleteColumnType(uid);
				}
			}catch(Exception e){
				e.printStackTrace();
				mResult.setResultCode(941901074);
				mResult.setResultMessage(bInfo(941901074));
				return mResult;
			}
		}
		return mResult;
	}

	/**
	 * 根据uid删除栏目类型
	 * @param uid
	 */
	private void deleteColumnType(String uid) {
		DbUp.upTable("nc_column_type").delete("uid", uid);
	}

	/**
	 * 校验在中间表中是否有数据存在
	 * @param column_code
	 * @return
	 */
	private boolean checkInfo(String column_code) {
		int atCount = DbUp.upTable("nc_app_column").count("column_type_code", column_code);
		if(atCount >= 1){
			return true;
		}
		return false;
	}

	
	/**
	 * 根据uid查出column_code
	 * @param uid  栏目类型UID
	 * @return
	 */
	public String  getBoutidByUid(String uid)
	{
		MDataMap AdvertiseGenreData = DbUp.upTable("nc_column_type").one("uid", uid);
		return AdvertiseGenreData.get("column_code");
	}
}
