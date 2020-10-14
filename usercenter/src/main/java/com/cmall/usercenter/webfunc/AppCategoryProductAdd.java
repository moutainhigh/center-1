package com.cmall.usercenter.webfunc;

import java.util.List;
import java.util.Map;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * ClassName:虚类私有类目新增与商品的关系 <br/>
 * Date:     2014-07-03
 * @author   shiyz
 * @version  1.0
 */
public class AppCategoryProductAdd extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		// 定义插入数据库
		MDataMap mInsertMap = new MDataMap();
		mInsertMap.put("category_code", mAddMaps.get("category_code"));
		mInsertMap.put("product_code", mAddMaps.get("product_code"));
		mInsertMap.put("seller_code", mAddMaps.get("app_code"));
		if (mResult.upFlagTrue()) {
			List<Map<String, Object>> list =  DbUp.upTable("uc_sellercategory_product_relation").dataQuery("", "", "", mInsertMap, 0, 0);
			if(!list.isEmpty()&&list.size()>0){
				mResult.inErrorMessage(941901024);
			}else{
				DbUp.upTable("uc_sellercategory_product_relation").dataInsert(mInsertMap);
			}
		}

		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969909001));
		}
		return mResult;
	}
}

