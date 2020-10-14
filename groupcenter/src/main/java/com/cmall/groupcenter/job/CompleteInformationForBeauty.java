package com.cmall.groupcenter.job;

import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.cmall.productcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/***
 * 
 * 完善个人资料推送 满足条件：没有完善资料的用户每周三发一次推送 每周三12:00执行一次
 * 
 * @author yangrong
 * 
 */
public class CompleteInformationForBeauty extends RootJob {

	public void doExecute(JobExecutionContext context) {

		// 所有惠美丽用户信息
		String sql = "SELECT * from mc_extend_info_star WHERE app_code = 'SI2007' ";
		List<Map<String, Object>> list = DbUp.upTable("mc_extend_info_star").dataSqlList(sql, new MDataMap());
		
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				// 没有完善个人资料
				if (list.get(i).get("skin_type") == null || list.get(i).get("skin_type").toString().equals("") || list.get(i).get("hopeful").toString().equals("") || list.get(i).get("hopeful") == null) {
					
					// 推送表中插入一条消息
					
					MDataMap dbDataMap = DbUp.upTable("nc_sys_push_configure").one("configure_code","TS449746660008","status","449747090001","app_code","SI2007");
					
					if(dbDataMap!=null && !dbDataMap.isEmpty()){
						
						String start = dbDataMap.get("push_time_start").toString();
						
						String end = dbDataMap.get("push_time_end").toString();
						
						String now = DateUtil.getSysTimeString();
						
						int num1 = now.compareTo(start);
						
						int num2 = end.compareTo(now);
						
						Boolean flag = num1>=0 && num2>=0;
						
						if(start.equals("全天") || flag ){
							
							String content = dbDataMap.get("comment").toString();
							
							MDataMap map = new MDataMap();

							map.inAllValues("accept_member",list.get(i).get("member_code").toString(),"comment", content , "push_time",DateUtil.getSysDateTimeString(), "jump_type", "2","jump_position", "8", "push_status","4497465000070001", "create_time",DateUtil.getSysDateTimeString(), "app_code","SI2007");
							
							DbUp.upTable("nc_comment_push_system").dataInsert(map);
						}
						
					}

				}
			}
		}
		
		
		// 所有小时代用户信息
		String ssql = "SELECT * from mc_extend_info_star WHERE app_code = 'SI2013' ";
		List<Map<String, Object>> slist = DbUp.upTable("mc_extend_info_star").dataSqlList(ssql, new MDataMap());
		
		if (slist != null && slist.size() > 0) {
			for (int i = 0; i < slist.size(); i++) {
				// 没有完善个人资料
				if (slist.get(i).get("member_sex") == null || slist.get(i).get("member_sex").toString().equals("") || slist.get(i).get("birthday").toString().equals("") || slist.get(i).get("birthday") == null) {
					
					// 推送表中插入一条消息
					
					MDataMap dbDataMap = DbUp.upTable("nc_sys_push_configure").one("configure_code","TS449746660008","status","449747090001","app_code","SI2013");
					
					if(dbDataMap!=null && !dbDataMap.isEmpty()){
						
						String start = dbDataMap.get("push_time_start").toString();
						
						String end = dbDataMap.get("push_time_end").toString();
						
						String now = DateUtil.getSysTimeString();
						
						int num1 = now.compareTo(start);
						
						int num2 = end.compareTo(now);
						
						Boolean flag = num1>=0 && num2>=0;
						
						if(start.equals("全天") || flag ){
							
							String content = dbDataMap.get("comment").toString();
							
							MDataMap map = new MDataMap();

							map.inAllValues("accept_member",slist.get(i).get("member_code").toString(),"comment", content , "push_time",DateUtil.getSysDateTimeString(), "jump_type", "2","jump_position", "8", "push_status","4497465000070001", "create_time",DateUtil.getSysDateTimeString(), "app_code","SI2013");
							
							DbUp.upTable("nc_comment_push_system").dataInsert(map);
						}
						
					}

				}
			}
		}

	}

	// 测试专用
	public static void main(String[] args) {
		CompleteInformationForBeauty completeInformation = new CompleteInformationForBeauty();
		completeInformation.doExecute(null);
	}
}
