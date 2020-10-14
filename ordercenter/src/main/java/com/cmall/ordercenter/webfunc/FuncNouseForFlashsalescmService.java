package com.cmall.ordercenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 取消发布闪购活动
 * @author jl
 *
 */
public class FuncNouseForFlashsalescmService extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MWebOperate mOperate = WebUp.upOperate(sOperateUid);
		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());
		MDataMap mEditMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		if (mResult.upFlagTrue()) {
			MDataMap dataMap=DbUp.upTable(mPage.getPageTable()).one("uid",mEditMaps.get("uid"));
			if (dataMap!=null&&dataMap.size()>0) {
				if ("449746740002".equals((String) dataMap.get("status"))) {//449746740002: 已发布
					
					DbUp.upTable(mPage.getPageTable()).dataUpdate(new MDataMap("uid",mEditMaps.get("uid"), "status","449746740001"),"","uid");
					mResult.setResultMessage(bInfo(969909001));
				} else {
					mResult.setResultCode(939301109);
					mResult.setResultMessage(bInfo(939301109));
				}
			}else{
				mResult.setResultCode(939301110);
				mResult.setResultMessage(bInfo(939301110));
			}
		}
		
		return mResult;
	}
}
