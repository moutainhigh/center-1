package com.cmall.newscenter.model;

import com.cmall.membercenter.model.MemberInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 系统消息类
 * 2014/07/21
 * @author liqiang
 */
public class SystemMessage {

	@ZapcomApi(value = "系统消息编码",remark="1")
	private String id  = "";
	
	@ZapcomApi(value = "发送人",remark="李四")
	private MemberInfo user = new MemberInfo();
	
	@ZapcomApi(value = "文字",remark="文字")
	private String text  = "";
	
	@ZapcomApi(value = "链接地址",remark="http://")
	private String link  = "";

	@ZapcomApi(value = "已读-1，未读-0",remark="1",demo= "0,1")
	private int read ;

	@ZapcomApi(value = "创建时间",remark="2009/07/07 21:51:22")
	private String created_at  = "";

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
	
	
}
