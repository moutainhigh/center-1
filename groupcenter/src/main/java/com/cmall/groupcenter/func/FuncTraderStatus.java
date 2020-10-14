package com.cmall.groupcenter.func;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.cmall.groupcenter.txservice.TxTraderFoundsService;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;


/**
 * 
 * 模块:微信公众号--精选特惠
 * 功能:精选特惠的发布与不发布
 * @author lipengfei
 * @date 2015-5-19
 * email:lipf@ichsy.com
 *
 */
public class FuncTraderStatus extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mWebResult = new MWebResult();
		String status = mDataMap.get("zw_f_trader_status");
		String uid = mDataMap.get("zw_f_uid");
		MDataMap whereDataMap  = new MDataMap();
		
		if("4497472500010001".equals(status)) {//当前为启用状态， 修改为停用
			whereDataMap.put("trader_status", "4497472500010002");
			whereDataMap.put("uid", uid);
			DbUp.upTable("gc_trader_info").dataUpdate(whereDataMap, null, "uid");
			MDataMap traderInfo=DbUp.upTable("gc_trader_info").one("uid",uid);
			String reasonDesc=DbUp.upTable("sc_define").one("define_code","4497472500050004").get("define_name");
			//插入状态日志
			DbUp.upTable("gc_trader_status_log").insert("trader_code",traderInfo.get("trader_code"),"trader_status","4497472500010002",
					"update_time",FormatHelper.upDateTime(),"reason_code","4497472500050004","reason_desc",reasonDesc);

	
	} else if("4497472500010002".equals(status)) {
		
		//启用判断保证金是否足够
		MDataMap traderInfo=DbUp.upTable("gc_trader_info").one("uid",uid);
		//商户预存款余额
		BigDecimal gurranteeBalance=new BigDecimal(traderInfo.get("gurrantee_balance"));
		
		MDataMap pre=DbUp.upTable("gc_pre_withdraw_notify").one("trader_code",traderInfo.get("trader_code"));
		//停止返利金额
		BigDecimal stopMoney=new BigDecimal(pre.get("sec_unenough_bal_notify"));
		if(pre!=null){
			TxTraderFoundsService service =new TxTraderFoundsService();
			boolean is_stop=service.validateGurrantee(traderInfo,Integer.parseInt(pre.get("stop_rebate_notify")));
			if(!is_stop&&gurranteeBalance.compareTo(stopMoney)==1){
				//重置提醒状态
				pre.put("second_notify_flag", "0");
				pre.put("first_notify_flag", "0");
				pre.put("stop_rebate_notify_flag", "0");
				
				pre.put("sec_unenough_bal_notify_flag", "0");
				pre.put("first_unenough_bal_flag", "0");
				DbUp.upTable("gc_pre_withdraw_notify").update(pre);
				
				traderInfo.put("trader_status", "4497472500010001");
				String reasonDesc=DbUp.upTable("sc_define").one("define_code","4497472500050003").get("define_name");
				//插入状态日志
				DbUp.upTable("gc_trader_status_log").insert("trader_code",traderInfo.get("trader_code"),"trader_status","4497472500010001",
						"update_time",FormatHelper.upDateTime(),"reason_code","4497472500050003","reason_desc",reasonDesc);
				DbUp.upTable("gc_trader_info").update(traderInfo);
			}else{
				mWebResult.setResultMessage("预存款余额不足请充值！");
			}
		}else{
			mWebResult.setResultMessage("预存款提醒服务未设置");
		}
		
//			whereDataMap.put("trader_status", "4497472500010001");
//			
//			whereDataMap.put("uid", uid);
//			
//			DbUp.upTable("gc_trader_info").dataUpdate(whereDataMap,null, "uid");
			
	}
		
		return mWebResult;
	}

}
