package com.cmall.groupcenter.job;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.productcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.rootweb.RootJob;

/***
 * 
 * 敏感词过滤          
 * 满足条件：与敏感词匹配的内容不展示
 * 每五分钟执行一次
 * @author yangrong
 *
 */
public class JobSensitiveForBeauty extends RootJob {
	
	public void doExecute(JobExecutionContext context) {
		
		//查出所有需要过滤敏感词的表的信息
		String sql="SELECT * from nc_sensitive_relation";
		 List<Map<String, Object>> list=DbUp.upTable("nc_sensitive_relation").dataSqlList(sql, new MDataMap());
		 if(list!=null&&list.size()>0){
			 for (Map<String, Object> map : list) {
				 
				 String tablename = map.get("table_name").toString();        //表名
				 String checkfiled = map.get("check_filed").toString();      //过滤的字段
				 String statusfiled = map.get("status_filed").toString();    //状态字段
				 String statusvalue = map.get("status_value").toString();    //非法状态值
				 String checkzid = map.get("check_zid").toString();          //上次过滤的最大zid
				 String appCode = map.get("app_code").toString();
				 
				 //取到过滤字段值
				 
				 String sqlwhere="SELECT uid,zid,"+checkfiled+" from "+tablename +" where app_code='"+appCode+"' and zid > '"+checkzid+"'";
				 if(tablename.equals("nc_order_evaluation")){
					 sqlwhere = "SELECT uid,zid,"+checkfiled+" from "+tablename +" where manage_code='"+appCode+"' and zid > '"+checkzid+"'";
				 } 
				 List<Map<String, Object>> mapList =DbUp.upTable(tablename).dataSqlList(sqlwhere, new MDataMap());				 
				 
				 if(mapList!=null&&mapList.size()>0){
					 for (Map<String, Object> maps : mapList) {
						 //过滤之前检查过的
						 if(Integer.valueOf(maps.get("zid").toString())>Integer.valueOf(checkzid)){
							 if(StringUtils.isNotBlank(maps.get(checkfiled).toString())){
								 String datawhere = " SELECT * from nc_sensitive_word where sensitive_word in ("+getSubStr(maps.get(checkfiled).toString())+") ";
							     List<Map<String, Object>> sensitiveList = DbUp.upTable("nc_sensitive_word").dataSqlList(datawhere, null);
							     //过滤的字段值如包含敏感词  将状态改为非法状态值
							     if(sensitiveList != null && sensitiveList.size() > 0){//包含敏感词
							    	 String sensitive = sensitiveList.get(0).get("sensitive_word").toString();//包含的敏感词
									 MDataMap dataMap = new MDataMap();
									 dataMap.put(statusfiled, statusvalue);
									 dataMap.put("zid", maps.get("zid").toString());
									 dataMap.put("uid", maps.get("uid").toString());
									 DbUp.upTable(tablename).update(dataMap);
									 //如果有敏感词 添加一条系统消息提示（帖子）
									 if(tablename.equals("nc_posts")){
										 
										String whString = "select post_code,publisher_code,post_title,post_type,app_code from nc_posts where uid ='"+maps.get("uid").toString()+"'"+" and app_code='"+appCode+"'";
										Map<String, Object> postMap = DbUp.upTable("nc_posts").dataSqlOne(whString, null);
										String messageCode = WebHelper.upCode("XX");
										String dateTime = DateUtil.getSysDateTimeString();
										if(postMap.get("post_type").toString().equals("449746780002")){
											
											DbUp.upTable("nc_comment_log").insert("publisher_code",postMap.get("publisher_code").toString(),"comment_title",postMap.get("post_title").toString(),"operate_status","449747020003","operate_time",DateUtil.getSysDateTimeString(),"operate_type","449747010002","operate_id",postMap.get("post_code").toString(),"sensitive_word",sensitive,"app_code",postMap.get("app_code").toString());
											DbUp.upTable("nc_message_info").insert("message_code", messageCode,"message_info","您的帖子'"+postMap.get("post_title").toString()+"'的追贴包含敏感词'"+sensitive+"'，已被删除","message_type","449746910002","member_send",postMap.get("publisher_code").toString(),"send_time",dateTime,"manage_code",postMap.get("app_code").toString());
											DbUp.upTable("nc_system_message").insert("message_code", messageCode,"message_info","您的帖子'"+postMap.get("post_title").toString()+"'的追贴包含敏感词'"+sensitive+"'，已被删除","message_type","449746910002","member_send",postMap.get("publisher_code").toString(),"send_time",dateTime,"manage_code",postMap.get("app_code").toString());

										}else{
											DbUp.upTable("nc_comment_log").insert("publisher_code",postMap.get("publisher_code").toString(),"comment_title",postMap.get("post_title").toString(),"operate_status","449747020003","operate_time",DateUtil.getSysDateTimeString(),"operate_type","449747010001","operate_id",postMap.get("post_code").toString(),"sensitive_word",sensitive,"app_code",postMap.get("app_code").toString());
											DbUp.upTable("nc_message_info").insert("message_code", messageCode,"message_info","您的帖子'"+postMap.get("post_title").toString()+"'包含敏感词'"+sensitive+"'，已被删除","message_type","449746910002","member_send",postMap.get("publisher_code").toString(),"send_time",dateTime,"manage_code",postMap.get("app_code").toString());
											DbUp.upTable("nc_system_message").insert("message_code", messageCode,"message_info","您的帖子'"+postMap.get("post_title").toString()+"'包含敏感词'"+sensitive+"'，已被删除","message_type","449746910002","member_send",postMap.get("publisher_code").toString(),"send_time",dateTime,"manage_code",postMap.get("app_code").toString());

										}
										
									 }
									 //如果有敏感词 添加一条系统消息提示（帖子评论）
									 if(tablename.equals("nc_posts_comment")){
										 
											String whString = "select publisher_code,post_title,comment_code,app_code from nc_posts_comment where uid ='"+maps.get("uid").toString()+"'"+" and app_code='"+appCode+"'";
											Map<String, Object> postMap = DbUp.upTable("nc_posts").dataSqlOne(whString, null);
											//String messageCode = WebHelper.upCode("XX");
											/*DbUp.upTable("nc_message_info").insert("message_code", messageCode,"message_info","您的评论"+checkString+"包含敏感词，已被删除","message_type","449746910002","member_send",postMap.get("publisher_code").toString(),"send_time",dateTime,"manage_code","SI2007");
											DbUp.upTable("nc_system_message").insert("message_code", messageCode,"message_info","您的评论"+checkString+"包含敏感词，已被删除","message_type","449746910002","member_send",postMap.get("publisher_code").toString(),"send_time",dateTime,"manage_code","SI2007");*/
											DbUp.upTable("nc_comment_log").insert("publisher_code",postMap.get("publisher_code").toString(),"comment_title",postMap.get("post_title").toString(),"operate_status","449747020003","operate_time",DateUtil.getSysDateTimeString(),"operate_type","449747010003","operate_id",postMap.get("comment_code").toString(),"sensitive_word",sensitive,"app_code",postMap.get("app_code").toString());
											
									 }
								 }
							 }
						 }
						 //每过滤一次都把上次过滤的最大zid和更新时间更新
						 MDataMap whereMap = new MDataMap();
						 whereMap.put("uid", map.get("uid").toString());
						 whereMap.put("zid", map.get("zid").toString());
						 whereMap.put("check_zid", maps.get("zid").toString());
						 whereMap.put("update_time",DateUtil.getSysDateTimeString());
						 DbUp.upTable("nc_sensitive_relation").update(whereMap);
					 }
				}
				 
			}
		 }
		 
	}
	
	
	/**
	 * 获取内容的所有子串,例:"'1','2','12'"
	 * @param text
	 * @return
	 */
	public String getSubStr(String text){
		String str = "";
		for(int i = text.length();i>0;i--){
			try{
				int j = 0 ;
				int z = i;
				while(z<=50 && z-j > 1){
					str += "'"+text.substring(j++,z++)+"',";
				}
			}catch(Exception e){
				continue;
			}
		}
		if(StringUtils.isNotBlank(str)){
			str = str.substring(0,str.length()-1);
		}else{
			str = "''";
		}
		return str;
	}
	
	//测试专用
	public static void main(String[] args) {
		JobSensitiveForBeauty cancelOrder =new JobSensitiveForBeauty();
		cancelOrder.doExecute(null);
	}
}

