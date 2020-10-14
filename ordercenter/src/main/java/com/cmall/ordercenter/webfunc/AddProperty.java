package com.cmall.ordercenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class AddProperty extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		String  productId= mDataMap.get("zw_f_uid");  //  property_code
		String uuid = mDataMap.get("uuid");           //  category_code     
		MDataMap mp = new MDataMap();
		mp.put("property_code", productId);
		mp.put("category_code", uuid);
		if(validateData(productId, uuid))
		{
			mResult.setResultCode(939301075);
			mResult.setResultMessage(bInfo(939301075));
			return mResult;
		}
		try {
			DbUp.upTable("pc_categoryproperty_rel").dataInsert(mp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			mResult.setResultCode(939301076);
			mResult.setResultMessage(bInfo(939301076));
			return mResult;
		}
		return mResult;
	}
	
	/**
	 *检查数据是否用重复
	 * @param uid
	 * @return
	 */
	private boolean validateData(String productId,String uuid)
	{
	   MDataMap mp =DbUp.upTable("pc_categoryproperty_rel").one("property_code",productId,"category_code",uuid);
	   if(null == mp)
		   return false ;
	   else
		   return true;
	}
}
