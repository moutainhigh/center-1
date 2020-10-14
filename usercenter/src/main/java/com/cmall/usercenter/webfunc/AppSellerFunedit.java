package com.cmall.usercenter.webfunc;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/***
 * 修改APP信息
 * @author shiyz
 * date： 2014-07-02
 */
public class AppSellerFunedit extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		MDataMap apMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		MDataMap map = new MDataMap();
		MDataMap oneData = DbUp.upTable("uc_appinfo").one("uid",apMap.get("uid"));
		/**将uc_appinfo表中的值放入uc_sellerinfo表中*/
		map.put("seller_code", oneData.get("app_code"));//App编号
		/**App名称*/
		map.put("seller_name", apMap.get("app_name"));
		try{
			if (StringUtils.isBlank(apMap.get("app_name"))) {
				mResult.setResultCode(959701013);
				mResult.setResultMessage(bInfo(959701013));
			}
			else if(querySellerByName(apMap.get("app_name").trim()) >0)
			{
				mResult.setResultCode(959701012);
				mResult.setResultMessage(bInfo(959701012));
			}else{
				apMap.put("modify_user", UserFactory.INSTANCE.create().getUserCode());
				apMap.put("modify_time", DateUtil.getNowTime());
				if (mResult.upFlagTrue()) {
					/**修改uc_sellerinfo表中APP数据*/
					DbUp.upTable("uc_sellerinfo").dataUpdate(map, "", "seller_code");
					/**修改uc_appinfo表中APP数据*/
					DbUp.upTable("uc_appinfo").dataUpdate(apMap, "", "uid");
				}
			}
		}catch (Exception e) {
			mResult.inErrorMessage(959701034);
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
