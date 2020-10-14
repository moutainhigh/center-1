package com.cmall.newscenter.beauty.api;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
public class MessageTemplate {


	/**
	 * 惠美丽回复消息模板
	 * 例通知:449746640004 ;小编提醒:449746640005;评论帖子:449746640006;赞了帖子:449746640007;赞了评论:449746640008;回复评论:449746640009
	 * @param 
	 */
	//参数  ————回复用户 编号     评论用户编号   内容   评论来源,消息类型
	
	public static void MessageReplyRule(String postCode,String replyName,String user,String content,String source,String manage_code,String type) {

		MDataMap mInsertMap = new MDataMap();
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
		
		mInsertMap.put("message_code",WebHelper.upCode("XX"));
		
		mInsertMap.put("member_code",replyName);
		
		mInsertMap.put("member_send", user);
		
		mInsertMap.put("old_comment", source);
		
		mInsertMap.put("manage_code", manage_code);
		
		mInsertMap.put("message_type", type);
		
		mInsertMap.put("create_time", df.format(new Date()));
		
		mInsertMap.put("post_code", postCode);
		
	/*	//查出回复用户 信息                           
		MDataMap mReplyUserMap = DbUp.upTable("mc_extend_info_star").one("member_code",replyName);
		
		//查出评论用户 信息                           
		MDataMap mUserMap = DbUp.upTable("mc_extend_info_star").one("member_code",user);*/
		if(type.equals("449746920001")){
			//mInsertMap.put("message_info",content+".评论了你"+'"'+source+'"');
			mInsertMap.put("message_info",content);
		}
		
		if(type.equals("449746920002")){
			mInsertMap.put("message_info",content);
		}
		
		if(type.equals("449746920003")){
			mInsertMap.put("message_info",content);
		}
		
		if(type.equals("449746920004")){
			mInsertMap.put("message_info",content);
		}
	
		DbUp.upTable("nc_message_info").dataInsert(mInsertMap);
	}
}
