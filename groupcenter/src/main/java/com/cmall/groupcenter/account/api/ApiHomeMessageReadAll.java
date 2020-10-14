package com.cmall.groupcenter.account.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.cmall.systemcenter.util.AppVersionUtils;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 用户客户端消息标记为已读
 * 
 * @author sunyan
 * 
 */
public class ApiHomeMessageReadAll extends RootApiForToken<RootResultWeb, RootInput> {

	public RootResultWeb Process(
			RootInput inputParam, MDataMap mRequestMap) {

		RootResultWeb rootResultWeb = new RootResultWeb();
		String buyer_code = getUserCode();
		
		MDataMap apiClient = getApiClient();
		String appVersion = "";
		if(apiClient != null && !apiClient.isEmpty()) {
			appVersion = apiClient.get("app_vision");
		}
		if("".equals(appVersion)) {
			appVersion = "5.6.0";
		}
		
		MDataMap mapParam = new MDataMap();
		mapParam.put("buyer_code", buyer_code);
		String sql = "SELECT * FROM fh_message_notification a WHERE a.uid NOT IN (SELECT b.message_code from fh_message_read b where b.member_code = :buyer_code) AND a.status = '4497469400030002'";
		List<Map<String, Object>> infoList = new ArrayList<Map<String, Object>>();
		infoList = DbUp.upTable("fh_message_notification").dataSqlList(sql, mapParam);
		if(infoList.size()>0){
			for(Map<String, Object> map:infoList){
				DbUp.upTable("fh_message_read").insert("message_code",map.get("uid")==null?"":map.get("uid").toString(),"member_code",buyer_code);
			}
		}
		
		/**
		 * 售后消息变为已读
		 */
		mapParam.put("member_code", buyer_code);
		mapParam.put("if_read", "1");
		DbUp.upTable("nc_aftersale_push_news").dataUpdate(mapParam, "if_read", "member_code");
		
		// 548添加,物流通知消息全部已读
		DbUp.upTable("nc_logistics_notice_push_news").dataUpdate(mapParam, "if_read", "member_code");
		
		// 560:客服意见反馈消息全部已读
		if(AppVersionUtils.compareTo(appVersion, "5.6.0")>=0) { // 版本控制
			// 配置时间参数，上线后接口只返回配置时间参数之后的回复内容，历史回复不返回
			Map<String, Object> feedbackTimeMap = DbUp.upTable("zw_define").dataSqlOne("SELECT * FROM zw_define WHERE define_dids = '469923300002'", new MDataMap());
			String feedbackTime = MapUtils.getString(feedbackTimeMap, "define_remark");
			MDataMap member = DbUp.upTable("mc_login_info").one("member_code",buyer_code);
			String login_name = member.get("login_name");
			DbUp.upTable("lc_suggestion_feedback").dataExec("UPDATE lc_suggestion_feedback SET is_read = '1' WHERE commit_user = '"+login_name+"' AND reply_time >= '"+feedbackTime+"'", new MDataMap());
		}
		
		return rootResultWeb;
	}
}
