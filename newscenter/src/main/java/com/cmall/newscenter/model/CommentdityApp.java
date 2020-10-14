package com.cmall.newscenter.model;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.cmall.membercenter.model.MemberInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
/**
 * 评论类
 * @author shiyz
 * date 2014-7-4
 * @version 1.0
 */
public class CommentdityApp {
	
	@ZapcomApi(value="评论id")
	private String id = "";
	
	@ZapcomApi(value="评论人")
	private MemberInfo user = new MemberInfo();
	
	@ZapcomApi(value="回复人")
	private MemberInfo reply = new MemberInfo();
	
	@ZapcomApi(value="评论内容")
	private String text = "";
	
	@ZapcomApi(value="图片")
	private List<CommentdityAppPhotos> photos = new ArrayList<CommentdityAppPhotos>();
	
	@ZapcomApi(value="创建时间")
	private String created_at = "";
	
	@ZapcomApi(value="审核状态",verify={ "in=4497172100030001,4497172100030002,4497172100030003" })
	private BigInteger state = new BigInteger("0");
	

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


	public List<CommentdityAppPhotos> getPhotos() {
		return photos;
	}

	public void setPhotos(List<CommentdityAppPhotos> photos) {
		this.photos = photos;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public BigInteger getState() {
		return state;
	}

	public void setState(BigInteger state) {
		this.state = state;
	}
	
	
}
