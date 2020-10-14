package com.cmall.newscenter.webfunc;

import com.cmall.newscenter.util.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 小时代消息推送新增
 * @author houwen
 * */
public class CommentPushAdd extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		MDataMap map = new MDataMap();
		
		if (mResult.upFlagTrue()) {
			/*系统当前时间*/
			String create_time = DateUtil.getNowTime();
			/*获取当前登录人*/
			String create_user = UserFactory.INSTANCE.create().getLoginName();
			/*获取当前app*/
			String appCode = UserFactory.INSTANCE.create().getManageCode();
			/*获取消息推送时间；*/
			//if(VersionHelper.checkServerVersion("7.5.15.501")&&(appCode.equals(MemberConst.MANAGE_CODE_BEAUTY)||appCode.equals(MemberConst.MANAGE_CODE_BEAUTY))){
			String radioId = mDataMap.get("radioId");
			String push_time = "";
			String cysTime = "";
			if(radioId.equals("0")){
				 push_time = mAddMaps.get("push_time");
				 if(null==push_time || push_time.equals("")){
					 mResult.setResultMessage("指定时间不能为空！");
					 return mResult;
				 }
				 map.put("push_time",mAddMaps.get("push_time"));
				 map.put("send_time",mAddMaps.get("push_time"));

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
				map.put("push_time", "3000-01-01 00:00:00"); //推送时间，推送的时候，会自动匹配今天的日期，如果匹配成功，将日期改为当前时间，默认值，便于推送
				map.put("send_time", push_time); //推送时间，仅用于后台页面展示
				map.put("cys_time", cysTime); //推送时间，用于后台定时任务更新时间
			}
			map.put("title", mAddMaps.get("title"));
			map.put("comment", mAddMaps.get("comment"));
			map.put("jump_type", mAddMaps.get("jump_type"));
			map.put("jump_position", mAddMaps.get("jump_position"));
			map.put("push_status", "4497465000070001");//默认未完成
			map.put("status",mAddMaps.get("status"));   // 启用：449747090001;停用 ：449747090002 
			map.put("create_user", create_user);
			map.put("create_time", create_time);
			map.put("app_code", appCode);
			
			/**将消息推送信息插入nc_comment_push表中*/
			DbUp.upTable("nc_comment_push").dataInsert(map);
		}
		return mResult;
	}

}
