package com.cmall.groupcenter.func;

import java.math.BigDecimal;

import com.cmall.dborm.txmodel.groupcenter.GcActiveLog;
import com.cmall.groupcenter.txservice.TxReckonOrderService;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
import com.srnpr.zapweb.websupport.UserSupport;

/**
 * 添加激活日志
 * 
 * @author srnpr
 * 
 */
public class FuncAddAcviteLog extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mWebResult = new MWebResult();

		MDataMap mInputMap = upFieldMap(mDataMap);

		if (mWebResult.upFlagTrue()) {
			TxReckonOrderService txReckonOrderService = BeansHelper
					.upBean("bean_com_cmall_groupcenter_txservice_TxReckonOrderService");
			GcActiveLog gcActiveLog = new GcActiveLog();

			gcActiveLog.setAccountCode(mInputMap.get("account_code"));
			gcActiveLog.setActiveTime(mInputMap.get("active_time"));
			gcActiveLog.setConsumeMoney(new BigDecimal(mInputMap
					.get("consume_money")));
			gcActiveLog.setRemark(UserFactory.INSTANCE.create().getUserCode()
					+ WebConst.CONST_SPLIT_COMMA + mInputMap.get("remark"));

			int iRelativeMembers = Integer.valueOf(mInputMap
					.get("v_add_number"));
			txReckonOrderService.updateActiveCount(gcActiveLog,
					iRelativeMembers);
		}

		return mWebResult;

	}

}
