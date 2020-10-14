package com.cmall.usercenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class SellerDescFunc4cm   extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap _mDataMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		String seller_code = UserFactory.INSTANCE.create().getManageCode();
		_mDataMap.put("seller_code", seller_code);
		try {
			if (mResult.upFlagTrue() == true) {
				int n = DbUp.upTable("uc_seller_desc").dataUpdate(_mDataMap, "seller_desc,seller_log", "seller_code");
				//System.out.println("n :"+n);
				if(n == 0){ 
					DbUp.upTable("uc_seller_desc").dataInsert(_mDataMap);
				}
				mResult.inErrorMessage(939301010);
				
			}
			mResult.setResultMessage(bInfo(939301009));
		} catch (Exception e) {
			mResult.inErrorMessage(939301010);
		}
		return mResult;
	}
	
	
	public String initSeller()
	{
			if(DbUp.upTable("uc_seller_desc").count("seller_code",UserFactory.INSTANCE.create().getManageCode())==0)
			{
				DbUp.upTable("uc_seller_desc").insert("seller_code",UserFactory.INSTANCE.create().getManageCode());
			}
			
			return "";
	
	}
	
	
	
	

}
