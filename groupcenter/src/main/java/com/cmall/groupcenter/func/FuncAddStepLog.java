package com.cmall.groupcenter.func;

import java.math.BigDecimal;

import com.cmall.dborm.txmodel.groupcenter.GcActiveLog;
import com.cmall.groupcenter.model.ReckonStep;
import com.cmall.groupcenter.support.GroupReckonSupport;
import com.cmall.groupcenter.txservice.TxReckonOrderService;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 添加流程步骤
 * 
 * @author srnpr
 * 
 */
public class FuncAddStepLog extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mWebResult = new MWebResult();

		MDataMap mInputMap = upFieldMap(mDataMap);

		MDataMap mOrderMap = DbUp.upTable("gc_reckon_order_info").one(
				"order_code", mInputMap.get("order_code"));

		if (mWebResult.upFlagTrue()) {
			if (mOrderMap == null) {
				mWebResult.inErrorMessage(918505132,
						mInputMap.get("order_code"));
			}
		}

		if (mWebResult.upFlagTrue()) {

			GroupReckonSupport groupReckonSupport = new GroupReckonSupport();

			ReckonStep reckonStep = new ReckonStep();

			reckonStep.setOrderCode(mOrderMap.get("order_code"));
			reckonStep.setAccountCode(mOrderMap.get("account_code"));
			reckonStep.setExecType(mInputMap.get("exec_type"));
			reckonStep.setRemark(UserFactory.INSTANCE.create().getUserCode()
					+ WebConst.CONST_SPLIT_COMMA + mInputMap.get("remark"));

			mWebResult.inOtherResult(groupReckonSupport
					.createReckonStep(reckonStep));

		}

		return mWebResult;
	}

}
