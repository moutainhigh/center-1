package com.cmall.ordercenter.tallyorder;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 为所有符合查询条件的特殊供应商付款
 * @author zht
 *
 */
public class UpdatePayForAllSpecSeller extends RootFunc {

	@Override
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		//变更特殊供应商结算单状态(已结算)
		try {
			String sql = "update oc_bill_merchant_new_spec set flag='4497476900040009' where flag='4497476900040008'";
			int count = DbUp.upTable("oc_bill_merchant_new_spec").dataExec(sql, new MDataMap());
			mResult.setResultMessage("操作成功!更新记录数:" + count);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return mResult;
	}

}
