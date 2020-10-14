package com.cmall.groupcenter.tongji.job;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.tongji.baidu.AccountInfo;
import com.cmall.groupcenter.tongji.baidu.BaiduTongjiApi;
import com.cmall.groupcenter.tongji.baidu.MetricsType;
import com.cmall.groupcenter.tongji.baidu.ResultGetSiteList;
import com.cmall.groupcenter.tongji.baidu.ResultGetSiteList.SiteInfo;
import com.cmall.groupcenter.tongji.baidu.ResultLandingpageA;
import com.cmall.groupcenter.tongji.baidu.ResultLandingpageA.LandingpageA;
import com.cmall.groupcenter.tongji.baidu.ResultTrendTimeA;
import com.cmall.groupcenter.tongji.baidu.ResultTrendTimeA.TrendTimeA;
import com.cmall.groupcenter.tongji.baidu.ResultVisitToppageA;
import com.cmall.groupcenter.tongji.baidu.ResultVisitToppageA.VisitToppageA;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 定时调用百度统计的接口
 */
public class JobForBaiduTongji extends RootJob {
	
	static Log LOG = LogFactory.getLog(BaiduTongjiApi.class);
	
	// 专题
	Pattern ZT = Pattern.compile("/template.html\\?pNum=(PNM\\d+)");
	// 秒杀
	Pattern MS = Pattern.compile("/product_secKill_salesCnt_n.html\\?pageCode=(ZT\\d+)");
	// 闪购
	Pattern SG = Pattern.compile("/saleshtml.ht\\?");
	// 扫码购
	Pattern SMG = Pattern.compile("/\\d?smg.html");
	
	// 专题统计固定域名，暂时写死
	String ztHost = "zt-family.huijiayou.cn";
	// 扫码购的所在域名
	String smgHost = "m.huijiayou.cn";

	@Override
	public void doExecute(JobExecutionContext context) {
		String username = bConfig("groupcenter.tongji_baidu_username");
		String password = bConfig("groupcenter.tongji_baidu_password");
		String token = bConfig("groupcenter.tongji_baidu_token");
		String userId = bConfig("groupcenter.tongji_baidu_userId");
		
		if(StringUtils.isBlank(username) || StringUtils.isBlank(password) 
				|| StringUtils.isBlank(token) || StringUtils.isBlank(userId) ){
			return;
		}
		
		AccountInfo accountInfo = new AccountInfo(username,password,token,userId);
		BaiduTongjiApi tongjiApi = new BaiduTongjiApi(accountInfo);
		
		// 刷新站点数据
		List<SiteInfo> siteList = refreshSiteList(tongjiApi);
		
		for(SiteInfo siteInfo : siteList){
			// 只统计启用的域名
			if("0".equals(siteInfo.getStatus())){
				// 刷新趋势分析数据
				refreshTrendTimeA(tongjiApi, siteInfo);
				
				// 暂时只统计专题的页面数据
				if(ztHost.equalsIgnoreCase(siteInfo.getDomain())){
					// 刷新受访页面数据
					refreshVisitToppageA(tongjiApi, siteInfo);
				}
				
				// 暂时只统计扫码购的页面数据
				if(smgHost.equalsIgnoreCase(siteInfo.getDomain())){
					// 入口页面数据
					refreshLandingpageA(tongjiApi, siteInfo);
				}
			}
		}
	}

	private List<SiteInfo> refreshSiteList(BaiduTongjiApi api){
		ResultGetSiteList result = api.getSiteList();
		MDataMap mDataMap;
		for(SiteInfo siteInfo : result.getList()){
			mDataMap = DbUp.upTable("fh_tongji_baidu_site").one("site_id",siteInfo.getSiteId());
			if(mDataMap == null){
				mDataMap = new MDataMap();
				mDataMap.put("site_id", siteInfo.getSiteId());
				mDataMap.put("domain", siteInfo.getDomain());
				mDataMap.put("status", siteInfo.getStatus());
				mDataMap.put("create_time", FormatHelper.upDateTime());
				mDataMap.put("update_time", mDataMap.get("create_time"));
				DbUp.upTable("fh_tongji_baidu_site").dataInsert(mDataMap);
			}
			
			if(!mDataMap.get("status").equals(siteInfo.getStatus())){
				mDataMap.put("status", siteInfo.getStatus());
				mDataMap.put("update_time", FormatHelper.upDateTime());
				DbUp.upTable("fh_tongji_baidu_site").dataUpdate(mDataMap, "status,update_time", "uid,zid");
			}
			
			//System.out.println(JSONObject.toJSON(mDataMap));
		}
		
		// 接口调用失败
		if(result.getResultCode() != 1){
			LOG.warn("JobForBaiduTongji --> refreshSiteList 接口调用失败");
		}
		
		return result.getList();
	}
	
	/**
	 * 抓取昨日的趋势统计数据
	 * @param api
	 * @param siteInfo
	 */
	private void refreshTrendTimeA(BaiduTongjiApi api,SiteInfo siteInfo){
		String day = FormatHelper.upDateTime(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd");
		
		Map<String, String> param = new HashMap<String, String>();
		param.put("start_date", day);
		param.put("end_date", day);
		param.put("gran", "day");
		param.put("site_id", siteInfo.getSiteId());
		
		ResultTrendTimeA result = api.getReportData(MetricsType.TREND_TIME_A, param, new ResultTrendTimeA.ParseImpl());
		MDataMap mDataMap;
		for(TrendTimeA item : result.getList()){
			mDataMap = DbUp.upTable("fh_tongji_baidu_trend").one("site_id",siteInfo.getSiteId(),"the_date", day);
			if(mDataMap == null) {
				mDataMap = new MDataMap();
			}
			
			mDataMap.put("the_date", day);
			mDataMap.put("site_id", 			siteInfo.getSiteId());
			mDataMap.put("pv_count", 			replace(item.getPvCount(),"0"));
			mDataMap.put("pv_ratio", 			replace(item.getPvRatio(),"0"));
			mDataMap.put("visit_count", 		replace(item.getVisitCount(),"0"));
			mDataMap.put("visitor_count", 		replace(item.getVisitorCount(),"0"));
			mDataMap.put("new_visitor_count",  replace(item.getNewVisitorCount(),"0"));
			mDataMap.put("new_visitor_ratio",   replace(item.getNewVisitorRatio(),"0"));
			mDataMap.put("ip_count", 			replace(item.getIpCount(),"0"));
			mDataMap.put("bounce_ratio", 		replace(item.getBounceRatio(),"0"));
			mDataMap.put("avg_visit_time",		replace(item.getAvgVisitTime(),"0"));
			mDataMap.put("avg_visit_pages", 	replace(item.getAvgVisitPages(),"0"));
			mDataMap.put("update_time", 		FormatHelper.upDateTime());
			
			if(mDataMap.get("uid") == null){
				mDataMap.put("create_time", mDataMap.get("update_time"));
				DbUp.upTable("fh_tongji_baidu_trend").dataInsert(mDataMap);
			} else {
				DbUp.upTable("fh_tongji_baidu_trend").update(mDataMap);
			}
			
			//System.out.println(JSONObject.toJSON(mDataMap));
		}
	}
	
	/**
	 * 抓取昨日的专题活动数据
	 * @param api
	 * @param siteInfo
	 */
	private void refreshVisitToppageA(BaiduTongjiApi api,SiteInfo siteInfo){
		String day = FormatHelper.upDateTime(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd");
		
		Map<String, String> param = new HashMap<String, String>();
		param.put("start_date", day);
		param.put("end_date", day);
		param.put("site_id", siteInfo.getSiteId());
		param.put("max_results", "0");
		
		ResultVisitToppageA result = api.getReportData(MetricsType.VISIT_TOPPAGE_A, param, new ResultVisitToppageA.ParseImpl());
		Map<String,AtomicInteger> groupSizeMap = new HashMap<String, AtomicInteger>();
		Map<String,VisitToppageA> groupItemMap = new HashMap<String, VisitToppageA>();
		Map<String,String> groupTypeMap = new HashMap<String, String>();
		
		String code,type;
		Matcher mc;
		VisitToppageA tmp;
		// 对专题页面编码，秒杀编码，闪购分组一下数据
		for(VisitToppageA item : result.getList()){
			// 忽略测试环境数据
			if(item.getUrl().startsWith("http://test-")){
				continue;
			}
			
			code = null;
			type = null;
			
			// 提取专题页面编号
			mc = ZT.matcher(item.getUrl());
			if(mc.find()){
				code = mc.group(1);
				type = "template";
			}
			
			// 提取秒杀页面编号
			if(code == null){
				mc = MS.matcher(item.getUrl());
				if(mc.find()){
					code = mc.group(1);
					type = "secKill";
				}
			}
			
			// 匹配是否是闪购
			if(code == null){
				mc = SG.matcher(item.getUrl());
				if(mc.find()){
					code = "saleshtml";
					type = "saleshtml";
				}
			}
			
			// 都匹配不到则忽略
			if(code == null) continue;
		
			tmp = groupItemMap.get(code);
			if(tmp == null){
				groupItemMap.put(code, item);
				groupSizeMap.put(code, new AtomicInteger(1));
				groupTypeMap.put(code, type);
				continue;
			}
			
			groupSizeMap.get(code).incrementAndGet();
			
			// 如果已经存在同类型的数据则直接更新已经存在的数据
			tmp.setPvCount(NumberUtils.toInt(tmp.getPvCount()) + NumberUtils.toInt(item.getPvCount()) + "");
			tmp.setVisitorCount(NumberUtils.toInt(tmp.getVisitorCount()) + NumberUtils.toInt(item.getVisitorCount()) + "");
			tmp.setIpCount(NumberUtils.toInt(tmp.getIpCount()) + NumberUtils.toInt(item.getIpCount()) + "");
			tmp.setVisit1Count(NumberUtils.toInt(tmp.getVisit1Count()) + NumberUtils.toInt(item.getVisit1Count()) + "");
			tmp.setOutwardCount(NumberUtils.toInt(tmp.getOutwardCount()) + NumberUtils.toInt(item.getOutwardCount()) + "");
			tmp.setExitCount(NumberUtils.toInt(tmp.getExitCount()) + NumberUtils.toInt(item.getExitCount()) + "");
			tmp.setAverageStayTime(NumberUtils.toInt(tmp.getAverageStayTime()) + NumberUtils.toInt(item.getAverageStayTime()) + "");
			if(!"0".equals(item.getPvCount())){
				tmp.setExitRatio(new BigDecimal(tmp.getExitCount()).divide(new BigDecimal(tmp.getPvCount()),4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).toString());
			}else{
				tmp.setExitRatio("0");
			}
		}
		
		MDataMap mDataMap;
		for(Entry<String, VisitToppageA> item : groupItemMap.entrySet()){
			mDataMap = DbUp.upTable("fh_tongji_baidu_toppage").one("site_id",siteInfo.getSiteId(), "the_date", day, "page_code",item.getKey());
			if(mDataMap == null){
				mDataMap = new MDataMap();
			}
			
			mDataMap.put("site_id", siteInfo.getSiteId());
			mDataMap.put("the_date", day);
			mDataMap.put("page_type",         groupTypeMap.get(item.getKey()));
			mDataMap.put("page_code",         item.getKey());
			mDataMap.put("pv_count",          item.getValue().getPvCount());
			mDataMap.put("visitor_count", 	  item.getValue().getVisitorCount());
			mDataMap.put("ip_count",          item.getValue().getIpCount());
			mDataMap.put("visit1_count",      item.getValue().getVisit1Count());
			mDataMap.put("outward_count",     item.getValue().getOutwardCount());
			mDataMap.put("exit_count",        item.getValue().getExitCount());
			// 页面平均停留时间 = 总平均停留时间  % 页面数量
			mDataMap.put("average_stay_time", new BigDecimal(item.getValue().getAverageStayTime()).divide(new BigDecimal(groupSizeMap.get(item.getKey()).get()),0,BigDecimal.ROUND_HALF_UP).toString());
			mDataMap.put("exit_ratio",        item.getValue().getExitRatio());
			mDataMap.put("update_time", FormatHelper.upDateTime());
			
			if(mDataMap.get("uid") == null){
				mDataMap.put("create_time", mDataMap.get("update_time"));
				DbUp.upTable("fh_tongji_baidu_toppage").dataInsert(mDataMap);
			} else {
				DbUp.upTable("fh_tongji_baidu_toppage").update(mDataMap);
			}
			
			//System.out.println(JSONObject.toJSON(mDataMap));
		}
	}
	
	/**
	 * 抓取昨日的入口页面
	 * @param api
	 * @param siteInfo
	 */
	private void refreshLandingpageA(BaiduTongjiApi api,SiteInfo siteInfo){
		String day = FormatHelper.upDateTime(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd");
		
		Map<String, String> param = new HashMap<String, String>();
		param.put("start_date", day);
		param.put("end_date", day);
		param.put("site_id", siteInfo.getSiteId());
		param.put("max_results", "0");
		
		ResultLandingpageA result = api.getReportData(MetricsType.VISIT_LANDINGPAGE_A, param, new ResultLandingpageA.ParseImpl());
		
		// 近保存扫码购数据
		MDataMap mDataMap;
		for(LandingpageA item : result.getList()){
			mDataMap = DbUp.upTable("fh_tongji_baidu_landingpage").one("site_id",siteInfo.getSiteId(), "the_date", day, "url",item.getUrl());
			if(mDataMap == null){
				mDataMap = new MDataMap();
			}
			
			// 排除测试数据
			if(item.getUrl().contains("test")) {
				continue;
			}
			
			// 排除非扫码购标识数据
			if(!SMG.matcher(item.getUrl()).find()) {
				continue;
			}
			
			mDataMap.put("site_id", 			siteInfo.getSiteId());
			mDataMap.put("the_date", 			day);
			mDataMap.put("url",                 item.getUrl());
			mDataMap.put("visit_count",         item.getVisitCount());
			mDataMap.put("visitor_count", 	    item.getVisitorCount());
			mDataMap.put("new_visitor_count",   item.getNewVisitorCount());
			mDataMap.put("new_visitor_ratio",   item.getNewVisitorRatio());
			mDataMap.put("ip_count",     		item.getIpCount());
			mDataMap.put("out_pv_count",        item.getOutPvCount());
			mDataMap.put("bounce_ratio", 		item.getBounceRatio());
			mDataMap.put("avg_visit_time",      item.getAvgVisitTime());
			mDataMap.put("avg_visit_pages",     item.getAvgVisitPages());
			mDataMap.put("update_time", 		FormatHelper.upDateTime());
			
			if(mDataMap.get("uid") == null){
				mDataMap.put("create_time", mDataMap.get("update_time"));
				DbUp.upTable("fh_tongji_baidu_landingpage").dataInsert(mDataMap);
			} else {
				DbUp.upTable("fh_tongji_baidu_landingpage").update(mDataMap);
			}
		}
	}
	
	/**
	 * 替换百度统计中无数据库的情况，替换“--”为默认值
	 * @param o
	 * @param n
	 * @return
	 */
	private String replace(String o, String n){
		if("--".equals(o)){
			return n;
		}
		return o;
	}
}
