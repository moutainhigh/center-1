package com.cmall.groupcenter.job;

import java.util.Map;

import org.quartz.JobExecutionContext;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 每日更新最近7天的商品浏览数据汇总
 */
public class JobForBrowseHistoryWeek extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		// 清空表数据
		DbUp.upTable("pc_browse_history_week").dataExec("TRUNCATE TABLE pc_browse_history_week", new MDataMap());
		
		// 查询统计的起始zid
		String startZid = getStartZid();
		
		// 统计浏览数据表
		String sql = "INSERT INTO pc_browse_history_week(product_code,browse_num,create_time) SELECT product_code,COUNT(1) num,NOW() FROM `pc_browse_history_log` WHERE zid > :startZid GROUP BY product_code";
		DbUp.upTable("pc_browse_history_week").dataExec(sql, new MDataMap("startZid", startZid));
		
	}
	
	private String getStartZid() {
		String sql = "SELECT zid FROM `pc_browse_history_log` WHERE create_time < DATE_SUB(DATE(NOW()),INTERVAL 7 DAY) ORDER BY zid desc limit 1";
		Map<String, Object> startMap = DbUp.upTable("pc_browse_history_log").dataSqlOne(sql, new MDataMap());
		String zid = "20832558";
		if(startMap != null) {
			zid = startMap.get("zid").toString();
		}
		return zid;
	}

}
