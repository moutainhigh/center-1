package com.cmall.groupcenter.txservice;

import com.cmall.groupcenter.service.TraderFoundsChangeLogService;
import com.cmall.groupcenter.service.TraderInfoService;
import com.cmall.groupcenter.service.TraderRebateService;
import com.cmall.membercenter.model.MLoginInput;
import com.cmall.membercenter.support.MemberLoginSupport;
import com.cmall.membercenter.txservice.TxMemberBase;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webmodel.MWebResult;
import org.apache.commons.lang.StringUtils;


/**
 * 创建商户信息时的事务service
 *
 * @author lipengfei
 * @date 2015-6-23
 * email:lipf@ichsy.com
 *
 */
public class TxTraderInfoCreateService extends BaseClass{
	
	
	public void insertTraderInfo(MDataMap traderInfo,MDataMap traderRebate,MDataMap foundsChangeLog) {

		TraderInfoService traderInfoService = new TraderInfoService();
		TraderFoundsChangeLogService changeLogService = new TraderFoundsChangeLogService();
		TraderRebateService traderRebateService = new TraderRebateService();
		
		traderInfoService.addNewTrader(traderInfo);
		traderRebateService.addNewTraderRebate(traderRebate);
		changeLogService.addFoundsChangeLog(foundsChangeLog);
	}


	public MWebResult insertTraderInfo(MDataMap traderInfo,MDataMap traderRebate,MDataMap foundsChangeLog,MDataMap traderPreWithdraw,String loginName) {

		MWebResult result = new MWebResult();


        //如果会公社用户不存在，则此时创建微公社用户
        if(DbUp.upTable("mc_member_info").count(new String[]{"account_code", "AI140701100001","manage_code","SI2011"})==0){
            TxMemberBase txMemberBase = (TxMemberBase) BeansHelper.upBean("bean_com_cmall_membercenter_txservice_TxMemberBase");
            MLoginInput mLoginInput = new MLoginInput();
            mLoginInput.setLoginName(loginName);
            mLoginInput.setManageCode("SI2011");
            mLoginInput.setLoginGroup("4497465100020001");
            result.inOtherResult(txMemberBase.doUserReginster(mLoginInput));
        };

		TraderInfoService traderInfoService = new TraderInfoService();
		TraderFoundsChangeLogService changeLogService = new TraderFoundsChangeLogService();
		TraderRebateService traderRebateService = new TraderRebateService();

		traderInfoService.addNewTrader(traderInfo);
		traderRebateService.addNewTraderRebate(traderRebate);
		changeLogService.addFoundsChangeLog(foundsChangeLog);

//		添加预存款管理设置提醒的数据
		DbUp.upTable("gc_pre_withdraw_notify").dataInsert(traderPreWithdraw);
		
		String reasonDesc=DbUp.upTable("sc_define").one("define_code","4497472500050001").get("define_name");
		//插入状态日志
		DbUp.upTable("gc_trader_status_log").insert("trader_code",traderInfo.get("trader_code"),"trader_status","4497472500010001",
				"update_time",FormatHelper.upDateTime(),"reason_code","4497472500050001","reason_desc",reasonDesc);


		return result;

	}
	
	
	public MWebResult insertTraderInfoMoney(MDataMap traderInfo,MDataMap traderRebate,MDataMap traderRebateChangeLog,MDataMap rebateTypeChangeLog,MDataMap traderPreWithdraw,String loginName) {

		MWebResult result = new MWebResult();


        //如果会公社用户不存在，则此时创建微公社用户
        if(DbUp.upTable("mc_member_info").count(new String[]{"account_code", "AI140701100001","manage_code","SI2011"})==0){
            TxMemberBase txMemberBase = (TxMemberBase) BeansHelper.upBean("bean_com_cmall_membercenter_txservice_TxMemberBase");
            MLoginInput mLoginInput = new MLoginInput();
            mLoginInput.setLoginName(loginName);
            mLoginInput.setManageCode("SI2011");
            mLoginInput.setLoginGroup("4497465100020001");
            result.inOtherResult(txMemberBase.doUserReginster(mLoginInput));
        };

		TraderInfoService traderInfoService = new TraderInfoService();
		TraderRebateService traderRebateService = new TraderRebateService();

		traderInfoService.addNewTrader(traderInfo);
		traderRebateService.addNewTraderRebate(traderRebate);

		//添加返利比例变更日志
		DbUp.upTable("gc_trader_info_change_log").dataInsert(traderRebateChangeLog);
		
		
		//添加返利方式日志
		DbUp.upTable("gc_trader_rebate_type_log").dataInsert(rebateTypeChangeLog);



//		添加预存款管理设置提醒的数据
		DbUp.upTable("gc_pre_withdraw_notify").dataInsert(traderPreWithdraw);
		
		String reasonDesc=DbUp.upTable("sc_define").one("define_code","4497472500050001").get("define_name");
		//插入状态日志
		DbUp.upTable("gc_trader_status_log").insert("trader_code",traderInfo.get("trader_code"),"trader_status","4497472500010001",
				"update_time",FormatHelper.upDateTime(),"reason_code","4497472500050001","reason_desc",reasonDesc);


		return result;

	}





}
