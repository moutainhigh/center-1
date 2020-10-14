package com.cmall.groupcenter.func.groupapp;


import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebField;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncWithdrawConfigEdit extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MWebOperate mOperate = WebUp.upOperate(sOperateUid);

		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		// 定义插入数据库
		MDataMap mInsertMap = new MDataMap();
		
		// 定义组件判断标记
		recheckMapField(mResult, mPage, mAddMaps);
		
		if (mResult.upFlagTrue()) {
			//要插入的字段
			String loginName = UserFactory.INSTANCE.create().getLoginName();
			String withdrawSource = mAddMaps.get("withdraw_source");
			String minimumWithdrawMoney = mAddMaps.get("minimum_withdraw_money").trim();
			
			String maximumMoneyRange = mAddMaps.get("maximum_money_range").trim();
			String feeMoney = mAddMaps.get("fee_money").trim();
			String uid = mAddMaps.get("uid");
			
			if(StringUtils.isEmpty(UserFactory.INSTANCE.create().getLoginName())){
				mResult.inErrorMessage(918570016);
			}
			if(!StringUtils.isNumeric(minimumWithdrawMoney)){
				mResult.inErrorMessage(918570017);
			}
			if(!StringUtils.isNumeric(maximumMoneyRange)){
				mResult.inErrorMessage(918570018);
			}
			if(!StringUtils.isNumeric(feeMoney)){
				mResult.inErrorMessage(918570019);
			}
			Object value = DbUp.upTable("gc_withdraw_config").dataGet("uid"," uid<>:uid and withdraw_source=:withdraw_source and flag_status= 1 ",
					new MDataMap("withdraw_source", withdrawSource,"uid",uid));
			if (value == null) {
				mInsertMap.put("withdraw_source", withdrawSource);
				mInsertMap.put("minimum_withdraw_money", minimumWithdrawMoney);
				
				mInsertMap.put("maximum_money_range", maximumMoneyRange);
				mInsertMap.put("fee_money", feeMoney);
				mInsertMap.put("update_time", FormatHelper.upDateTime());
				mInsertMap.put("update_name", loginName);
				mInsertMap.put("uid", uid);
				DbUp.upTable("gc_withdraw_config").dataUpdate(mInsertMap,"","uid");
				mResult.setResultCode(1);
				mResult.setResultMessage("修改成功");
			} else {
				mResult.inErrorMessage(918570015);
			}
		}

		return mResult;
	}
	
}
