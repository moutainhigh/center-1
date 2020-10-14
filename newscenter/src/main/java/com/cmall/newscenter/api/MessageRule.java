package com.cmall.newscenter.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 消息相关模板
 * @author yangrong
 */
public class MessageRule{
	
	/**
	 * 回复消息模板
	 * @param 
	 */
	//参数  ————回复用户 编号     评论用户编号   内容   评论来源
	public static void MessageReplyRule(String replyName,String user,String content,String source,String manage_code) {

		MDataMap mInsertMap = new MDataMap();
		
		mInsertMap.put("message_code",WebHelper.upCode("XX"));
		
		mInsertMap.put("member_code",replyName);
		
		mInsertMap.put("member_send", user);
		
		mInsertMap.put("old_comment", source);
		
		mInsertMap.put("manage_code", manage_code);
		
		mInsertMap.put("message_type", "449746640002");
		
	/*	//查出回复用户 信息                           
		MDataMap mReplyUserMap = DbUp.upTable("mc_extend_info_star").one("member_code",replyName);
		
		//查出评论用户 信息                           
		MDataMap mUserMap = DbUp.upTable("mc_extend_info_star").one("member_code",user);*/
		
		mInsertMap.put("message_info",content+".回复我的评论"+'"'+source+'"');
	
		DbUp.upTable("nc_message_info").dataInsert(mInsertMap);
	}
	
	
	/**
	 * 嘉玲回复消息模板
	 * @param 
	 */
	//参数  ————回复用户 编号     评论用户编号   内容   评论来源
	public static void MessageReplyTemplate(String replyName,String user,String content,String source,String manage_code,String create_time,int url_type,String url_id) {

		MDataMap mInsertMap = new MDataMap();
		
		mInsertMap.put("message_code",WebHelper.upCode("XX"));
		
		mInsertMap.put("member_code",replyName);
		
		mInsertMap.put("member_send", user);
		
		mInsertMap.put("old_comment", source);
		
		mInsertMap.put("manage_code", manage_code);
		
		mInsertMap.put("message_type", "449746640002");
		
		mInsertMap.put("create_time", create_time);
		
	/*	//查出回复用户 信息                           
		MDataMap mReplyUserMap = DbUp.upTable("mc_extend_info_star").one("member_code",replyName);
		
		//查出评论用户 信息                           
		MDataMap mUserMap = DbUp.upTable("mc_extend_info_star").one("member_code",user);*/
		
		mInsertMap.put("message_info",content);
		
		mInsertMap.put("url_type", String.valueOf(url_type));
		
		mInsertMap.put("url_id", url_id);
	
		DbUp.upTable("nc_message_info").dataInsert(mInsertMap);
	}
	
	/**
	 * 报名消息模板
	 * @param 
	 */
	//参数  ————活动code  活动标题     报名人数     报名时间   发起人编号  报名人编号 
	public static void MessageApplyRule(String id,String title,int count,String time,String user,String BMuser,String manage_code) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
		//粉丝头
		MDataMap mInsertMap = new MDataMap();
		
		mInsertMap.put("info_code", id);
		
		mInsertMap.put("message_code",WebHelper.upCode("XX"));
		
		mInsertMap.put("member_send", user);//发送者
		
		mInsertMap.put("create_time", df.format(new Date()));
		
		mInsertMap.put("send_time", time);
		
		mInsertMap.put("message_type", "449746640003");
		
		mInsertMap.put("activity_type", "4497466400030002");
		
		mInsertMap.put("message_info",title+"报名消息已有"+count+"人报名");
		
		mInsertMap.put("manage_code", manage_code);
		
		DbUp.upTable("nc_message_info").dataInsert(mInsertMap);
		
		//报名者
		MDataMap mInsertMap2 = new MDataMap();
		
		mInsertMap2.put("message_code",WebHelper.upCode("XX"));
		
		mInsertMap2.put("info_code", id);
		
		mInsertMap2.put("member_send", BMuser);//回复者
		
		mInsertMap2.put("create_time", df.format(new Date()));
		
		mInsertMap2.put("send_time", time);
		
		mInsertMap2.put("message_type", "449746640003");
		
		mInsertMap2.put("activity_type", "4497466400030002");
		
		mInsertMap2.put("message_info","亲爱的，”"+title+"“报名成功，请注意合理安排自己的时间，不要错过重要时刻哦！");
	
		
		mInsertMap2.put("manage_code", manage_code);
		
		DbUp.upTable("nc_message_info").dataInsert(mInsertMap2);
	}
	
	/**
	 * 活动取消消息模板
	 * @param   
	 */
	//参数  ————活动id   活动时间
	public static void MessageActivityRule(String id,String time,String manage_code) {
		
		MDataMap mWhereMap = new MDataMap();
		
		mWhereMap.put("info_code", id);
		
		MPageData mPageData = DataPaging.upPageData("nc_registration", "", "", mWhereMap,new PageOption());

		for( MDataMap mDataMap: mPageData.getListData()){
			
			MDataMap mInsertMap = new MDataMap();
			
			mInsertMap.put("message_code",WebHelper.upCode("XX"));
			
			mInsertMap.put("member_send", mDataMap.get("info_member"));
			
			mInsertMap.put("message_type", "449746640003");
			
			mInsertMap.put("create_time", FormatHelper.upDateTime());
			
			mInsertMap.put("activity_type", "4497466400030001");
			
			mInsertMap.put("info_code", id);
			
			mInsertMap.put("manage_code", manage_code);
			
			mInsertMap.put("message_info","亲爱的，有点伤感，"+time+"的"+mDataMap.get("info_nickname")+"取消。请大家做好心理准备，安排好自己的时间。嘉玲在此向大家真诚道歉！");
		
			DbUp.upTable("nc_message_info").dataInsert(mInsertMap);
		}
	}
	
	/**
	 * 活动变更消息模板
	 * @param  
	 */
	//参数  ————   活动id   原内容   修改内容  
	public static void MessageActivityChangeRule(String id,String oldContent,String newContent,String manage_code) {
		
		MDataMap mWhereMap = new MDataMap();
		
		mWhereMap.put("info_code", id);
		
		MPageData mPageData = DataPaging.upPageData("nc_registration", "", "", mWhereMap,new PageOption());

		for( MDataMap mDataMap: mPageData.getListData()){
			
			MDataMap mInsertMap = new MDataMap();
			
			mInsertMap.put("message_code",WebHelper.upCode("XX"));
			
			mInsertMap.put("info_code", id);
			
			mInsertMap.put("member_send", mDataMap.get("info_member"));
			
			mInsertMap.put("message_type", "449746640003");
			
			mInsertMap.put("activity_type", "4497466400030001");
			
			mInsertMap.put("create_time", FormatHelper.upDateTime());
			
			mInsertMap.put("manage_code", manage_code);
			
			mInsertMap.put("message_info","亲爱的，"+mDataMap.get("info_nickname")+oldContent+"，改为'"+newContent+"'，请大家注意合理安排自己的时间，不要错过重要时刻哦！");
		
			DbUp.upTable("nc_message_info").dataInsert(mInsertMap);
		}
	}

		
}
