package com.cmall.groupcenter.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONObject;
import org.quartz.JobExecutionContext;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 优惠券过期提醒
 */
public class JobForCouponExpireRemind extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		// 限定一定时间内不能重复执行
		if(StringUtils.isBlank(WebHelper.addLock(300, "JobForCouponExpairyRemind"))){
			return;
		}
		
		// 关联一下活动编号优化查询速度
		String sql = "SELECT COUNT(*) num, member_code FROM oc_coupon_info WHERE activity_code IN( ";
			      sql += " SELECT a.activity_code FROM oc_activity a,oc_coupon_type t WHERE a.activity_code = t.activity_code";
					  sql += " and (";
					  sql += " (t.end_time != '' AND (t.end_time > DATE_SUB(NOW(),INTERVAL 1 DAY)))";
					  sql += " OR ";
					  sql += " (a.end_time > DATE_SUB(NOW(),INTERVAL 30 DAY))";
					  sql += " )";
				  sql += " )";
			  sql += " AND end_time > DATE_ADD(NOW(),INTERVAL 2 HOUR)";  // 优惠券到期时间在2个小时之后
			  sql += " AND end_time < DATE_ADD(NOW(),INTERVAL 24 HOUR)"; // 优惠券到期时间在24个小时之内
			  sql += " GROUP BY member_code"; 

		List<Map<String, Object>> dataList = DbUp.upTable("oc_coupon_info").dataSqlList(sql, new MDataMap());
		
		// 没有可提醒的优惠券定时直接返回
		if(dataList.isEmpty()) {
			return;
		}
		
		String taskCode = WebHelper.upCode("PBK");
		int taskSeq = 0;
		
		JSONObject obj = new JSONObject();
		obj.put("toPage", "6");  // 打开优惠券列表
		obj.put("msgContent", bConfig("groupcenter.coupon_expire_remind")); // 您有优惠券快过期了，快去使用吧！
		String content = obj.toString();
		
		List<String> memberCodes = new ArrayList<String>((int)(500/0.75)+1);
		for(Map<String, Object> map : dataList){
			if(StringUtils.isBlank((String)map.get("member_code"))){
				continue;
			}
			//校验该用户下的快过期优惠券是否是小程序专用的，如果是，则不提醒。
			if(checkChannel(MapUtils.getString(map, "member_code", ""))) {
				continue;
			}
			memberCodes.add(map.get("member_code")+"");
			
			// 500 条做一批
			if(memberCodes.size() == 500) {
				saveTaskDetail(taskCode, taskSeq, memberCodes, content);
				memberCodes.clear();
				taskSeq++;
			}
		}
		
		// 如果有剩余则单独做一批处理
		if(!memberCodes.isEmpty()) {
			saveTaskDetail(taskCode, taskSeq, memberCodes, content);
			memberCodes.clear();
		}
		
		// 插入任务主表
		MDataMap dataMap = new MDataMap();
		dataMap.put("task_code", taskCode);
		// 本次任务总批量处理条数
		dataMap.put("total_count", dataList.size()+"");
		// 任务最终执行结果
		dataMap.put("flag_success", "0");
		// 任务过期时间，超过时间则任务不再执行
		dataMap.put("expire_time", FormatHelper.upDateTime(DateUtils.addHours(new Date(), 2), "yyyy-MM-dd HH:mm:ss"));
		dataMap.put("create_time", FormatHelper.upDateTime());
		dataMap.put("update_time", dataMap.get("create_time"));
		DbUp.upTable("fh_push_batch_task").dataInsert(dataMap);
	}
	
	/**
	 * 校验渠道
	 * @param string
	 * @return true ：不推送。
	 */
	private boolean checkChannel(String memberCode) {
		String sql = "SELECT * FROM ordercenter.oc_coupon_info WHERE "
				+ "member_code = :member_code AND start_time < now() "
				+ "AND end_time > DATE_ADD(NOW(),INTERVAL 2 HOUR) "
				+ "AND end_time < DATE_ADD(NOW(),INTERVAL 24 HOUR)";
		List<Map<String,Object>> list = DbUp.upTable("oc_coupon_info").dataSqlList(sql,new MDataMap("member_code",memberCode));
		if(list.size() <= 0) {
			return true;
		}
		boolean flag = true;
		for(Map<String,Object> map : list) {
			String couponTypeCode = MapUtils.getString(map, "coupon_type_code", "");
			if(StringUtils.isEmpty(couponTypeCode)) {
				continue;
			}
			MDataMap limitMap = DbUp.upTable("oc_coupon_type_limit").one("coupon_type_code",couponTypeCode);
			if(limitMap == null || limitMap.isEmpty()) {//有无限制的，说明APP可用，返回false。需要给APP推送。
				return false;
			}
			String channel_limit = MapUtils.getString(limitMap, "channel_limit", "");
			if(StringUtils.isEmpty(channel_limit)||"4497471600070001".equals(channel_limit)) {//渠道无限制。返回false需要推送。
				return false;
			}else {//指定渠道，需要校验是否是小程序专用。
				String channelCodes = MapUtils.getString(limitMap, "channel_codes", "");
				if(channelCodes.contains("449747430001")) {//非小程序专用
					return false;
				}
				continue;
			}
			
		}
		return flag;
	}

	/**
	 * 插入任务明细表
	 * @param taskCode
	 * @param taskSeq
	 * @param memberCodes
	 */
	private void saveTaskDetail(String taskCode, int taskSeq, List<String> memberCodes, String content) {
		MDataMap detailMap = new MDataMap();
		detailMap.put("task_code", taskCode);
		detailMap.put("task_seq", taskSeq+"");
		detailMap.put("task_count", memberCodes.size()+"");
		detailMap.put("flag_success", "0");
		detailMap.put("exec_number", "0");
		detailMap.put("member_code", StringUtils.join(memberCodes,","));
		detailMap.put("push_param", content);
		detailMap.put("create_time", FormatHelper.upDateTime());
		detailMap.put("update_time", detailMap.get("create_time"));
		DbUp.upTable("fh_push_batch_task_detail").dataInsert(detailMap);
	}
	
}
