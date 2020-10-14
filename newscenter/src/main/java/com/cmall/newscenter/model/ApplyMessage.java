package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.membercenter.model.MemberInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 报名消息类
 * 2014/07/21
 * @author liqiang
 */
public class ApplyMessage {

	@ZapcomApi(value = "系统消息编码",remark="1")
	private String id = "";
	
	@ZapcomApi(value = "发送人",remark="李四")
	private MemberInfo user = new MemberInfo();
	
	@ZapcomApi(value = "文字",remark="文字")
	private String text ="";
	
	@ZapcomApi(value = "报名id",remark="0")
	private String activity ="";
	
	@ZapcomApi(value = "消息类型",remark="0,1")
	private int activity_message_type ;
	
	
	@ZapcomApi(value = "报名人数",remark="10")
	private  int apply_count  =0;
	
	@ZapcomApi(value = "链接地址",remark="http://")
	private String link  = "";

	@ZapcomApi(value = "已读-1，未读-0",remark="1",demo= "0,1")
	private int read  ;

	@ZapcomApi(value = "创建时间",remark="2009/07/07 21:51:22")
	private String created_at  = "";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getRead() {
		return read;
	}

	public void setRead(int read) {
		this.read = read;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public int getApply_count() {
		return apply_count;
	}

	public void setApply_count(int apply_count) {
		this.apply_count = apply_count;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public int getActivity_message_type() {
		return activity_message_type;
	}

	public void setActivity_message_type(int activity_message_type) {
		this.activity_message_type = activity_message_type;
	}

	public MemberInfo getUser() {
		return user;
	}

	public void setUser(MemberInfo user) {
		this.user = user;
	}

}
