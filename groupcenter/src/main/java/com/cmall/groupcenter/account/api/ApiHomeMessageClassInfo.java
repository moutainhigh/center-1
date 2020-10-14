package com.cmall.groupcenter.account.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.cmall.groupcenter.account.model.ApiHomeMessageClassListResult;
import com.cmall.groupcenter.account.model.ApiHomeMessageClassResult;
import com.cmall.systemcenter.util.AppVersionUtils;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 用户客户端消息分类列表
 * 
 * @author sunyan
 * 
 */
public class ApiHomeMessageClassInfo extends RootApiForToken<ApiHomeMessageClassListResult, RootInput> {

	public ApiHomeMessageClassListResult Process(
			RootInput inputParam, MDataMap mRequestMap) {

		ApiHomeMessageClassListResult apiHomeMessageInfoResult = new ApiHomeMessageClassListResult();
		String buyer_code = getUserCode();
//		String is_flag = XmasKv.upFactory(EKvSchema.MessageUseable).get("useable");
//		if(StringUtils.isBlank(is_flag)){
//			MDataMap one = DbUp.upTable("sc_message_configure").one();
//			is_flag = one.get("is_flag");
//		}
//		if(!is_flag.equals("4497480100020001")){
//			apiHomeMessageInfoResult.setResultCode(-1);
//			apiHomeMessageInfoResult.setResultMessage("客户端消息并没有启用");
//			return apiHomeMessageInfoResult;
//		}
		
		MDataMap mapParam = new MDataMap();
		mapParam.put("buyer_code", buyer_code);
		String sql = "SELECT a.classify,IFNULL(b.cnt,0) cnt FROM fh_message_notification a LEFT JOIN (SELECT a.classify,COUNT(a.zid) cnt FROM fh_message_notification a WHERE a.uid NOT IN (SELECT b.message_code from fh_message_read b where b.member_code = :buyer_code) AND a.status = '4497469400030002' GROUP BY a.classify) b ON a.classify = b.classify GROUP BY a.classify";
		List<Map<String, Object>> infoList = new ArrayList<Map<String, Object>>();
		infoList = DbUp.upTable("fh_message_notification").dataSqlList(sql, mapParam);
		if(infoList.size()>0){
			for(Map<String, Object> map:infoList){
				ApiHomeMessageClassResult info = new ApiHomeMessageClassResult();
				info.setCount(map.get("cnt")==null?"":map.get("cnt").toString());
				mapParam.put("classify", map.get("classify")==null?"":map.get("classify").toString());
				String sqlString = "SELECT b.define_name,a.content,a.start_time FROM systemcenter.sc_define b LEFT JOIN (SELECT * FROM fh_message_notification a WHERE a. STATUS = '4497469400030002') a ON b.define_code = a.classify WHERE b.define_code = :classify ORDER BY a.start_time DESC,a.zid DESC LIMIT 1";
				Map<String, Object> infoMap = DbUp.upTable("fh_message_notification").dataSqlOne(sqlString, mapParam);
				info.setClassify(infoMap.get("define_name")==null?"":infoMap.get("define_name").toString());
				String content = infoMap.get("content")==null?"":infoMap.get("content").toString();
				if(content.length()>32){
					content = content.substring(0, 32)+"...";
				}
				info.setContent(content);
				info.setStart_time(infoMap.get("start_time")==null?"":infoMap.get("start_time").toString().substring(0, 10).replaceAll("-", "."));
				info.setClassifyCode("4497471600420001");
				apiHomeMessageInfoResult.add(info);
			}
		}
		/**
		 * 新增售后消息 20190621 NG++
		 */
		MDataMap apiClient = getApiClient();
		String appVersion = "";
		if(apiClient != null && !apiClient.isEmpty()) {
			appVersion = apiClient.get("app_vision");
		}
		if("".equals(appVersion)) {
			appVersion = "5.6.0";
		}
		if(AppVersionUtils.compareTo(appVersion, "5.4.60")>=0) {
			String sql2 = "SELECT * FROM newscenter.nc_aftersale_push_news WHERE member_code = :buyer_code ORDER BY create_time DESC";
			List<Map<String, Object>> afterSaleInfoList = new ArrayList<Map<String, Object>>();
			afterSaleInfoList = DbUp.upTable("nc_aftersale_push_news").dataSqlList(sql2, mapParam);
			Integer count = DbUp.upTable("nc_aftersale_push_news").count("member_code",buyer_code,"if_read","0");
			if(count == null) {
				count = 0;
			}
			ApiHomeMessageClassResult info = new ApiHomeMessageClassResult();
			info.setCount(count.toString());
			info.setClassify("售后消息");
			info.setClassifyCode("4497471600420002");
			if(afterSaleInfoList.size()>0){
				Map<String,Object> map = afterSaleInfoList.get(0);
				String content = afterSaleInfoList.get(0).get("message").toString();
				if(content.length()>32){
					content = content.substring(0, 32)+"...";
				}
				info.setContent(content);
				info.setStart_time(map.get("create_time")==null?"":map.get("create_time").toString().substring(0, 10).replaceAll("-", "."));
			}
			apiHomeMessageInfoResult.add(info);
		}
		
		// 548添加,物流通知消息
		if(AppVersionUtils.compareTo(appVersion, "5.4.80")>=0) {
			String sql3 = "SELECT * FROM newscenter.nc_logistics_notice_push_news WHERE member_code = :buyer_code ORDER BY create_time DESC";
			List<Map<String, Object>> logisticsNoticeList = new ArrayList<Map<String, Object>>();
			logisticsNoticeList = DbUp.upTable("nc_logistics_notice_push_news").dataSqlList(sql3, mapParam);
			Integer count3 = DbUp.upTable("nc_logistics_notice_push_news").count("member_code",buyer_code,"if_read","0");
			if(count3 == null) {
				count3 = 0;
			}
			ApiHomeMessageClassResult info = new ApiHomeMessageClassResult();
			info.setCount(count3.toString());
			info.setClassify("物流通知");
			info.setClassifyCode("4497471600420003");
			if(null != logisticsNoticeList && logisticsNoticeList.size()>0){
				Map<String,Object> map = logisticsNoticeList.get(0);
				//Map<String,Object> map2 = logisticsNoticeList.get(logisticsNoticeList.size()-1);
				String content = logisticsNoticeList.get(0).get("title").toString();
				if(content.length()>32){
					content = content.substring(0, 32)+"...";
				}
				info.setContent(content);
				info.setStart_time(map.get("create_time")==null?"":map.get("create_time").toString().substring(0, 10).replaceAll("-", "."));
			}
			apiHomeMessageInfoResult.add(info);
		}
		
		// 560添加,客服意见反馈消息
		if(AppVersionUtils.compareTo(appVersion, "5.6.0")>=0) {
			// 配置时间参数，上线后接口只返回配置时间参数之后的回复内容，历史回复不返回
			Map<String, Object> feedbackTimeMap = DbUp.upTable("zw_define").dataSqlOne("SELECT * FROM zw_define WHERE define_dids = '469923300002'", new MDataMap());
			String feedbackTime = MapUtils.getString(feedbackTimeMap, "define_remark");
			MDataMap member = DbUp.upTable("mc_login_info").one("member_code",buyer_code);
			String login_name = member.get("login_name");
			String sql4 = "SELECT count(1) num FROM lc_suggestion_feedback WHERE commit_user = '"+login_name+"' AND reply_time >= '"+feedbackTime+"' AND is_read = '0'";
			String feedbackNum = "0";
			Map<String,Object> feedbackMap = DbUp.upTable("lc_suggestion_feedback").dataSqlOne(sql4, new MDataMap());
			if(null != feedbackMap) {
				feedbackNum = feedbackMap.get("num")!= null ?feedbackMap.get("num").toString():"0";
			}
			
			String reply_time = "";
			String feedbackSql = "SELECT * FROM lc_suggestion_feedback WHERE commit_user = '"+login_name+"' AND reply_time >= '"+feedbackTime+"' ORDER BY reply_time DESC";
			List<Map<String, Object>> feedbackList = DbUp.upTable("lc_suggestion_feedback").dataSqlList(feedbackSql, new MDataMap());
			
			ApiHomeMessageClassResult info = new ApiHomeMessageClassResult();
			info.setClassify("意见反馈");
			info.setClassifyCode("4497471600420004");
			info.setCount(feedbackNum);
			if(Integer.parseInt(feedbackNum) > 0) {				
				info.setContent("您的反馈已回复，点击查看");
				if(null != feedbackList && feedbackList.size() > 0) {
					reply_time = MapUtils.getString(feedbackList.get(0), "reply_time");
				}
			}
			info.setStart_time("".equals(reply_time)?"":reply_time.toString().substring(0, 10).replaceAll("-", "."));
			
			apiHomeMessageInfoResult.add(info);
			
		}
		
		return apiHomeMessageInfoResult;
	}
	
}
