package com.cmall.groupcenter.func.trader;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.util.CalendarHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 修改返利信息
 *
 * @author lipengfei
 * @date 2015-6-24
 * email:lipf@ichsy.com
 *
 */
public class FuncTraderEditRebateMoney  extends RootFunc{

	@Override
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		//商户创建人
		String updateUserCode = UserFactory.INSTANCE.create().getUserCode();

		String updateDate = CalendarHelper.Date2String(new Date(), "yyyy-MM-dd HH:mm:ss");

		mAddMaps.put("update_time", updateDate);
		mAddMaps.put("update_user", updateUserCode);

//		将返现范围等数据组合起来
		String   moneyRangeString = FuncTraderAddMoney.consistMoneyRebateRange(mAddMaps);
		mAddMaps.put("money_rebate_grade",moneyRangeString);


		String rebateRate  = mAddMaps.get("rebate_rate");
		char a = ',';
		if (rebateRate.charAt(rebateRate.length()-1)==a){
			rebateRate = rebateRate.substring(0,rebateRate.length()-1);
		}

		mAddMaps.put("money_rebate_scale",rebateRate);
		
		//查询当前返利比例
		MDataMap traderRebate=DbUp.upTable("gc_trader_rebate").one("uid",mAddMaps.get("uid"));
		//插入返利比例变更日志
		MDataMap traderRebateChangeLog=new MDataMap();
		if(StringUtils.isNotEmpty(traderRebate.get("money_rebate_scale"))){
			traderRebateChangeLog.put("before_rebate_grade", traderRebate.get("money_rebate_grade"));
			traderRebateChangeLog.put("before_rebate_range", traderRebate.get("money_rebate_range"));
			traderRebateChangeLog.put("before_rebate_scale", traderRebate.get("money_rebate_scale"));
			traderRebateChangeLog.put("remark", "商户修改返利比例");
		}else{
			traderRebateChangeLog.put("remark", "历史商户初始化返利比例");
		}
		traderRebateChangeLog.put("log_code", WebHelper.upCode("TRCL"));
		traderRebateChangeLog.put("trader_code", mAddMaps.get("trader_code"));
		traderRebateChangeLog.put("now_rebate_grade", moneyRangeString);
		traderRebateChangeLog.put("now_rebate_range", mAddMaps.get("money_rebate_range"));
		traderRebateChangeLog.put("now_rebate_scale", rebateRate);
		traderRebateChangeLog.put("now_rebate_mode", "4497472500080002");
		traderRebateChangeLog.put("create_user", updateUserCode);
		traderRebateChangeLog.put("create_time", updateDate);
		
		DbUp.upTable("gc_trader_info_change_log").dataInsert(traderRebateChangeLog);
				
		DbUp.upTable("gc_trader_rebate").dataUpdate(mAddMaps, "money_rebate_scale,money_rebate_grade,money_rebate_range,update_time,update_user,return_goods_day", "uid");

		if (StringUtils.isNotEmpty(mAddMaps.get("trader_code"))){

			if (StringUtils.isEmpty(mAddMaps.get("activate_operation"))){
				mAddMaps.put("activate_operation","");
			}
			
			//判断返利方式是否为空
			MDataMap traderInfo=DbUp.upTable("gc_trader_info").one("trader_code",mAddMaps.get("trader_code"));
			if(StringUtils.isEmpty(traderInfo.get("rebate_type"))){
				mAddMaps.put("rebate_type", "4497472500080002");
				mAddMaps.put("type_apply_time", updateDate);
				DbUp.upTable("gc_trader_info").dataUpdate(mAddMaps,"activate_operation,rebate_type,type_apply_time","trader_code");
				//插入返利方式变更日志
				MDataMap rebateTypeChangeLog=new MDataMap();
				rebateTypeChangeLog.put("trader_code", mAddMaps.get("trader_code"));
				rebateTypeChangeLog.put("rebate_mode", "4497472500080002");
				rebateTypeChangeLog.put("create_time", updateDate);
				rebateTypeChangeLog.put("remark", "历史商户初始化返利比例");
				DbUp.upTable("gc_trader_rebate_type_log").dataInsert(rebateTypeChangeLog);
			}else{
				DbUp.upTable("gc_trader_info").dataUpdate(mAddMaps,"activate_operation","trader_code");
			}
		}

		return mResult;
	}

}
