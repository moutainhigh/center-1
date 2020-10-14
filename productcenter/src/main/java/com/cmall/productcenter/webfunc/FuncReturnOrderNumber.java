package com.cmall.productcenter.webfunc;

import java.util.HashMap;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 返回前台订单业务数字
 * @author 张圣瑞
 *
 */
public class FuncReturnOrderNumber extends RootFunc {

	@Override
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		String manageCode = UserFactory.INSTANCE.create().getManageCode();
		MDataMap dataMap = new MDataMap();
		dataMap.put("small_seller_code", manageCode);
		dataMap.put("status", "4497153900020005");
		int dataCounthuan = DbUp.upTable("oc_exchange_goods").dataCount("status =:status and small_seller_code =:small_seller_code", dataMap);
		dataMap.put("status", "4497153900050004");
		int dataCounttui = DbUp.upTable("oc_return_goods").dataCount("status =:status and small_seller_code =:small_seller_code", dataMap);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("exchange",dataCounthuan);
		map.put("return",dataCounttui);
		map.put("total",dataCounthuan + dataCounttui);
		MWebResult mWebResult = new MWebResult();
		mWebResult.setResultObject(map);
		return mWebResult;
	}
}
