package com.cmall.productcenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 删除广告位
 * @author 李国杰
 *
 */
public class FuncDeleteForAdvPlace extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		if (mResult.upFlagTrue()){
			try{
				//广告位的uid
				String uid=mSubMap.get("uid");
				
				//检验广告位是否被占用
				if(checkInfo(uid)){
					//返回提示信息
					mResult.setResultCode(941901079);
					mResult.setResultMessage(bInfo(941901079));
					return mResult;
					
				}else{
					deleteAdvPlace(uid);
				}
			}catch(Exception e){
				e.printStackTrace();
				mResult.setResultCode(941901078);
				mResult.setResultMessage(bInfo(941901078,"删除广告位发生错误！"));
				return mResult;
			}
		}
		return mResult;
	}

	/**
	 * 根据uid删除广告位
	 * @param uid
	 */
	private void deleteAdvPlace(String uid) {
		DbUp.upTable("nc_advertise_place").delete("uid", uid);
	}

	/**
	 * 广告位是否被占用
	 * @param uid
	 * @return
	 */
	private boolean checkInfo(String uid) {
		MDataMap advPlaceData = DbUp.upTable("nc_advertise_place").one("uid", uid);
		//广告位下没有广告时可以删除。
		int atCount = DbUp.upTable("nc_advertise").count("place_code", advPlaceData.get("place_code"));
		if (atCount > 0 )  return true;
		
		return false;
	}
}
