package com.cmall.ordercenter.webfunc;

import com.cmall.ordercenter.tallyorder.settleperiod.OcAccountPeriodService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebField;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 结算周期删除
 * @author pang_jhui
 *
 */
public class OcAccountPeriodDelete extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();

		MWebOperate mOperate = WebUp.upOperate(sOperateUid);

		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

		MDataMap mDelMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		if (mResult.upFlagTrue()) {
			if (mDelMaps.containsKey("uid")) {
				
				
				MDataMap mThisMap=null;
				

				// 循环所有结构
				for (MWebField mField : mPage.getPageFields()) {

					if (mField.getFieldTypeAid().equals("104005003")) {
						
						if(mThisMap==null)
						{
							mThisMap=DbUp.upTable(mPage.getPageTable()).one("uid",mDelMaps.get("uid"));
						}
						
						
						WebUp.upComponent(mField.getSourceCode()).inDelete(mField,
								mThisMap);
						
					}
				}
				
				
				new OcAccountPeriodService().delete(mPage.getPageTable(),mDelMaps.get("uid"));

			}
		}

		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969909001));
		}

		return mResult;
	}

}
