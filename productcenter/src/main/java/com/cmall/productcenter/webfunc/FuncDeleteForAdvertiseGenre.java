package com.cmall.productcenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 删除广告类型
 * @author yangrong
 *
 */
public class FuncDeleteForAdvertiseGenre extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		if (mResult.upFlagTrue()){
			try{
				//广告类型的uid
				String uid=mSubMap.get("uid");
				
				//根据uid查出advertise_code
				String advertise_code = getBoutidByUid(uid);
				
				//根据advertise_code校验在中间表中是否有数据存在
				if(checkInfo(advertise_code)){
					//返回提示信息
					mResult.setResultCode(941901076);
					mResult.setResultMessage(bInfo(941901076));
					return mResult;
					
				}else{
					deleteAdvertiseGenre(uid);
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
	 * 根据uid删除广告类型
	 * @param uid
	 */
	private void deleteAdvertiseGenre(String uid) {
		DbUp.upTable("nc_advertise_genre").delete("uid", uid);
	}

	/**
	 * 校验在中间表中是否有数据存在
	 * @param advertise_code
	 * @return
	 */
	private boolean checkInfo(String advertise_code) {
		int atCount = DbUp.upTable("nc_advertise").count("genre_code", advertise_code);
		if(atCount >= 1){
			return true;
		}
		return false;
	}

	
	/**
	 * 根据uid查出advertise_code
	 * @param uid  广告类型UID
	 * @return
	 */
	public String  getBoutidByUid(String uid)
	{
		MDataMap AdvertiseGenreData = DbUp.upTable("nc_advertise_genre").one("uid", uid);
		return AdvertiseGenreData.get("advertise_code");
	}
}
