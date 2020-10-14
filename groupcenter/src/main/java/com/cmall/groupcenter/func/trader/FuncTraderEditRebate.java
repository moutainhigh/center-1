package com.cmall.groupcenter.func.trader;

import com.cmall.groupcenter.util.CalendarHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
import org.apache.commons.lang.StringUtils;

import java.util.Date;

/**
 * 修改返利信息
 *
 * @author lipengfei
 * @date 2015-6-24
 * email:lipf@ichsy.com
 *
 */
public class FuncTraderEditRebate  extends RootFunc{

	@Override
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		//商户创建人
		String updateUserCode = UserFactory.INSTANCE.create().getUserCode();

		String updateDate = CalendarHelper.Date2String(new Date(), "yyyy-MM-dd HH:mm:ss");

		mAddMaps.put("update_time", updateDate);
		mAddMaps.put("update_user", updateUserCode);

		//System.out.println(mAddMaps);
		DbUp.upTable("gc_trader_rebate").dataUpdate(mAddMaps, "rebate_rate,rebate_range,update_time,update_user,return_goods_day", "uid");

		if (StringUtils.isNotEmpty(mAddMaps.get("trader_code"))){

			if (StringUtils.isEmpty(mAddMaps.get("activate_operation"))){
				mAddMaps.put("activate_operation","");
			}

			DbUp.upTable("gc_trader_info").dataUpdate(mAddMaps,"trader_name,activate_operation","trader_code");
		}

		return mResult;
	}

}
