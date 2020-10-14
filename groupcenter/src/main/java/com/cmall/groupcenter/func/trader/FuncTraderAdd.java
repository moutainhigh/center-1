package com.cmall.groupcenter.func.trader;

import java.util.Date;

import com.cmall.groupcenter.service.TraderFoundsChangeLogService;
import com.cmall.groupcenter.service.TraderInfoService;
import com.cmall.groupcenter.service.TraderRebateService;
import com.cmall.groupcenter.txservice.TxTraderInfoCreateService;
import com.cmall.groupcenter.util.CalendarHelper;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncTraderAdd  extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		MDataMap mWhereMap  = new MDataMap();
		
		
		mWhereMap.put("account_code",mAddMaps.get("account_code"));
		mWhereMap.put("login_account", mAddMaps.get("login_account"));
		
		Object value = DbUp.upTable("gc_trader_info").dataGet("uid", null, mWhereMap);
		
		if(value==null){
			
			MDataMap traderInfo = new MDataMap();
			
			//返现范围表
			MDataMap traderRebate = new MDataMap();
			MDataMap foundsChangeLog = new MDataMap();

            MDataMap traderPreWithdraw = new MDataMap();
			
			TxTraderInfoCreateService createService = BeansHelper
					.upBean("bean_com_cmall_groupcenter_txservice_TxTraderInfoCreateService");

			//准备数据
			prepareData(mAddMaps, traderInfo, traderRebate, foundsChangeLog,traderPreWithdraw);
			
			createService.insertTraderInfo(traderInfo, traderRebate, foundsChangeLog,traderPreWithdraw,mAddMaps.get("login_account"));


			
		}else {
			mResult.inErrorMessage(918549001);
		}
		
		
		return mResult;
	}
	
	/**
	 * 准备需要存储的数据
	 * @author lipengfei
	 * @date 2015-6-23
	 * @param mAddMaps
	 */
	private void prepareData(MDataMap mAddMaps,MDataMap traderInfo,MDataMap traderRebate,MDataMap foundsChangeLog,MDataMap traderPreWithdraw){
		//商户创建人
			String createUserCode = UserFactory.INSTANCE.create().getUserCode();
		String traderCode = WebHelper.upCode("SG11");
		String createDate = CalendarHelper.Date2String(new Date(),"yyyy-MM-dd HH:mm:ss");
		
		traderInfo.put("uid", WebHelper.upUuid());
		traderInfo.put("trader_name", mAddMaps.get("trader_name"));
		traderInfo.put("trader_code",traderCode);
		traderInfo.put("account_code",mAddMaps.get("account_code"));
		traderInfo.put("login_account", mAddMaps.get("login_account"));
		traderInfo.put("trader_status","4497472500010001");//默认启用状态
		traderInfo.put("gurrantee_balance", mAddMaps.get("gurrantee_balance"));
		traderInfo.put("create_time",createDate);
		traderInfo.put("create_user",createUserCode);
		traderInfo.put("activate_operation",mAddMaps.get("activate_operation")==null?"":mAddMaps.get("activate_operation"));
		
		traderRebate.put("trader_code", traderCode);
		traderRebate.put("account_code", mAddMaps.get("account_code"));
		traderRebate.put("rebate_range", mAddMaps.get("REBATE_RANGE"));
		traderRebate.put("rebate_rate", mAddMaps.get("REBATE_RATE"));
		traderRebate.put("create_user", createUserCode);
		traderRebate.put("create_time",createDate);
		traderRebate.put("delete_flag","0");
		traderRebate.put("return_goods_day","30");//默认30天的服务退货时间。


//        预存款管理提醒的默认设置
        traderPreWithdraw.put("trader_code",traderCode);
        traderPreWithdraw.put("first_notify","10");
        traderPreWithdraw.put("second_notify","5");
        traderPreWithdraw.put("stop_rebate_notify","2");
        traderPreWithdraw.put("create_time",createDate);

		foundsChangeLog.put("uid", WebHelper.upUuid());
		foundsChangeLog.put("trader_code", traderCode);
		foundsChangeLog.put("account_code", mAddMaps.get("account_code"));
		foundsChangeLog.put("gurrantee_balance_before","0");
		foundsChangeLog.put("gurrantee_change_amount", mAddMaps.get("gurrantee_balance"));
		foundsChangeLog.put("gurrantee_balance_after", traderInfo.get("gurrantee_balance"));
		foundsChangeLog.put("create_user",createUserCode);
		foundsChangeLog.put("create_time",createDate);
		foundsChangeLog.put("REMARK","创建商户时添加的充值保证金");
		//充值类型
		foundsChangeLog.put("CHANGE_TYPE","4497472500030002");
		
	}

}
