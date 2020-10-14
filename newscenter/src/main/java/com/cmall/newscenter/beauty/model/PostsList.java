package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.productcenter.model.PicAllInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;

/**
 * 帖子列表类输出
 * @author houwen
 * date 2014-08-26
 * @version 1.0
 */
public class PostsList extends RootResult{
	
/*	@ZapcomApi(value="用户ID")
	private String publisher_code  = "";*/
	
	@ZapcomApi(value = "发布人信息列表")
	private PostPublisherList postPublisherLists = new PostPublisherList();

	@ZapcomApi(value="选择标签")
	private String post_label = "";
	
	@ZapcomApi(value="标题")
	private String post_title = "" ;
	
	@ZapcomApi(value="正文")
	private String post_content = "";
	
	@ZapcomApi(value="原图片",remark="1.0版本返回图片字段名称")
	private String post_img  = "";
	
	@ZapcomApi(value="图片")
	private List<PicAllInfo> picInfos = new ArrayList<PicAllInfo>();
	
	@ZapcomApi(value="帖子ID")
	private String post_code  = "";
	
	@ZapcomApi(value="精华帖",remark="是：449746770001；否：449746770002")
	private String issessence  = "";

	@ZapcomApi(value="官方帖",remark="是：449746760001；否：449746760002")
	private String isofficial  = "";
	
	@ZapcomApi(value="点赞量")
	private String post_praise  = "";
	
	@ZapcomApi(value="浏览量")
	private String post_browse  = "";
	
	@ZapcomApi(value="回复量")
	private int post_count  ;
	
	@ZapcomApi(value="是否火",remark="是：449746880001；否：449746880002")
	private String ishot  ;
	
	@ZapcomApi(value="发布时间",remark="1天前")
	private String publish_time  = "";
	
	@ZapcomApi(value="url")
	private String linkUrl = "";
	
	
	public String getPost_label() {
		return post_label;
	}

	public void setPost_label(String post_label) {
		this.post_label = post_label;
	}


	public String getPost_title() {
		return post_title;
	}

	public void setPost_title(String post_title) {
		this.post_title = post_title;
	}

	public String getPost_content() {
		return post_content;
	}

	public void setPost_content(String post_content) {
		this.post_content = post_content;
	}

	public List<PicAllInfo> getPicInfos() {
		return picInfos;
	}

	public void setPicInfos(List<PicAllInfo> picInfos) {
		this.picInfos = picInfos;
	}

	public String getPost_code() {
		return post_code;
	}

	public void setPost_code(String post_code) {
		this.post_code = post_code;
	}


	public String getIssessence() {
		return issessence;
	}

	public void setIssessence(String issessence) {
		this.issessence = issessence;
	}

	public String getIsofficial() {
		return isofficial;
	}

	public void setIsofficial(String isofficial) {
		this.isofficial = isofficial;
	}

	public String getPost_praise() {
		return post_praise;
	}

	public void setPost_praise(String post_praise) {
		this.post_praise = post_praise;
	}

	public String getPost_browse() {
		return post_browse;
	}

	public void setPost_browse(String post_browse) {
		this.post_browse = post_browse;
	}

	public int getPost_count() {
		return post_count;
	}

	public void setPost_count(int post_count) {
		this.post_count = post_count;
	}

	public String getPublish_time() {
		return publish_time;
	}

	public void setPublish_time(String publish_time) {
		this.publish_time = publish_time;
	}

	public String getIshot() {
		return ishot;
	}

	public void setIshot(String ishot) {
		this.ishot = ishot;
	}

	public PostPublisherList getPostPublisherLists() {
		return postPublisherLists;
	}

	public void setPostPublisherLists(PostPublisherList postPublisherLists) {
		this.postPublisherLists = postPublisherLists;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public String getPost_img() {
		return post_img;
	}

	public void setPost_img(String post_img) {
		this.post_img = post_img;
	}
}
