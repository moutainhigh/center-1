package com.cmall.groupcenter.job;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.cmall.productcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/***
 * 
 * 根据几号或者周几发推送    每天00点执行一次
 * 
 * @author yangrong
 * 
 */
public class JobAddPushMessage extends RootJob {

	public void doExecute(JobExecutionContext context) {
		
		//只查询启用的
		String sql = "SELECT * from nc_comment_push WHERE STATUS ='449747090001'";   
		List<Map<String, Object>> list = DbUp.upTable("nc_comment_push").dataSqlList(sql, null);
		
		if(list !=null && list.size()!=0){
			for (int i = 0; i < list.size(); i++) {
				
				Map<String, Object> map = list.get(i);
				
				if(map.get("cys_time")!=null && !map.get("cys_time").equals("")){
					
					String cys = map.get("cys_time").toString();
					
					String week = DateUtil.getSystemWeekdayString();          //今天星期几  
					
					Calendar cale = Calendar.getInstance();         //可以对每个时间域单独修改

					int date = cale.get(Calendar.DATE); 
					
					String day = String.valueOf(date)+"号";                 //今天几号
					
					if(week.equals("星期一")){
						
						if(cys.equals("周一")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(4,12);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
					}else if (week.equals("星期二")){
						
						if(cys.equals("周二")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(4,12);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
					}else if(week.equals("星期三")){
						
						if(cys.equals("周三")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(4,12);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
					}else if(week.equals("星期四")){
						
						if(cys.equals("周四")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(4,12);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("push_time", time);
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(week.equals("星期五")){
						
						if(cys.equals("周五")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(4,12);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(week.equals("星期六")){
						
						if(cys.equals("周六")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(4,12);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(week.equals("星期日")){
						
						if(cys.equals("周日")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(4,12);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
					}
					
					if(day.equals("1号")){
						
						if(cys.equals("1号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(5,13);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("2号")){
						
						if(cys.equals("2号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(5,13);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("3号")){
						
						if(cys.equals("3号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(5,13);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("4号")){
						
						if(cys.equals("4号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(5,13);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("5号")){
						
						if(cys.equals("5号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(5,13);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("6号")){
						
						if(cys.equals("6号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(5,13);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("7号")){
						
						if(cys.equals("7号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(5,13);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("8号")){
						
						if(cys.equals("8号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(5,13);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("9号")){
						
						if(cys.equals("9号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(5,13);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("10号")){  
						
						if(cys.equals("10号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(6,14);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("11号")){
						
						if(cys.equals("11号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(6,14);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("12号")){
						
						if(cys.equals("12号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(6,14);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("13号")){
						
						if(cys.equals("13号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(6,14);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("14号")){
						
						if(cys.equals("14号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(6,14);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}	
						
					}else if(day.equals("15号")){
						
						if(cys.equals("15号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(6,14);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("16号")){
						
						if(cys.equals("16号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(6,14);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("17号")){
						
						if(cys.equals("17号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(6,14);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("18号")){
						
						if(cys.equals("18号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(6,14);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("19号")){
						
						if(cys.equals("19号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(6,14);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("20号")){
						
						if(cys.equals("20号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(6,14);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("21号")){
						
						if(cys.equals("21号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(6,14);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("22号")){
						
						if(cys.equals("22号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(6,14);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("23号")){
						
						if(cys.equals("23号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(6,14);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("24号")){
						
						if(cys.equals("24号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(6,14);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("25号")){
						
						if(cys.equals("25号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(6,14);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("26号")){
						
						if(cys.equals("26号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(6,14);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("27号")){
						
						if(cys.equals("27号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(6,14);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("28号")){
						
						if(cys.equals("28号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(6,14);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("29号")){
						
						if(cys.equals("29号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(6,14);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("30号")){
						
						if(cys.equals("30号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(6,14);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}else if(day.equals("31号")){
						
						if(cys.equals("31号")){
							
							String a = DateUtil.getSysDateString();
							
							String b = map.get("send_time").toString().substring(6,14);
							
							String time = a + " "+ b;
							
							MDataMap datamap = new MDataMap();
							
							datamap.put("push_time", time);
							
							datamap.put("push_status", "4497465000070001");
							
							datamap.put("uid", map.get("uid").toString());
							
							DbUp.upTable("nc_comment_push").dataUpdate(datamap, "push_time,push_status", "uid");
							
						}
						
					}
				}
				
				
			}
		}
	}

	// 测试专用
	public static void main(String[] args) {
		
		JobAddPushMessage jobAddPushMessage = new JobAddPushMessage();
		jobAddPushMessage.doExecute(null);
		
	}
}
