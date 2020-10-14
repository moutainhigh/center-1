package com.cmall.groupcenter.func.trader;

import java.math.BigDecimal;
import java.util.Date;

import com.cmall.groupcenter.service.TraderFoundsChangeLogService;
import com.cmall.groupcenter.txservice.TxTraderFoundsService;
import com.cmall.groupcenter.util.CalendarHelper;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 修改充值保证金
 * <p>如果为整数，则代表添加保证金金额，如果为负数则代表减少保证金金额
 * @author lipengfei
 * @date 2015-6-24
 * email:lipf@ichsy.com
 *
 */
public class FuncTraderEditDeposit  extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		//System.out.println(mAddMaps);
		
		String lockKey = WebHelper.addLock(60, mAddMaps.get("trader_code"));
		
		//当前修改人
		String createUserCode = UserFactory.INSTANCE.create().getUserCode();
//		String createDate = CalendarHelper.Date2String(new Date(),"yyyy-MM-dd HH:mm:ss");
		try {
			
			MDataMap mWhereMap  = new MDataMap();
			
			mWhereMap.put("uid",mAddMaps.get("uid"));

			TxTraderFoundsService service = BeansHelper
					.upBean("bean_com_cmall_groupcenter_txservice_TxTraderFoundsService");

			//存钱的事务的操作
			service.doWithdraw(mAddMaps.get("uid"),mAddMaps.get("gurrantee_balance"),createUserCode);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			mResult.setResultCode(0);
			mResult.setResultMessage("出错了");
			
		}finally{
			WebHelper.unLock(lockKey);//解锁
			
		}
		
		
		
		return mResult;
	}

}
