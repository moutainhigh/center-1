package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.membercenter.model.MemberInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
/**
 * 活动类
 * @author yangrong
 * date 2014-7-21
 * @version 1.0
 */
public class Activity {

	@ZapcomApi(value="活动信息id",remark="1")
	private String id = "";
	
	@ZapcomApi(value="限制条件")
	private CommentdityAppLimt limit = new CommentdityAppLimt(); 

	@ZapcomApi(value="发起人")
	private MemberInfo user = new MemberInfo(); 

	@ZapcomApi(value="发标题",remark="型人示范潮流点晴饰物",require=1,verify={"minlength=6","maxlength=100"})
	private String title = ""; 

	@ZapcomApi(value="文字",remark="长项链搭配小吊带....",require=1,verify={"minlength=20","maxlength=2000"})
	private String text = ""; 

	@ZapcomApi(value="单图")
	
	private List<CommentdityAppPhotos> photo = new ArrayList<CommentdityAppPhotos>(); 
	
	@ZapcomApi(value="我是否喜欢过，0-否，1-是",remark="1",demo="0,1")
	private int liked = 0; 

	@ZapcomApi(value="多少人喜欢过",remark="10000")
	private int like_count ; 

	@ZapcomApi(value="我是否收藏过，0-否，1-是",remark="1",demo="0,1")
	private int faved = 0; 
	
	@ZapcomApi(value="多少人收藏过",remark="10000")
	private int fav_count ;

	@ZapcomApi(value="我是否分享过，0-否，1-是",remark="1",demo="0,1")
	private int shared = 0; 
	
	@ZapcomApi(value="多少人分享过",remark="10000")
	private int share_count ;

	@ZapcomApi(value="我是否评论过，0-否，1-是",remark="1",demo="0,1")
	private int commented = 0; 
	
	@ZapcomApi(value="多少人评论过",remark="10000")
	private int comment_count ;
	
	
	@ZapcomApi(value="多少人报名过",remark="10000")
	private int apply_count ;

	@ZapcomApi(value="创建时间",remark="2009/07/07 21:51:22",require=1,verify="base=datetime")
	private String created_at = ""; 
	
	@ZapcomApi(value="限制等级",remark="4497465000030001",require=1,verify={ "in=4497465000030001,4497465000030002,4497465000030003,4497465000030004,4497465000030005" })
	private String level = "";
	
	@ZapcomApi(value="限制等级名称",remark="银牌会员")
	private String level_name = "";

	@ZapcomApi(value="我是否可以报名，0-否，1-是",remark="1",demo="0,1")
	private int can_join = 0 ;

	@ZapcomApi(value="我是否报名了，0-否，1-是",remark="1",demo="0,1")
	private int joined = 0 ;

	@ZapcomApi(value="报名时间",remark="1",require=1)
	private String joined_at = "";
	
	@ZapcomApi(value="位置信息",remark="1")
	private Location location = new Location();
	

	@ZapcomApi(value="分享链接")
	private String linkUrl = "";
	
	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public CommentdityAppLimt getLimit() {
		return limit;
	}

	public void setLimit(CommentdityAppLimt limit) {
		this.limit = limit;
	}

	public MemberInfo getUser() {
		return user;
	}

	public void setUser(MemberInfo user) {
		this.user = user;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	
	public List<CommentdityAppPhotos> getPhoto() {
		return photo;
	}

	public void setPhoto(List<CommentdityAppPhotos> photo) {
		this.photo = photo;
	}

	public int getLiked() {
		return liked;
	}

	public void setLiked(int liked) {
		this.liked = liked;
	}

	public int getLike_count() {
		return like_count;
	}

	public void setLike_count(int like_count) {
		this.like_count = like_count;
	}

	public int getFaved() {
		return faved;
	}

	public void setFaved(int faved) {
		this.faved = faved;
	}

	public int getFav_count() {
		return fav_count;
	}

	public void setFav_count(int fav_count) {
		this.fav_count = fav_count;
	}

	public int getShared() {
		return shared;
	}

	public void setShared(int shared) {
		this.shared = shared;
	}

	public int getShare_count() {
		return share_count;
	}

	public void setShare_count(int share_count) {
		this.share_count = share_count;
	}

	public int getCommented() {
		return commented;
	}

	public void setCommented(int commented) {
		this.commented = commented;
	}

	public int getComment_count() {
		return comment_count;
	}

	public void setComment_count(int comment_count) {
		this.comment_count = comment_count;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getLevel_name() {
		return level_name;
	}

	public void setLevel_name(String level_name) {
		this.level_name = level_name;
	}

	public int getCan_join() {
		return can_join;
	}

	public void setCan_join(int can_join) {
		this.can_join = can_join;
	}

	public int getJoined() {
		return joined;
	}

	public void setJoined(int joined) {
		this.joined = joined;
	}

	public String getJoined_at() {
		return joined_at;
	}

	public void setJoined_at(String joined_at) {
		this.joined_at = joined_at;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public int getApply_count() {
		return apply_count;
	}

	public void setApply_count(int apply_count) {
		this.apply_count = apply_count;
	}

	
}
