package com.cmall.newscenter.webfunc;

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
 * 修改营销推送消息 
 * @author houwen
 *
 */
public class FuncEditForComment extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();

		MWebOperate mOperate = WebUp.upOperate(sOperateUid);

		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		MDataMap mInsertMap = new MDataMap();

		recheckMapField(mResult, mPage, mAddMaps);

		// 定义组件判断标记
		boolean bFlagComponent = false;

		if (mResult.upFlagTrue()) {

			// 循环所有结构
			for (MWebField mField : mPage.getPageFields()) {

				if (mField.getFieldTypeAid().equals("104005003")) {
					bFlagComponent = true;
				}

				if (mAddMaps.containsKey(mField.getColumnName())) {

					String sValue = mAddMaps.get(mField.getColumnName());

					mInsertMap.put(mField.getColumnName(), sValue);
				} else if (mField.getFieldTypeAid().equals("104005103")) {
					//特殊判断修改时如果没有传值 则自动赋空
					mInsertMap.put(mField.getColumnName(), "");
				}

			}
		}

		if (mResult.upFlagTrue()) {
			
			/*获取消息推送时间；*/
			//if(VersionHelper.checkServerVersion("7.5.15.501")&&(appCode.equals(MemberConst.MANAGE_CODE_BEAUTY)||appCode.equals(MemberConst.MANAGE_CODE_BEAUTY))){
			/*获取消息推送时间；*/
			String radioId = mDataMap.get("radioId");
			String push_time = "";
			String cysTime = "";
			if(radioId.equals("0")){
				 push_time = mDataMap.get("zw_f_push_time");
				 if(null==push_time || push_time.equals("")){
					 mResult.setResultMessage("指定时间不能为空！");
					 return mResult;
				 }
				 mInsertMap.put("push_time",mAddMaps.get("push_time"));
				 mInsertMap.put("send_time",mAddMaps.get("push_time"));
			}else {
				String timeId = mDataMap.get("timeId");
				if(null==timeId || timeId.equals("")){
					mResult.setResultMessage("指定周期的时间不能为空！");
					return mResult;
				}
				String cycleId = mDataMap.get("cycleId");
				if(cycleId.equals("0")){
					String weekId = mDataMap.get("weekId");
					cysTime = weekId;
					push_time = "每" + weekId + " " + timeId;
				}else if(cycleId.equals("1")){
					String monthId = mDataMap.get("monthId");
					cysTime = monthId;
					push_time = "每月" + monthId + " " + timeId;
				}
				mInsertMap.put("push_time", "3000-01-01 00:00:00"); //推送时间，推送的时候，会自动匹配今天的日期，如果匹配成功，将日期改为当前时间，默认值 ，便于推送
				mInsertMap.put("send_time", push_time); //推送时间，仅用于后台页面展示
				mInsertMap.put("cys_time", cysTime); //推送时间，用于后台定时任务更新时间
			}
		
			}
			
			DbUp.upTable(mPage.getPageTable())
					.dataUpdate(mInsertMap, "", "uid");

			if (bFlagComponent) {

				for (MWebField mField : mPage.getPageFields()) {
					if (mField.getFieldTypeAid().equals("104005003")) {

						WebUp.upComponent(mField.getSourceCode()).inEdit(
								mField, mDataMap);

					}
				}

			}

		//}

		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969909001));
		}
		return mResult;
	}
}
