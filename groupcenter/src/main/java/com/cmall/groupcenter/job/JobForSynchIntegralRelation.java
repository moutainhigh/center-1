package com.cmall.groupcenter.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncIntegralRelation;
import com.cmall.groupcenter.homehas.model.IntegralRelation;
import com.cmall.groupcenter.homehas.model.RsyncRequestIntegralRelation;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 定时同步积分战队关系到LD
 * @remark 
 * @author 任宏斌
 * @date 2019年3月15日
 */
public class JobForSynchIntegralRelation extends RootJob {

	private final int COUNT = 200;
	
	public void doExecute(JobExecutionContext context) {
		String sSql = "SELECT * FROM mc_integral_relation WHERE is_valid='1' AND update_time >= DATE_SUB(curdate(), INTERVAL 1 DAY) AND update_time < DATE_SUB(curdate(), INTERVAL 0 DAY)";
		List<Map<String, Object>> dataList = DbUp.upTable("mc_integral_relation").dataSqlList(sSql, new MDataMap());
		if(null != dataList && !dataList.isEmpty()) {
			Map<String, List<String>> eventTimeMap = new HashMap<String, List<String>>();//key:eventCode  value:活动开始时间、介绍时间
			
			int part = 1;
			if(dataList.size()>COUNT) {
				part = (dataList.size()/COUNT) + (dataList.size()%COUNT==0? 0 : 1);
			}
			for(int i = 0; i < part; i++) {
				List<IntegralRelation> integralRelationList = new ArrayList<IntegralRelation>();
				int start = i*COUNT;
				int end = 0;
				if(i==(part-1) && dataList.size()%COUNT!=0) {
					end = dataList.size()%COUNT;
				}else {
					end = (i+1)*COUNT;
				}
				for(int j = start; j< end; j ++) {
					Map<String, Object> teamInfo = dataList.get(j);
					String inviter_code = teamInfo.get("inviter_code")+"";
					String invitee_code = teamInfo.get("invitee_code")+"";
					String is_valid = teamInfo.get("is_valid")+"";
					String is_main = teamInfo.get("is_main")+"";
					String event_code = teamInfo.get("event_code")+"";
					
					IntegralRelation integralRelation = new IntegralRelation();
					integralRelation.setWeb_id(inviter_code);
					integralRelation.setScust_web_id(invitee_code);
					
					MDataMap inviter = DbUp.upTable("mc_login_info").one("member_code",inviter_code);
					MDataMap invitee = DbUp.upTable("mc_login_info").one("member_code",invitee_code);
					integralRelation.setFcust_id(inviter.get("login_name"));
					integralRelation.setScust_id(invitee.get("login_name"));
					integralRelation.setVl_yn("1".equals(is_valid)?"Y":"N");
					integralRelation.setIs_main(is_main);
					integralRelation.setEtr_id("app");//写死
					integralRelation.setEtr_date(teamInfo.get("create_time")+"");//写死
					integralRelation.setEvent_code(event_code);
					if(eventTimeMap.containsKey(event_code)) {
						integralRelation.setBegin_time(eventTimeMap.get(event_code).get(0));
						integralRelation.setEnd_time(eventTimeMap.get(event_code).get(1));
					}else {
						String querySql = "SELECT begin_time,end_time FROM sc_hudong_event_info WHERE event_code=:event_code";
						Map<String, Object> queryResult = DbUp.upTable("sc_hudong_event_info").dataSqlOne(querySql, new MDataMap("event_code",event_code));
						if(null!=queryResult) {
							integralRelation.setBegin_time(queryResult.get("begin_time")+"");
							integralRelation.setEnd_time(queryResult.get("end_time")+"");
							
							List<String> timeList = new ArrayList<String>();
							timeList.add(queryResult.get("begin_time")+"");
							timeList.add(queryResult.get("end_time")+"");
							eventTimeMap.put(event_code, timeList);
						}
					}
					
					MDataMap inviterAddr = DbUp.upTable("nc_address").one("address_code",inviter_code,"address_default","1");
					if(null != inviterAddr) {
						integralRelation.setCust_nm(inviterAddr.get("address_name"));
						integralRelation.setAddr_2(inviterAddr.get("address_street"));
						integralRelation.setSrgn_cd(inviterAddr.get("area_code"));
						integralRelation.setZip_no(inviterAddr.get("address_postalcode"));
					}
					MDataMap inviteeAddr = DbUp.upTable("nc_address").one("address_code",invitee_code,"address_default","1");
					if(null != inviteeAddr) {
						integralRelation.setScust_nm(inviteeAddr.get("address_name"));
						integralRelation.setScust_addr_2(inviteeAddr.get("address_street"));
						integralRelation.setScust_srgn_cd(inviteeAddr.get("area_code"));
						integralRelation.setScust_zip_no(inviteeAddr.get("address_postalcode"));
					}
					
					integralRelationList.add(integralRelation);
				}
				
				if(!integralRelationList.isEmpty()) {
					RsyncIntegralRelation rsyncIntegralRelation = new RsyncIntegralRelation();
					RsyncRequestIntegralRelation upRsyncRequest = rsyncIntegralRelation.upRsyncRequest();
					upRsyncRequest.setParamList(integralRelationList);
					rsyncIntegralRelation.doRsync();
				}
			}
		}
	}
}
