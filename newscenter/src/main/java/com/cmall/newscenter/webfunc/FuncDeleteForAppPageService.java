package com.cmall.newscenter.webfunc;

import java.util.List;
import java.util.Map;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 删除app页面
 * @author jl
 *
 */
public class FuncDeleteForAppPageService extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MWebOperate mOperate = WebUp.upOperate(sOperateUid);
		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());
		MDataMap mDelMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		if (mResult.upFlagTrue()) {
			if (mDelMaps.containsKey("uid")) {
				List<Map<String, Object>> exlist = DbUp.upTable(mPage.getPageTable()).dataSqlList("SELECT c.uid from nc_app_column c LEFT JOIN nc_app_page p on c.page_code=p.page_code where p.uid=:uid", mDelMaps);
				if(exlist.size()>0){
					mResult.setResultCode(909101010);
					mResult.setResultMessage(bInfo(909101010));
				}else {
					DbUp.upTable(mPage.getPageTable()).delete("uid",mDelMaps.get("uid"));
				}
			}
		}
		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969909001));
		}
		return mResult;
	}
	
}
