package com.cmall.groupcenter.job;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 化妆包到期推送，到期日 晚8点推送
 * @author GaoYang
 *
 */
public class JobAddPushMessageForCosmetic extends RootJob {

	public void doExecute(JobExecutionContext context) {
		
		//查询条件：失效时间不为空，提醒时间不为空，即将过期
		String sql = "SELECT uid,member_code,cosmetic_code,cosmetic_name,disabled_time,warn_time,status FROM nc_cosmetic_bag WHERE disabled_time != '' and warn_time !='' and status ='449747120001'";
		List<Map<String, Object>> list = DbUp.upTable("nc_cosmetic_bag").dataSqlList(sql, null);
		
		if(list!=null && list.size()!=0){
			
			for (int i = 0; i < list.size(); i++) {
				
				Map<String, Object> map = list.get(i);
				
				String memberCode = map.get("member_code").toString();
//				String cosmeticCode = map.get("cosmetic_code").toString();
//				String cosmeticName = map.get("cosmetic_name").toString();
				String disabledTime = map.get("disabled_time").toString();
				String warnTime = map.get("warn_time").toString();
				
				java.sql.Timestamp timestamp = DateUtil.getSysDateTimestamp();
				//计算得到的到期日
				String compareDisTime = "";
				//推送内容
				String pushComment = "";
				
				if(!StringUtils.isEmpty(warnTime)){
					
					//提醒时间段
					String[] warnTimeAry = warnTime.split(",");
					//解析warn_time，到每个节点 将推送数据写入到表nc_comment_push_system，推送时间为20:00:00
					for(int j = 0; j < warnTimeAry.length; j++){
						String warnTimeSingle = warnTimeAry[j];
						if("449747140001".equals(warnTimeSingle)){
							//一个月
							compareDisTime = CalculateDisabledTime(timestamp,30);
							if(disabledTime.equals(compareDisTime)){
								//设定推送内容
								pushComment = "您有一个妆品1个月后过期啦，快来看看";
								AddPushCosmeticMessage(memberCode,pushComment);
							}
						}else if("449747140002".equals(warnTimeSingle)){
							//三个月
							compareDisTime = CalculateDisabledTime(timestamp,90);
							if(disabledTime.equals(compareDisTime)){
								//设定推送信息
								pushComment = "您有一个妆品3个月后过期啦，快来看看";
								AddPushCosmeticMessage(memberCode,pushComment);
							}
						}else if("449747140003".equals(warnTimeSingle)){
							//半年
							compareDisTime = CalculateDisabledTime(timestamp,180);
							if(disabledTime.equals(compareDisTime)){
								//设定推送信息
								pushComment = "您有一个妆品半年后过期啦,快来看看";
								AddPushCosmeticMessage(memberCode,pushComment);
							}
						}else if("449747140004".equals(warnTimeSingle)){
							//一年
							compareDisTime = CalculateDisabledTime(timestamp,360);
							if(disabledTime.equals(compareDisTime)){
								//设定推送信息
								pushComment = "您有一个妆品一年后过期啦,快来看看";
								AddPushCosmeticMessage(memberCode,pushComment);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 设定推送信息
	 * @param disabledTime
	 * @param memberCode
	 */
	private void AddPushCosmeticMessage(String memberCode, String pushComment) {
		
		MDataMap pushMap = new MDataMap();
		//系统时间
		String createTime = DateUtil.getSysDateTimeString();
		//系统日期
		String sysDateString = DateUtil.getSysDateString();
		
		pushMap.put("comment", pushComment);
		pushMap.put("push_time", sysDateString +" " + "20:00:00");
		pushMap.put("accept_member", memberCode);
		pushMap.put("jump_type", "0");
		pushMap.put("jump_position", "0");
		pushMap.put("push_status", "4497465000070001");
		pushMap.put("create_time", createTime);
		pushMap.put("app_code", "SI2007");//惠美丽
		
		DbUp.upTable("nc_comment_push_system").dataInsert(pushMap);
	}

	/**
	 * 根据系统日期由 天数计算妆品到期日
	 * @param timestamp 
	 * @param i
	 * @return
	 */
	private String CalculateDisabledTime(Timestamp timestamp, int i) {
		String sysFormat = "yyyy-MM-dd"; // 年/月/日
		SimpleDateFormat sysDateTime = new SimpleDateFormat(sysFormat);
		String ymd = sysDateTime.format(DateUtil.addDays(timestamp, i));
		return ymd;
	}
	
	/**
	 * 根据系统日期由 月数计算妆品到期日
	 */
//	private String CalculateDisabledTime(Timestamp timestamp, int i) {
//		String sysFormat = "yyyy-MM-dd"; // 年/月/日
//		SimpleDateFormat sysDateTime = new SimpleDateFormat(sysFormat);
//		String ymd = sysDateTime.format(DateUtil.addMonths(timestamp, i));
//		return ymd;
//	}

}
