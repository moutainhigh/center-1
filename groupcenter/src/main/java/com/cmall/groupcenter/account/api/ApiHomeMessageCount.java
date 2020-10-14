package com.cmall.groupcenter.account.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.cmall.groupcenter.account.model.ApiHomeMessageCntResult;
import com.cmall.systemcenter.util.AppVersionUtils;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 订单跟踪轨迹表
 * 
 * @author sunyan
 * 
 */
public class ApiHomeMessageCount extends RootApiForToken<ApiHomeMessageCntResult, RootInput> {

	public ApiHomeMessageCntResult Process(
			RootInput inputParam, MDataMap mRequestMap) {

		ApiHomeMessageCntResult apiHomeMessageCntResult = new ApiHomeMessageCntResult();
		String buyer_code = getUserCode();
//		String is_flag = XmasKv.upFactory(EKvSchema.MessageUseable).get("useable");
//		if(StringUtils.isBlank(is_flag)){
//			MDataMap one = DbUp.upTable("sc_message_configure").one();
//			is_flag = one.get("is_flag");
//		}
//		if(!is_flag.equals("4497480100020001")){
//			apiHomeMessageCntResult.setIs_flag("N");
//			apiHomeMessageCntResult.setResultCode(-1);
//			apiHomeMessageCntResult.setResultMessage("客户端消息并没有启用");
//			return apiHomeMessageCntResult;
//		}else{
//			apiHomeMessageCntResult.setIs_flag("Y");
//		}
		MDataMap mapParam = new MDataMap();
		mapParam.put("buyer_code", buyer_code);
		String sql = "SELECT count(uid) cnt FROM fh_message_notification a WHERE a.uid NOT IN (SELECT b.message_code from fh_message_read b where b.member_code = :buyer_code) AND a.status = '4497469400030002'";
		List<Map<String, Object>> infoList = new ArrayList<Map<String, Object>>();
		infoList = DbUp.upTable("fh_message_notification").dataSqlList(sql, mapParam);
		String oldNum = "0";
		Integer totalNum = 0;
		if(infoList.size()>0){
			oldNum = infoList.get(0).get("cnt")==null?"0":infoList.get(0).get("cnt").toString();
		}
		String sql2 = "SELECT COUNT(uid) num FROM newscenter.nc_aftersale_push_news WHERE member_code = :buyer_code AND if_read = 0";
		String afterSaleNum = "0";
		Map<String,Object> map = DbUp.upTable("nc_aftersale_push_news").dataSqlOne(sql2, mapParam);
		if(null != map) {
			afterSaleNum = map.get("num")!= null ?map.get("num").toString():"0";
		}
		
		// 548添加,物流通知消息
		String sql3 = "SELECT count(uid) num FROM newscenter.nc_logistics_notice_push_news WHERE member_code = :buyer_code AND if_read = 0";
		String logisticsNoticeNum = "0";
		Map<String,Object> logisticsNoticeMap = DbUp.upTable("nc_logistics_notice_push_news").dataSqlOne(sql3, mapParam);
		if(null != logisticsNoticeMap) {
			logisticsNoticeNum = logisticsNoticeMap.get("num")!= null ?logisticsNoticeMap.get("num").toString():"0";
		}
		
		// 560添加,客服意见反馈消息
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
		
		MDataMap apiClient = getApiClient();
		String appVersion = "";
		if(apiClient != null && !apiClient.isEmpty()) {
			appVersion = apiClient.get("app_vision");
		}
		if("".equals(appVersion)) {
			appVersion = "5.6.0";
		}
		if(AppVersionUtils.compareTo(appVersion, "5.4.60")<0) {
			totalNum = Integer.parseInt(oldNum);
		}else if(AppVersionUtils.compareTo(appVersion, "5.4.80")<0){			
			totalNum = Integer.parseInt(afterSaleNum)+Integer.parseInt(oldNum);
		}else if(AppVersionUtils.compareTo(appVersion, "5.6.0")<0){			
			totalNum = Integer.parseInt(afterSaleNum)+Integer.parseInt(oldNum)+Integer.parseInt(logisticsNoticeNum);
		}else {
			totalNum = Integer.parseInt(afterSaleNum)+Integer.parseInt(oldNum)+Integer.parseInt(logisticsNoticeNum)+Integer.parseInt(feedbackNum);
		}
		apiHomeMessageCntResult.setMessage_cnt(totalNum.toString());
		return apiHomeMessageCntResult;
	}
}
