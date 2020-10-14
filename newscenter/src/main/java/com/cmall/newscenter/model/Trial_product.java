package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
/**
 * 试用商品类
 * @author shiyz
 * date 2014-7-4
 * @version 1.0
 */
public class Trial_product {

	@ZapcomApi(value = "商品id")
	String id = "";
	
	@ZapcomApi(value = "单图或多图")
	private List<CommentdityAppPhotos> photos= new ArrayList<CommentdityAppPhotos>();

	@ZapcomApi(value = "标题",demo="海百合XXX面膜")
	private String title ="";
	
	@ZapcomApi(value = "售价",demo="￥198.00")
	private double trial_price ;

	@ZapcomApi(value = "过期时间",demo="2009/07/07 21:51:22")
	private String trial_expires ="";
	
	@ZapcomApi(value = "推荐理由",demo="推荐理由")
	private String reason ="";
	
	@ZapcomApi(value = "介绍文字",demo="介绍介绍介绍介绍")
	private String intro ="";
	
	@ZapcomApi(value = "已申请数量",demo="100")
	private int apply_count ;
	
	@ZapcomApi(value = "申请成功数量",demo="100")
	private int success_count ;
	
	@ZapcomApi(value = "库存量",demo="100")
	private int repo_count ;
	
	@ZapcomApi(value = "图文详情",demo="http://")
	private String detail_url ="";
	
	@ZapcomApi(value = "产品参数",demo="http://")
	private String param_url ="";

	@ZapcomApi(value = "口碑评价")
	private List<CommentdityApp> comments = new ArrayList<CommentdityApp>();

	@ZapcomApi(value="url")
	private String linkUrl = "";

	private String surplusTime="";
	
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

	public List<CommentdityAppPhotos> getPhotos() {
		return photos;
	}

	public void setPhotos(List<CommentdityAppPhotos> photos) {
		this.photos = photos;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public double getTrial_price() {
		return trial_price;
	}

	public void setTrial_price(double trial_price) {
		this.trial_price = trial_price;
	}

	public String getTrial_expires() {
		return trial_expires;
	}

	public void setTrial_expires(String trial_expires) {
		this.trial_expires = trial_expires;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public int getApply_count() {
		return apply_count;
	}

	public void setApply_count(int apply_count) {
		this.apply_count = apply_count;
	}

	public int getSuccess_count() {
		return success_count;
	}

	public void setSuccess_count(int success_count) {
		this.success_count = success_count;
	}

	public int getRepo_count() {
		return repo_count;
	}

	public void setRepo_count(int repo_count) {
		this.repo_count = repo_count;
	}

	public String getDetail_url() {
		return detail_url;
	}

	public void setDetail_url(String detail_url) {
		this.detail_url = detail_url;
	}

	public String getParam_url() {
		return param_url;
	}

	public void setParam_url(String param_url) {
		this.param_url = param_url;
	}

	public List<CommentdityApp> getComments() {
		return comments;
	}

	public void setComments(List<CommentdityApp> comments) {
		this.comments = comments;
	}

	public String getSurplusTime() {
		return surplusTime;
	}

	public void setSurplusTime(String surplusTime) {
		this.surplusTime = surplusTime;
	}
	
	
}
