package com.cmall.groupcenter.job;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobExecutionContext;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 自动选品的各指标数据更新
 */
public class JobForProductXunpinLimit extends RootJob {

	static String PRODUCT_COMMENT = "JobForProductXunpinLimit#updateProductCommentNum";
	static String PRODUCT_BUYER = "JobForProductXunpinLimit#updateProductBuyerNum";
	
	Object lockobj = new Object();
	
	static ReentrantLock lock = new ReentrantLock();
	
	@Override
	public void doExecute(JobExecutionContext context) {
		if(!lock.tryLock()) return;
		
		try {
			updateProductCommentNum();
			updateProductBuyerNum();
		} finally {
			lock.unlock();
		}
	}
	
	// 更新评论数据
	private void updateProductCommentNum() {
		if(checkStatic(PRODUCT_COMMENT)) return;
		
		String today = FormatHelper.upDateTime("yyyy-MM-dd");
		String yestoday = DateFormatUtils.format(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd");
		
		// 取出起始的zid
		MDataMap lastMap = DbUp.upTable("nc_order_evaluation").oneWhere("zid", "zid desc", "oder_creattime < :yestoday", "yestoday", yestoday);
		if(lastMap == null) return;
		String startZid = lastMap.get("zid");
		
		String sql = "SELECT product_code, COUNT(1) comment_num, SUM(IF(grade >= 4, 1, 0)) comment_good_num FROM nc_order_evaluation WHERE "
				+ " zid > :startZid AND oder_creattime >= :yestoday AND oder_creattime < :today "
				+ " AND auto_good_evaluation_flag = 0 AND order_name != 'MI150824100141' "
				+ " GROUP BY product_code";
		
		// 昨天的评论数据
		List<Map<String, Object>> list = DbUp.upTable("nc_order_evaluation").dataSqlList(sql, new MDataMap("startZid", startZid, "today", today, "yestoday", yestoday));
		String updateSql = "update pc_product_xuanpin_comment set comment_num = comment_num + :comment_num, comment_good_num = comment_good_num + :comment_good_num,update_time = :update_time where product_code = :product_code";
		
		String now = FormatHelper.upDateTime();
		// 把昨天的评论数据累计到选品的中间表
		MDataMap updateMap = new MDataMap();
		for(Map<String, Object> m : list) {
			updateMap.put("product_code", m.get("product_code").toString());
			updateMap.put("comment_num", m.get("comment_num").toString());
			updateMap.put("comment_good_num", m.get("comment_good_num").toString());
			updateMap.put("update_time", now);
			
			if(DbUp.upTable("pc_product_xuanpin_comment").count("product_code",updateMap.get("product_code")) == 0) {
				DbUp.upTable("pc_product_xuanpin_comment").dataInsert(updateMap);
			} else {
				DbUp.upTable("pc_product_xuanpin_comment").dataExec(updateSql, updateMap);
			}
		}
		
		updateStatic(PRODUCT_COMMENT);
	}
	
	// 更新购买人数据
	private void updateProductBuyerNum() {
		if(checkStatic(PRODUCT_BUYER)) {
			return;
		}
		
		// 最近1年的购买人数
		String startDate = DateFormatUtils.format(DateUtils.addYears(new Date(), -1), "yyyy-MM-dd");
		String startZid = bConfig("groupcenter.xuanpin_rebuy_orderzid");
		String sql = "INSERT INTO pc_product_xuanpin_rebuy(product_code,rebuy_rate,update_time)"
				+ " SELECT x.product_code,ROUND(x.buyer_more/x.buyer_total*100,2),now() FROM ("
					+ " SELECT t.product_code, COUNT(1) buyer_total, SUM(IF(t.order_num > 1,1,0)) buyer_more "
					+ " FROM ("
					+ " SELECT d.product_code, o.buyer_code, COUNT(DISTINCT o.big_order_code) order_num FROM ordercenter.oc_orderinfo o, ordercenter.oc_orderdetail d"
					+ " WHERE o.zid > :startZid AND o.create_time > :startDate AND o.create_time < now() AND o.order_code = d.order_code AND o.order_status NOT IN ('4497153900010001','4497153900010006') AND d.gift_flag = '1'"
					+ " GROUP BY d.product_code,o.buyer_code"
					+ " ) t GROUP BY t.product_code HAVING buyer_more > 0"
				+ " ) x";
		
		// 先清理表数据
		DbUp.upTable("pc_product_xuanpin_rebuy").dataExec("TRUNCATE TABLE pc_product_xuanpin_rebuy", new MDataMap());
		// 再插入表数据
		DbUp.upTable("pc_product_xuanpin_rebuy").dataExec(sql, new MDataMap("startDate", startDate, "startZid", startZid));
		
		updateStatic(PRODUCT_BUYER);
	}
	
	/**
	 * 检查标量值，是否当天执行过定时
	 * @param key
	 * @return true 已经存在， false  不存在
	 */
	private boolean checkStatic(String key) {
		MDataMap map = DbUp.upTable("za_static").one("static_code", key);
		if(map == null) {
			map = new MDataMap();
			map.put("static_code", key);
			map.put("static_info", "");
			map.put("create_time", FormatHelper.upDateTime());
			DbUp.upTable("za_static").dataInsert(map);
			return false;
		}
		
		// 如果标量值的日期跟当天是同一天则表示已经执行过定时
		String today = FormatHelper.upDateTime("yyyy-MM-dd");
		return map.get("static_info").contains(today);
	}
	
	/**
	 * 执行完成更正标量值
	 * @param key
	 */
	private void updateStatic(String key) {
		MDataMap map = new MDataMap();
		map.put("static_code", key);
		map.put("static_info", FormatHelper.upDateTime());
		map.put("update_time", map.get("static_info"));
		DbUp.upTable("za_static").dataUpdate(map, "static_info,update_time", "static_code");
	}
}
