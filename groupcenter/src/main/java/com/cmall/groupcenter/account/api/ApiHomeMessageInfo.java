package com.cmall.groupcenter.account.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.account.model.ApiHomeMessageInfoInput;
import com.cmall.groupcenter.account.model.ApiHomeMessageInfoListResult;
import com.cmall.groupcenter.account.model.ApiHomeMessageInfoResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 用户客户端消息列表
 * 
 * @author sunyan
 * 
 */
public class ApiHomeMessageInfo extends RootApiForToken<ApiHomeMessageInfoListResult, ApiHomeMessageInfoInput> {

	public ApiHomeMessageInfoListResult Process(
			ApiHomeMessageInfoInput inputParam, MDataMap mRequestMap) {
		ApiHomeMessageInfoListResult result = new ApiHomeMessageInfoListResult();
		String classifyCode = inputParam.getClassifyCode();
		if("4497471600420001".equals(classifyCode)) {
			/**
			 * 系统消息
			 */
			result = this.oldSysNews(inputParam, mRequestMap);
		}else if("4497471600420002".equals(classifyCode)) {
			/**
			 * 售后消息
			 */
			result = this.afterSaleNews(inputParam, mRequestMap);
		}else if("4497471600420003".equals(classifyCode)) {
			/**
			 * 物流通知
			 */
			result = this.logisticsNoticeNews(inputParam, mRequestMap);
		}else if("4497471600420004".equals(classifyCode)) {
			/**
			 * 意见反馈
			 */
			result = this.feedbackNews(inputParam, mRequestMap);
		}
		return result;
	}
	
	/**
	 * 售后消息
	 * @param inputParam
	 * @param mRequestMap
	 * @return
	 */
	private ApiHomeMessageInfoListResult afterSaleNews(ApiHomeMessageInfoInput inputParam, MDataMap mRequestMap) {
		ApiHomeMessageInfoListResult apiHomeMessageInfoResult = new ApiHomeMessageInfoListResult();
		String buyer_code = getUserCode();
		int pageNo = inputParam.getPageNo();
		int pageSize=inputParam.getPageSize();
		
		MDataMap mapParam = new MDataMap();
		mapParam.put("buyer_code", buyer_code);
		String sql = "SELECT * FROM newscenter.nc_aftersale_push_news WHERE  member_code = :buyer_code order by zid desc,create_time desc";
		sql = sql + " limit "+pageNo*pageSize+" ,"+pageSize;
		List<Map<String, Object>> infoList = new ArrayList<Map<String, Object>>();
		infoList = DbUp.upTable("nc_aftersale_push_news").dataSqlList(sql, mapParam);
		Integer recordNum = DbUp.upTable("nc_aftersale_push_news").count("member_code",buyer_code);
		for(Map<String,Object> map : infoList) {
			if(null == map) {
				continue;
			}
			String uid = map.get("uid").toString();
			MDataMap query = new MDataMap();
			query.put("uid", uid);
			query.put("if_read", "1");
			if("0".equals(map.get("if_read").toString())) {
				DbUp.upTable("nc_aftersale_push_news").dataUpdate(query, "if_read", "uid");
			}
			ApiHomeMessageInfoResult info = new ApiHomeMessageInfoResult();
			info.setContent(map.get("message").toString());
			info.setAfterSaleCode(map.get("after_sale_code").toString());
			String time = map.get("create_time").toString();
			time = time.replaceFirst("-", "年");
			time = time.replaceFirst("-", "月");
			time = time.replaceFirst(" ", "日 ");
			time = time.substring(0, 17);
			info.setStart_time(time);
			info.setTitle(map.get("title").toString());
			
			// 548添加,消息通知类型和消息布局类型
			info.setType("4497471600410004");
			info.setLayout("4497471600440001");
			
			apiHomeMessageInfoResult.add(info);
		}
		int pageNum=0;
		if ((recordNum%pageSize) == 0) {
			pageNum = (int) recordNum / pageSize;
		} else {
			pageNum = (int) recordNum / pageSize + 1;
		}
		apiHomeMessageInfoResult.setPageNum(pageNum);
		return apiHomeMessageInfoResult;
	}

	/**
	 * 系统消息
	 * @param inputParam
	 * @param mRequestMap
	 * @return
	 */
	private ApiHomeMessageInfoListResult oldSysNews(ApiHomeMessageInfoInput inputParam, MDataMap mRequestMap) {


		ApiHomeMessageInfoListResult apiHomeMessageInfoResult = new ApiHomeMessageInfoListResult();
		String buyer_code = getUserCode();
		int pageNo = inputParam.getPageNo();
		int pageSize=inputParam.getPageSize();
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
		String sql = "SELECT * FROM fh_message_notification a WHERE a.status = '4497469400030002' order by a.start_time desc,a.zid desc";
		sql = sql + " limit "+pageNo*pageSize+" ,"+pageSize;
		List<Map<String, Object>> infoList = new ArrayList<Map<String, Object>>();
		infoList = DbUp.upTable("fh_message_notification").dataSqlList(sql, mapParam);
		if(infoList.size()>0){
			for(Map<String, Object> map:infoList){
				ApiHomeMessageInfoResult info = new ApiHomeMessageInfoResult();
				String t = map.get("end_time")==null?"":map.get("end_time").toString();
				t = StringUtils.substringBeforeLast(t, ":");
				t = t.replaceFirst("-", "年");
				t = t.replaceFirst("-", "月");
				t = t.replaceFirst(" ", "日 ");
				info.setContent(map.get("content")==null?"":map.get("content").toString());
				info.setEnd_time(t);
				info.setPicture(map.get("picture")==null?"":map.get("picture").toString());
				info.setProduct_code(map.get("product_code")==null?"":map.get("product_code").toString());
				t = map.get("start_time")==null?"":map.get("start_time").toString();
				t = StringUtils.substringBeforeLast(t, ":");
				t = t.replaceFirst("-", "年");
				t = t.replaceFirst("-", "月");
				t = t.replaceFirst(" ", "日 ");
				info.setStart_time(t);
				info.setTitle(map.get("title")==null?"":map.get("title").toString());
				info.setType(map.get("type")==null?"":map.get("type").toString());
				info.setUid(map.get("uid")==null?"":map.get("uid").toString());
				info.setUrl(map.get("url")==null?"":map.get("url").toString());
				
				// 548添加,消息布局类型
				if(!"".equals(info.getPicture())) {
					info.setLayout("4497471600440002");
				}else {
					info.setLayout("4497471600440001");
				}
				// info.setLayout(map.get("picture")==null?"4497471600440001":"4497471600440002");
				
				apiHomeMessageInfoResult.add(info);				
			}
		}
		String sqltemp = "SELECT * FROM fh_message_notification a WHERE a.uid NOT IN (SELECT b.message_code from fh_message_read b where b.member_code = :buyer_code) AND a.status = '4497469400030002'";
		sqltemp = sqltemp + " limit "+pageNo*pageSize+" ,"+pageSize;
		List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
		tempList = DbUp.upTable("fh_message_notification").dataSqlList(sqltemp, mapParam);
		if(tempList.size()>0){
			for(Map<String, Object> map:tempList){
				DbUp.upTable("fh_message_read").insert("message_code",map.get("uid")==null?"":map.get("uid").toString(),"member_code",buyer_code);
			}
		}
		
		int pageNum=0;
		int recordNum=0;
		String sqlString = "SELECT count(*) cnt FROM fh_message_notification a WHERE a.status = '4497469400030002'";
		Map<String, Object> proListPage = null;
		proListPage = DbUp.upTable("fh_message_notification").dataSqlOne(sqlString,mapParam);
		if(proListPage!=null){
			recordNum = Integer.parseInt(proListPage.get("cnt").toString());
			 
			if ((recordNum%pageSize) == 0) {
				pageNum = (int) recordNum / pageSize;
			} else {
				pageNum = (int) recordNum / pageSize + 1;
			}
		}
		apiHomeMessageInfoResult.setPageNum(pageNum);
		
		return apiHomeMessageInfoResult;
	}
	
	/**
	 * 548添加,物流通知
	 * @param inputParam
	 * @param mRequestMap
	 * @return
	 */
	private ApiHomeMessageInfoListResult logisticsNoticeNews(ApiHomeMessageInfoInput inputParam, MDataMap mRequestMap) {
		ApiHomeMessageInfoListResult apiHomeMessageInfoResult = new ApiHomeMessageInfoListResult();
		String buyer_code = getUserCode();
		int pageNo = inputParam.getPageNo();
		int pageSize=inputParam.getPageSize();
		
		MDataMap mapParam = new MDataMap();
		mapParam.put("buyer_code", buyer_code);
		String sql = "SELECT * FROM newscenter.nc_logistics_notice_push_news WHERE  member_code = :buyer_code order by zid desc,create_time desc";
		sql = sql + " limit "+pageNo*pageSize+" ,"+pageSize;
		List<Map<String, Object>> infoList = new ArrayList<Map<String, Object>>();
		infoList = DbUp.upTable("nc_logistics_notice_push_news").dataSqlList(sql, mapParam);
		Integer recordNum = DbUp.upTable("nc_logistics_notice_push_news").count("member_code",buyer_code);
		if(null != infoList) {
			for(Map<String,Object> map : infoList) {
				if(null == map) {
					continue;
				}
				String uid = map.get("uid").toString();
				MDataMap query = new MDataMap();
				query.put("uid", uid);
				query.put("if_read", "1");
				if("0".equals(map.get("if_read").toString())) {
					DbUp.upTable("nc_logistics_notice_push_news").dataUpdate(query, "if_read", "uid");
				}
				ApiHomeMessageInfoResult info = new ApiHomeMessageInfoResult();
				info.setTitle(map.get("title")==null?"":map.get("title").toString());
				info.setContent(map.get("message")==null?"":map.get("message").toString());
				info.setType("4497471600410005");
				info.setLayout("4497471600440003");
				info.setOrder_code(map.get("order_code")==null?"":map.get("order_code").toString());
				info.setProd_main_pic(map.get("prod_main_pic")==null?"":map.get("prod_main_pic").toString());
				String time = map.get("create_time").toString();
				time = time.replaceFirst("-", "年");
				time = time.replaceFirst("-", "月");
				time = time.replaceFirst(" ", "日 ");
				time = time.substring(0, 17);
				info.setStart_time(time);
				apiHomeMessageInfoResult.add(info);
			}
		}
		int pageNum=0;
		if ((recordNum%pageSize) == 0) {
			pageNum = (int) recordNum / pageSize;
		} else {
			pageNum = (int) recordNum / pageSize + 1;
		}
		apiHomeMessageInfoResult.setPageNum(pageNum);
		return apiHomeMessageInfoResult;
	}
	
	/**
	 * 560添加,意见反馈
	 * @param inputParam
	 * @param mRequestMap
	 * @return
	 */
	private ApiHomeMessageInfoListResult feedbackNews(ApiHomeMessageInfoInput inputParam, MDataMap mRequestMap) {
		ApiHomeMessageInfoListResult apiHomeMessageInfoResult = new ApiHomeMessageInfoListResult();
		String buyer_code = getUserCode();
		int pageNo = inputParam.getPageNo();
		int pageSize=inputParam.getPageSize();
		
		// 配置时间参数，上线后接口只返回配置时间参数之后的回复内容，历史回复不返回
		Map<String, Object> feedbackTimeMap = DbUp.upTable("zw_define").dataSqlOne("SELECT * FROM zw_define WHERE define_dids = '469923300002'", new MDataMap());
		String feedbackTime = MapUtils.getString(feedbackTimeMap, "define_remark");
		MDataMap member = DbUp.upTable("mc_login_info").one("member_code",buyer_code);
		String login_name = member.get("login_name");
		
		int pageNum=0;
		String sql4 = "SELECT count(1) num FROM lc_suggestion_feedback WHERE commit_user = '"+login_name+"' AND reply_time >= '"+feedbackTime+"'";
		Map<String,Object> feedbackMap = DbUp.upTable("lc_suggestion_feedback").dataSqlOne(sql4, new MDataMap());
		if(null != feedbackMap) {
			int recordNum = MapUtils.getIntValue(feedbackMap, "num");
			if ((recordNum % pageSize) == 0) {
				pageNum = recordNum / pageSize;
			} else {
				pageNum = recordNum / pageSize + 1;
			}
		}
		apiHomeMessageInfoResult.setPageNum(pageNum);
		
		String feedbackSql = "SELECT * FROM lc_suggestion_feedback WHERE commit_user = '"+login_name+"' AND reply_time >= '"+feedbackTime+"' ORDER BY reply_time DESC";
		feedbackSql = feedbackSql + " limit "+pageNo*pageSize+" ,"+pageSize;
		List<Map<String, Object>> feedbackList = DbUp.upTable("lc_suggestion_feedback").dataSqlList(feedbackSql, new MDataMap());
		if(null != feedbackList && feedbackList.size() > 0) {
			for(Map<String,Object> map : feedbackList) {
				if(null == map) {
					continue;
				}
				String uid = map.get("uid").toString();
				MDataMap query = new MDataMap();
				query.put("uid", uid);
				query.put("is_read", "1");
				if("0".equals(map.get("is_read"))) {
					DbUp.upTable("lc_suggestion_feedback").dataUpdate(query, "is_read", "uid");
				}
				
				ApiHomeMessageInfoResult info = new ApiHomeMessageInfoResult();
				info.setType("4497471600410001");
				info.setLayout("4497471600440004");
				info.setSuggestion_feedback(MapUtils.getString(map, "suggestion_feedback"));
				info.setFeedbackTime(MapUtils.getString(map, "update_time").substring(0, 10).replaceAll("-", "."));
				info.setRepply_content(MapUtils.getString(map, "repply_content"));
				String time = map.get("reply_time").toString();
				time = time.replaceFirst("-", "年");
				time = time.replaceFirst("-", "月");
				time = time.replaceFirst(" ", "日 ");
				time = time.substring(0, 17);
				info.setStart_time(time);
				apiHomeMessageInfoResult.add(info);
			}
		}
		
		return apiHomeMessageInfoResult;
	}
	
}
