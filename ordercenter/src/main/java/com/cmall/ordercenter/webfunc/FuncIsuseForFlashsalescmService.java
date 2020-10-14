package com.cmall.ordercenter.webfunc;

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
 * 发布闪购活动
 * @author jl
 *
 */
public class FuncIsuseForFlashsalescmService extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MWebOperate mOperate = WebUp.upOperate(sOperateUid);
		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());
		MDataMap mEditMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		if (mResult.upFlagTrue()) {
			
			List<Map<String, Object>> list=DbUp.upTable(mPage.getPageTable()).dataSqlList("select status,activity_code from oc_activity_flashsales where uid=:uid", new MDataMap("uid",mEditMaps.get("uid")));
			
			if (list!=null&&list.size()>0) {
				
				String status=(String)list.get(0).get("status");
				String activity_code=(String)list.get(0).get("activity_code");
				if ("449746740001".equals(status)) {//449746740001: 未发布
					
					//判断没有商品不让发布
					if(DbUp.upTable("oc_flashsales_skuInfo").count("activity_code",activity_code,"status","449746810001")<1){
						mResult.setResultCode(939301115);
						mResult.setResultMessage(bInfo(939301115));
						return mResult;
					}
					
					DbUp.upTable(mPage.getPageTable()).dataUpdate(new MDataMap("uid",mEditMaps.get("uid"), "status","449746740002"),"","uid");
					mResult.setResultMessage(bInfo(969909001));
				} else {
					mResult.setResultCode(939301108);
					mResult.setResultMessage(bInfo(939301108));
				}
			}else{
				mResult.setResultCode(939301110);
				mResult.setResultMessage(bInfo(939301110));
			}
		}
		
		return mResult;
		
	}
}
