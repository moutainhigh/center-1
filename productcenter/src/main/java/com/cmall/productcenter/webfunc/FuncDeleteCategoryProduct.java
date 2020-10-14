package com.cmall.productcenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncDeleteCategoryProduct extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();

		MDataMap mDelMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		try {
			if (mResult.upFlagTrue()) {
				if (mDelMaps.containsKey("uid")&&mDelMaps.containsKey("product_code")&&mDelMaps.containsKey("seller_code")&&mDelMaps.containsKey("category_code")) {
					mDelMaps.remove("uid");
					DbUp.upTable("uc_sellercategory_product_relation").dataDelete("", mDelMaps, "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(909101014);
		}

		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969909001));
		}

		return mResult;
	}

}
