package com.cmall.groupcenter.job;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 微公社用户预警管理，按照规则统计预警用户。
 * @author GaoYang
 * @CreateDate 2015年4月21日下午2:13:17
 *
 */
public class JobGroupAccountEarlyWarning extends RootJob{
	
	public void doExecute(JobExecutionContext context) {
		
		//规则1用MAP
		HashMap<String, String> fNumMap = new HashMap<String, String>();
		HashMap<String, Integer> fNumValMap = new HashMap<String, Integer>();
		//规则2用MAP
		HashMap<String, String> fConsumeMap = new HashMap<String, String>();
		HashMap<String, Integer> fConsumeValMap = new HashMap<String, Integer>();
		
		String sysFormat = "yyyy-MM-dd"; // 年/月/日
		SimpleDateFormat sFormat = new SimpleDateFormat(sysFormat);
		
		//系统时间
		java.sql.Timestamp timestamp = DateUtil.getSysDateTimestamp();
		
		//现在时间
		String nowYMD = sFormat.format(timestamp);
		
		//根据系统时间获取昨天的日期
		String yesterday = GetDiffYMD(sFormat,timestamp,-1);
		
		//根据系统时间获取一个月前的日期(以30天为标准)
		String oneMonthDay = GetDiffYMD(sFormat,timestamp,-30);
		
		//预警规则1：统计微公社中每个用户昨日新增一度好友数量，然后进行倒序排列，以排序第25%的用户新增一度好友数*1.5作为标准，高于此标准则该指标超标。(gc_member_relation)
		//查询微公社所有账户的昨日新增一度好友数量
		String sql1="SELECT re.parent_code,count(re.account_code) as friendsNum FROM gc_member_relation re WHERE date(create_time) = '"
				+ yesterday +"' GROUP BY re.parent_code ORDER BY friendsNum DESC ";
		
		List<Map<String, Object>> fListMap=DbUp.upTable("gc_member_relation").dataSqlList(sql1, new MDataMap());
		
		int friendsCount = fListMap.size();
		if(fListMap != null && friendsCount > 0){
			
			int fIndex = 0;//规则1：排序第25%用户位置
			int standardNum = 0;//标准数量
			int myfriendsNum = 0;//微公社账户的昨日新增一度好友数量
			
			//按照规则1 获取一度好友数的标准值的位置
			if(friendsCount > 1){
				fIndex =  (int) Math.floor((friendsCount * 0.25 + 1));
			}
			
			//以排序第25%位置的用户数值*1.5作为标准
			standardNum = (int) Math.floor(Integer.parseInt(String.valueOf(fListMap.get(fIndex).get("friendsNum"))) * 1.5);
			
			for(Map<String, Object> map : fListMap){
				
				//微公社账户
				String parentCode = String.valueOf(map.get("parent_code"));

				//每个账户的一度好友数量
				myfriendsNum = Integer.parseInt(String.valueOf(map.get("friendsNum")));
				
				if(myfriendsNum>standardNum){
					fNumMap.put(parentCode, "true");//规则1超标
				}else{
					fNumMap.put(parentCode, "false");//规则1正常
				}
				//临时保存每个账户的一度好友数量
				fNumValMap.put(parentCode, myfriendsNum);
			}
		}
		
		//预警规则2：一度好友人均消费金额标准）： 仅统计一度好友总人数大于10人的用户。统计微公社中每个用户截止至统计当日0点 过去一个月的 一度好友的总消费金额/一度好友的总人数，将数值倒序排列，
		//以排序第25%位置的用户数值*1.5作为标准，高于此标准则该指标超标。(gc_active_log)
		StringBuffer sql2= new StringBuffer();
		sql2.append("SELECT c.parent_code,IFNULL(sum(d.consume_money),0)/c.friendsNum as avgMoney FROM groupcenter.gc_active_log d,");
		sql2.append("(");
		sql2.append("SELECT b.parent_code,a.account_code,friendsNum FROM  groupcenter.gc_member_relation a,");
		sql2.append("(");
		sql2.append("SELECT parent_code,friendsNum FROM ");
		sql2.append("(");
		sql2.append("SELECT  re.parent_code,count(re.account_code) as friendsNum ");
		sql2.append(" FROM groupcenter.gc_member_relation re ");
		sql2.append(" GROUP BY re.parent_code ");
		sql2.append(" ORDER BY friendsNum DESC");
		sql2.append(") AS f");
		sql2.append(" WHERE f.friendsNum > 10 ");
		sql2.append(") AS b WHERE a.parent_code=b.parent_code");
		sql2.append(") c ");
		sql2.append(" WHERE  d.order_account_code=c.account_code ");
		sql2.append(" AND date(d.active_time) >='").append(oneMonthDay).append("' ");
		sql2.append(" AND date(d.active_time) <'").append(nowYMD).append("' ");
		sql2.append(" GROUP BY c.parent_code ");
		sql2.append(" ORDER BY avgMoney DESC ");
		
		List<Map<String, Object>> fListMap2=DbUp.upTable("gc_member_relation").dataSqlList(sql2.toString(), new MDataMap());
		
		int monthCount = fListMap2.size();
		if(fListMap2 != null && monthCount > 0){
			
			int mIndex = 0;//规则2：排序第25%用户位置
			int standardConsume = 0;//标准消费
			int fridensAvgConsume = 0;//一度好友的前一个月平均消费额
			
			//以排序第25%位置的用户数值*1.5作为标准
			if(monthCount > 1){
				mIndex = (int) Math.floor((monthCount * 0.25 + 1));
			}
			BigDecimal sagm = new BigDecimal(fListMap2.get(mIndex).get("avgMoney").toString());
			BigDecimal at = new BigDecimal(1.5);
			Double aResult = sagm.multiply(at).doubleValue();
			standardConsume = (int) Math.floor(aResult);
			
			for(Map<String, Object> map2 : fListMap2){
				
				//微公社账户
				String parentCode2 = String.valueOf(map2.get("parent_code"));
				//微公社账户的一度好友前一个月平均消费额
				BigDecimal agm = new BigDecimal(map2.get("avgMoney").toString());
				BigDecimal t1 = new BigDecimal(1);
				Double fAvg = agm.multiply(t1).doubleValue();
				fridensAvgConsume = (int) Math.floor(fAvg);
				
				if(fridensAvgConsume>standardConsume){
					fConsumeMap.put(parentCode2, "true");//规则2超标
				}else{
					fConsumeMap.put(parentCode2, "false");//规则2正常
				}
				
				//临时保存一度好友前一个月平均消费额
				fConsumeValMap.put(parentCode2, fridensAvgConsume);
			}
		}
		
		//以统计消费额的账户为基准进行计算
		//规则1数据
		Iterator fNumIter = fNumMap.entrySet().iterator();
		while(fNumIter.hasNext()){
			
			//预警等级计算
			String warningLevel = "";
			
			Map.Entry nEntry = (Map.Entry) fNumIter.next();
			Object fNumKey = nEntry.getKey();//规则1账户
			Object fNumVal = nEntry.getValue();//规则1是否超标
			
			//规则2数据
			Iterator consumeIter = fConsumeMap.entrySet().iterator();
			while (consumeIter.hasNext()){
				Map.Entry cEntry = (Map.Entry) consumeIter.next();
				Object consumeKey = cEntry.getKey();//规则2账户
				Object consumeVal = cEntry.getValue();//规则2是否超标
				
				if(consumeKey.equals(fNumKey)){
					//指标1超标&&指标2超标 预警等级=高
					if("true".equals(fNumVal) && "true".equals(consumeVal)){
						warningLevel = "449747210003";
					}else if("false".equals(fNumVal) && "true".equals(consumeVal)){
						//指标1正常&&指标2超标 预警等级=中
						warningLevel = "449747210002";
					}else if("true".equals(fNumVal) && "false".equals(consumeVal)){
						//指标1超标&&指标2正常 预警等级=低
						warningLevel = "449747210001";
					}
					break;
				}
			}
			
			//组织数据
			if(warningLevel.startsWith("4497")){
				MDataMap dMap = new MDataMap();
				dMap.put("account_code", String.valueOf(fNumKey));
				dMap.put("warning_level", warningLevel);
				dMap.put("newfriends_number_oneday", String.valueOf(fNumValMap.get(fNumKey)));
				dMap.put("average_consume_onemonth", String.valueOf(fConsumeValMap.get(fNumKey)));
				dMap.put("create_time", DateUtil.getSysDateString());
				dMap.put("warning_code", WebHelper.upCode("WARN"));
				DbUp.upTable("gc_account_earlyWarning").dataInsert(dMap);
			}
		}
	}

	/**
	 * 根据系统时间获取相差天数的日期
	 * @param sFormat 
	 * @param i 相差天数
	 * @param timestamp 系统日期
	 * @return
	 */
	private String GetDiffYMD(SimpleDateFormat sFormat, Timestamp timestamp, int i) {
		
		String ymd = sFormat.format(DateUtil.addDays(timestamp, i));
		return ymd;
		
	}
	
//	public static void main(String[] args) {
//
//		JobGroupAccountEarlyWarning job = new JobGroupAccountEarlyWarning();
//
//		job.doExecute(null);
//	}
	
}
