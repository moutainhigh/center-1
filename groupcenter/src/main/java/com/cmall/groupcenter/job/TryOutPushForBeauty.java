package com.cmall.groupcenter.job;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.cmall.productcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/***
 * 
 * 推送提醒写试用报告   满足条件：试用商品收货7天      每天10点执行一次
 * 
 * @author yangrong
 * 
 */
public class TryOutPushForBeauty extends RootJob {

	public void doExecute(JobExecutionContext context) {
		
		//七天之前的年月日
		java.sql.Timestamp timestamp = DateUtil.getSysDateTimestamp();
		String sysFormat = "yyyy-MM-dd"; // 年/月/日
		SimpleDateFormat sysDateTime = new SimpleDateFormat(sysFormat);
		String seventime = sysDateTime.format(DateUtil.addDays(timestamp, -7));

		// 所有满足条件的订单信息 (订单类型=试用订单     订单状态=已发货)
		String sql = "SELECT * from oc_orderinfo WHERE create_time like '"+seventime+"%' and order_type='449715200003' and seller_code in('SI2007','SI2013')  and order_status = '4497153900010004'";
		List<Map<String, Object>> list = DbUp.upTable("oc_orderinfo").dataSqlList(sql, new MDataMap());
		
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				
				Map<String, Object> orderMap = list.get(i);
				//订单编号
				String order_code = orderMap.get("order_code").toString();
				
				//订单创建时间(这个时间的试用商品肯定是有效的)
				String create_time = orderMap.get("create_time").toString();
				
				//买家编号
				String buyer_code = orderMap.get("buyer_code").toString();
				
				//查出活动编号（一个试用订单只对应一个活动）   活动类型=试用商品=449715400005
				String sqlstring = "SELECT * from oc_order_activity WHERE order_code = '"+order_code+"' and activity_type='449715400005'";
				Map<String, Object> map = DbUp.upTable("oc_order_activity").dataSqlOne(sqlstring, new MDataMap());
				String activity_code = "";
				String sku_code = "";
				if(map != null){
					activity_code = map.get("activity_code").toString();
					sku_code = map.get("sku_code").toString();
				}
				
				//查出该活动下该sku的结束时间
				String sssql = "SELECT * from oc_tryout_products WHERE app_code in('SI2007','SI2013') and activity_code = '"+activity_code+"' and sku_code = '"+sku_code+"' and start_time<='"+create_time+"'<=end_time";
				Map<String, Object> Datamap = DbUp.upTable("oc_tryout_products").dataSqlOne(sssql, new MDataMap());
				
				if(Datamap!=null){
					
					String end_time= Datamap.get("end_time").toString();
					String is_freeShipping = Datamap.get("is_freeShipping").toString();
					
					String jump_position = sku_code+","+end_time;
					
					if(is_freeShipping.equals("449746930002")){       //付邮试用
						
						//推送表中插入一条消息
						
						MDataMap dbDataMap = new MDataMap();
						if(Datamap.get("app_code").toString().equals("SI2007")){
							
							 dbDataMap = DbUp.upTable("nc_sys_push_configure").one("configure_code","TS449746660002","status","449747090001","app_code","SI2007");
							
						}else if(Datamap.get("app_code").toString().equals("SI2013")){
							
							 dbDataMap = DbUp.upTable("nc_sys_push_configure").one("configure_code","TS449746660002","status","449747090001","app_code","SI2013");
								
						}
							
						if(dbDataMap!=null && !dbDataMap.isEmpty()){
							
							String start = dbDataMap.get("push_time_start").toString();
							
							String end = dbDataMap.get("push_time_end").toString();
							
							String now = DateUtil.getSysTimeString();
							
							int num1 = now.compareTo(start);
							
							int num2 = end.compareTo(now);
							
							Boolean flag = num1>=0 && num2>=0;
							
							if(start.equals("全天") || flag ){
								
								String content = dbDataMap.get("comment").toString();
								
								MDataMap insertmap = new MDataMap();

								insertmap.inAllValues("accept_member",buyer_code,"comment", content, "push_time",DateUtil.getSysDateTimeString(), "jump_type", "7","jump_position", jump_position, "push_status","4497465000070001", "create_time",DateUtil.getSysDateTimeString(), "app_code",Datamap.get("app_code").toString());
								
								DbUp.upTable("nc_comment_push_system").dataInsert(insertmap);
							}
							
						}
						
					}else{                                       //免费试用
						
						//推送表中插入一条消息
						
						MDataMap dbDataMap = new MDataMap();
						if(Datamap.get("app_code").toString().equals("SI2007")){
							
							 dbDataMap = DbUp.upTable("nc_sys_push_configure").one("configure_code","TS449746660002","status","449747090001","app_code","SI2007");
							
						}else if(Datamap.get("app_code").toString().equals("SI2013")){
							
							 dbDataMap = DbUp.upTable("nc_sys_push_configure").one("configure_code","TS449746660002","status","449747090001","app_code","SI2013");
								
						}
						
						if(dbDataMap!=null && !dbDataMap.isEmpty()){
							
							String start = dbDataMap.get("push_time_start").toString();
							
							String end = dbDataMap.get("push_time_end").toString();
							
							String now = DateUtil.getSysTimeString();
							
							int num1 = now.compareTo(start);
							
							int num2 = end.compareTo(now);
							
							Boolean flag = num1>=0 && num2>=0;
							
							if(start.equals("全天") || flag ){
								
								String content = dbDataMap.get("comment").toString();
								
								MDataMap insertmap = new MDataMap();

								insertmap.inAllValues("accept_member",buyer_code,"comment", content, "push_time",DateUtil.getSysDateTimeString(), "jump_type", "5","jump_position", jump_position, "push_status","4497465000070001", "create_time",DateUtil.getSysDateTimeString(), "app_code",Datamap.get("app_code").toString());
								
								DbUp.upTable("nc_comment_push_system").dataInsert(insertmap);
							}
							
						}
					}
				}
				
			}
		}

	}

	// 测试专用
	public static void main(String[] args) {
		TryOutPushForBeauty tryOutPush = new TryOutPushForBeauty();
		tryOutPush.doExecute(null);
	}
	
	
}
