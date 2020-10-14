package com.cmall.newscenter.webfunc;



import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 删除系统消息
 * 
 * @author shiyz
 * 
 */
public class FuncDeleteForSystemMessage extends RootFunc {
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MWebOperate mOperate = WebUp.upOperate(sOperateUid);

		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		// 定义插入数据库
		MDataMap mInsertMap = new MDataMap();

		recheckMapField(mResult, mPage, mAddMaps);

		if (mResult.upFlagTrue()) {
			
			try{
			DbUp.upTable(mPage.getPageTable()).delete("message_code",mAddMaps.get("uid"));
			
				
			DbUp.upTable("nc_system_message").delete("message_code",mAddMaps.get("uid"));
			}catch(Exception e){
				mResult.setResultMessage(bInfo(969912006));
				
			}

		}

		return mResult;

	}
	
}
