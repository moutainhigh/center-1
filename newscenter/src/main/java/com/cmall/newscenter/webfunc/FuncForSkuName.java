package com.cmall.newscenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 查询名称 
 * 
 * @author houwen
 * 
 */
public class FuncForSkuName extends RootFunc {
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MWebOperate mOperate = WebUp.upOperate(sOperateUid);

		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		recheckMapField(mResult, mPage, mAddMaps);
		
		String skuId = mDataMap.get("zw_f_skuId");
		
		if (mResult.upFlagTrue()) {
	
		
			MDataMap list = DbUp.upTable("pc_skuinfo").one("sku_code",skuId);
		
			if(list!=null){
				mResult.setResultMessage(list.get("sku_name"));
			}else {
				mResult.setResultMessage(skuId);
			}
			mResult.setResultCode(1);
		}

	
		return mResult;

	}


}
