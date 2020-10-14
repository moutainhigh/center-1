package com.cmall.ordercenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FunEditForSkuImgReplaceService  extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		MDataMap map = new MDataMap();
		if (mResult.upFlagTrue()) {
			String uid = mSubMap.get("uid");
			String skuImgReplace = mSubMap.get("sku_img_replace");
			map.put("uid", uid);
			map.put("sku_img_replace", skuImgReplace);
			DbUp.upTable("oc_flashsales_skuInfo").dataUpdate(map, "sku_img_replace","uid");
		}
		return mResult;
	}

}
