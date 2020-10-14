package com.cmall.groupcenter.tongji.baidu;

/**
 * 百度统计各报告枚举定义
 */
public enum MetricsType {
	
	/** 网站概况(趋势数据) */
	OVERVIEW_GET_TIME_TREND_RPT("overview/getTimeTrendRpt",
			"pv_count,visitor_count,ip_count,bounce_ratio,avg_visit_time"),
			
	/** 网站概况(地域分布)*/
	OVERVIEW_GET_DISTRICT_RPT("overview/getDistrictRpt","pv_count"),
	
	/** 网站概况(来源网站、搜索词、入口页面、受访页面) */
	OVERVIEW_GET_COMMON_TRACK_RPT("overview/getCommonTrackRpt","pv_count"),
	
	/** 推广方式 */
	PRO_PRODUCT_A("pro/product/a",
			"show_count,clk_count,cost_count,ctr,cpm,pv_count,visit_count,visitor_count,new_visitor_count,new_visitor_ratio,in_visit_count,bounce_ratio,avg_visit_time,avg_visit_pages,arrival_ratio"),
	
	/** 趋势分析 */
	TREND_TIME_A("trend/time/a",
			"pv_count,pv_ratio,visit_count,visitor_count,new_visitor_count,new_visitor_ratio,ip_count,bounce_ratio,avg_visit_time,avg_visit_pages"),
			
	/** 百度推广趋势*/
	PRO_HOUR_A("pro/hour/a",
			"pv_count,pv_ratio,visit_count,visitor_count,new_visitor_count,new_visitor_ratio,ip_count,bounce_ratio,avg_visit_time,avg_visit_pages,trans_count,trans_ratio,avg_trans_cost,income,profit,roi"),
	
	/** 全部来源 */
	SOURCE_ALL_A("source/all/a",
			"pv_count,pv_ratio,visit_count,visitor_count,new_visitor_count,new_visitor_ratio,ip_count,bounce_ratio,avg_visit_time,avg_visit_pages,trans_count,trans_ratio"),
			
	/** 搜索引擎 */
	SOURCE_ENGINE_A("source/engine/a",
			"pv_count,pv_ratio,visit_count,visitor_count,new_visitor_count,new_visitor_ratio,ip_count,bounce_ratio,avg_visit_time,avg_visit_pages,trans_count,trans_ratio"),
			
	/** 搜索词 */
	SOURCE_SEARCHWORD_A("source/searchword/a",
			"pv_count,pv_ratio,visit_count,visitor_count,new_visitor_count,new_visitor_ratio,ip_count,bounce_ratio,avg_visit_time,avg_visit_pages,trans_count,trans_ratio"),
			
	/** 外部链接 */
	SOURCE_LINK_A("source/link/a",
			"pv_count,pv_ratio,visit_count,visitor_count,new_visitor_count,new_visitor_ratio,ip_count,bounce_ratio,avg_visit_time,avg_visit_pages,trans_count,trans_ratio"),
			
	/** 受访页面 */
	VISIT_TOPPAGE_A("visit/toppage/a",
			"pv_count,visitor_count,ip_count,visit1_count,outward_count,exit_count,average_stay_time,exit_ratio"),
			
	/** 入口页面  */
	VISIT_LANDINGPAGE_A("visit/landingpage/a",
			"visit_count,visitor_count,new_visitor_count,new_visitor_ratio,ip_count,out_pv_count,bounce_ratio,avg_visit_time,avg_visit_pages,trans_count,trans_ratio"),
			
	/** 受访域名 */
	VISIT_TOPDOMAIN_A("visit/topdomain/a",
			"pv_count,pv_ratio,visit_count,visitor_count,new_visitor_count,new_visitor_ratio,ip_count,bounce_ratio,average_stay_time ,avg_visit_pages"),
			
	/** 地域分布 (按省)*/
	VISIT_DISTRICT_A("visit/district/a",
			"pv_count,pv_ratio,visit_count,visitor_count,new_visitor_count,new_visitor_ratio,ip_count,bounce_ratio,avg_visit_time,avg_visit_pages,trans_count,trans_ratio"),
			
	/** 地域分布 (按国家)*/
	VISIT_WORLD_A("visit/world/a",
			"pv_count,pv_ratio,visit_count,visitor_count,new_visitor_count,new_visitor_ratio,ip_count,bounce_ratio,avg_visit_time,avg_visit_pages,trans_count,trans_ratio");

	/** 指标名 */
	private String method;
	/** 指标字段 */
	private String fileds;
	
	private MetricsType(String method, String fileds) {
		this.method = method;
		this.fileds = fileds;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getFileds() {
		return fileds;
	}
	public void setFileds(String fileds) {
		this.fileds = fileds;
	}

}
