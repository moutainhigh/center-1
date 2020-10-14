package com.cmall.newscenter.model;

import com.cmall.membercenter.model.MemberInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class ReplyMessage {

	@ZapcomApi(value = "系统消息编码",remark="1")
	private String id = "";
	
	@ZapcomApi(value = "发送人")
	private MemberInfo user = new MemberInfo();
	
	@ZapcomApi(value = "我")
	private MemberInfo reply = new MemberInfo();
	
	@ZapcomApi(value = "文字")
	private String text= "";
	
	@ZapcomApi(value = "已读-1，未读-0",remark="1,0")
	private  int read = 0;
	
	@ZapcomApi(value = "原来的评论内容")
	private String orig_comment = "";
	
	@ZapcomApi(value = "创建时间",remark="2009/07/07 21:51:22")
	private String created_at = "";

	@ZapcomApi(value="回复类型ID")
	private String url="";
	
	@ZapcomApi(value="回复类型")
	private int reply_type = 0;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public MemberInfo getUser() {
		return user;
	}

	public void setUser(MemberInfo user) {
		this.user = user;
	}

	public MemberInfo getReply() {
		return reply;
	}

	public void setReply(MemberInfo reply) {
		this.reply = reply;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getRead() {
		return read;
	}

	public void setRead(int read) {
		this.read = read;
	}

	public String getOrig_comment() {
		return orig_comment;
	}

	public void setOrig_comment(String orig_comment) {
		this.orig_comment = orig_comment;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getReply_type() {
		return reply_type;
	}

	public void setReply_type(int reply_type) {
		this.reply_type = reply_type;
	}

}
