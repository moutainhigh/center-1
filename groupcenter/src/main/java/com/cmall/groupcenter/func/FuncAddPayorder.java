package com.cmall.groupcenter.func;

import com.cmall.dborm.txmapper.groupcenter.GcMemberBankMapper;
import com.cmall.dborm.txmodel.groupcenter.GcMemberBank;
import com.cmall.dborm.txmodel.groupcenter.GcMemberBankExample;
import com.cmall.groupcenter.model.api.WithdrawApiResult;
import com.cmall.groupcenter.txservice.TxWithdrawCashService;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 提现测试调用
 * @author chenbin@ichsy.com
 *
 */
public class FuncAddPayorder extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mWebResult = new MWebResult();

		MDataMap mInputMap = upFieldMap(mDataMap);

		// 调用提现创建
		if (mWebResult.upFlagTrue()) {
			GcMemberBankMapper gcMemberBankMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcMemberBankMapper");
			GcMemberBankExample gcMemberBankExample=new GcMemberBankExample();
			gcMemberBankExample.createCriteria().andAccountCodeEqualTo(mInputMap.get("account_code")).andFlagEnableEqualTo(1);
			GcMemberBank gcMemberBank=gcMemberBankMapper.selectByExample(gcMemberBankExample).get(0);
			TxWithdrawCashService txWithdrawCashService = BeansHelper
					.upBean("bean_com_cmall_groupcenter_txservice_TxWithdrawCashService");

			mWebResult.inOtherResult(txWithdrawCashService.doWithdrawCash(mInputMap.get("member_code"),
					mInputMap.get("account_code"),
					mInputMap.get("withdraw_money"),gcMemberBank.getBankCode()));

		}

		return mWebResult;
	}

}
