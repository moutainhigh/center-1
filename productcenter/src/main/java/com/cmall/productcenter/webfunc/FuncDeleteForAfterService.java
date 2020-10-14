package com.cmall.productcenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 删除售后服务
 * @author yangrong
 *
 */
public class FuncDeleteForAfterService extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		if (mResult.upFlagTrue()){
			try{
				//售后服务的uid
				String uid=mSubMap.get("uid");
				
				//根据uid查出service_code
				String service_code = getBoutidByUid(uid);
				
				//根据service_code校验在中间表中是否有数据存在
				if(checkInfo(service_code)){
					//返回提示信息
					mResult.setResultCode(941901072);
					mResult.setResultMessage(bInfo(941901072));
					return mResult;
					
				}else{
					deleteAfterService(uid);
				}
			}catch(Exception e){
				e.printStackTrace();
				mResult.setResultCode(949701042);
				mResult.setResultMessage(bInfo(949701042));
				return mResult;
			}
		}
		return mResult;
	}

	/**
	 * 根据uid删除售后服务
	 * @param uid
	 */
	private void deleteAfterService(String uid) {
		DbUp.upTable("pc_after_service").delete("uid", uid);
	}

	/**
	 * 校验在中间表中是否有数据存在
	 * @param service_code
	 * @return
	 */
	private boolean checkInfo(String service_code) {
		int atCount = DbUp.upTable("pc_service_product").count("service_code", service_code);
		if(atCount >= 1){
			return true;
		}
		return false;
	}

	
	/**
	 * 根据uid查出service_code
	 * @param uid  售后服务UID
	 * @return
	 */
	public String  getBoutidByUid(String uid)
	{
		MDataMap AfterServiceData = DbUp.upTable("pc_after_service").one("uid", uid);
		return AfterServiceData.get("service_code");
	}
}
