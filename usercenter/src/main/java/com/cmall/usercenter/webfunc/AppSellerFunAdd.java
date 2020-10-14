package com.cmall.usercenter.webfunc;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/***
 * 将app信息插入uc_sellerinfo表中
 * @author shiyz
 * Date:2014-07-02
 */
public class AppSellerFunAdd extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		MDataMap map = new MDataMap();
		if (StringUtils.isBlank(mAddMaps.get("app_name"))) {
			mResult.setResultCode(959701013);
			mResult.setResultMessage(bInfo(959701013));
		}
		else if(querySellerByName(mAddMaps.get("app_name").trim()) >0)
		{
			mResult.setResultCode(959701012);
			mResult.setResultMessage(bInfo(959701012));
		}else{
			String app_code = WebHelper.upCode("SP");
			mAddMaps.put("app_code", app_code);
			/**将uc_appinfo表中的值放入uc_sellerinfo表中*/
			map.put("seller_code", mAddMaps.get("app_code"));//App编号
			/**App名称*/
			map.put("seller_name", mAddMaps.get("app_name"));
//			/**App类型*/
//			map.put("seller_type", mAddMaps.get("app_type"));
			map.put("seller_status", "4497172300040004");
			try{
				map.put("editId", UserFactory.INSTANCE.create().getUserCode());
				mAddMaps.put("create_user", UserFactory.INSTANCE.create().getUserCode());
				mAddMaps.put("create_time", DateUtil.getNowTime());
				if (mResult.upFlagTrue()) {
					/**将APP信息插入uc_sellerinfo表中*/
					DbUp.upTable("uc_sellerinfo").dataInsert(map);
					/**将APP信息插入uc_appinfo表中*/
					DbUp.upTable("uc_appinfo").dataInsert(mAddMaps);
				}
			}catch (Exception e) {
				mResult.inErrorMessage(959701033);
			}
		}
		return mResult;
	}
	private int querySellerByName(String seller_name) {
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("seller_name", seller_name);
		int count = DbUp.upTable("uc_sellerinfo").dataCount("seller_name=:seller_name", mWhereMap);
		return count;
	}

}
