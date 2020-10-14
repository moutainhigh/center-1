package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 消息——》与我相关类
 * 2014/09/16
 * @author houwen
 */
public class AboutMeMessage {

	@ZapcomApi(value = "系统消息编码",remark="1")
	private String message_code  = "";
	
	@ZapcomApi(value = "用户")
	private MessageUser messageUser = new MessageUser();
	
	@ZapcomApi(value = "消息类型",remark="评论帖子:449746920001;赞了帖子:449746920002;赞了评论:449746920003;回复评论:449746920004")
	private String message_type  = "";
	
	@ZapcomApi(value = "消息内容",remark="回复消息")
	private String message_info  = "";

	@ZapcomApi(value = "已读-1，未读-0",remark="1",demo= "0,1")
	private int is_read ;

	@ZapcomApi(value = "创建时间",remark="2009/07/07 21:51:22")
	private String create_time = "";

	@ZapcomApi(value = "被评论标题或内容")
	private String old_comment = "";
	
	@ZapcomApi(value = "帖子Id")
	private String post_code = "";
	
	public String getMessage_code() {
		return message_code;
	}

	public void setMessage_code(String message_code) {
		this.message_code = message_code;
	}

	public String getMessage_type() {
		return message_type;
	}

	public void setMessage_type(String message_type) {
		this.message_type = message_type;
	}

	public String getMessage_info() {
		return message_info;
	}

	public void setMessage_info(String message_info) {
		this.message_info = message_info;
	}

	public int getIs_read() {
		return is_read;
	}

	public void setIs_read(int is_read) {
		this.is_read = is_read;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public MessageUser getMessageUser() {
		return messageUser;
	}

	public void setMessageUser(MessageUser messageUser) {
		this.messageUser = messageUser;
	}

	public String getOld_comment() {
		return old_comment;
	}

	public void setOld_comment(String old_comment) {
		this.old_comment = old_comment;
	}

	public String getPost_code() {
		return post_code;
	}

	public void setPost_code(String post_code) {
		this.post_code = post_code;
	}
	
}
