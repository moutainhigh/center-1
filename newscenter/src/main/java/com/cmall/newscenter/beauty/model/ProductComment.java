package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.productcenter.model.PicAllInfo;
import com.cmall.productcenter.model.PicInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 商品评论列表类
 * @author houwen
 * date 2014-08-25
 * @version 1.0
 */
public class ProductComment {
	
	@ZapcomApi(value="app编号")
	private String app_code  = "";

	@ZapcomApi(value="sku编号")
	private String sku_code = "" ;
	
	@ZapcomApi(value="sku名称")
	private String sku_name = "";
	
	@ZapcomApi(value="评论内容")
	private String comment_content = "";

	@ZapcomApi(value="评论人信息")
	private Commentator commentator =  new Commentator() ;
	
	@ZapcomApi(value="评论时间")
	private String comment_time = "";

	@ZapcomApi(value="印象标签")
	private String label = "";
	
	@ZapcomApi(value="图片")
	private List<PicAllInfo> picInfos = new ArrayList<PicAllInfo>();
	
	public String getApp_code() {
		return app_code;
	}

	public void setApp_code(String app_code) {
		this.app_code = app_code;
	}

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public String getSku_name() {
		return sku_name;
	}

	public void setSku_name(String sku_name) {
		this.sku_name = sku_name;
	}

	public String getComment_content() {
		return comment_content;
	}

	public void setComment_content(String comment_content) {
		this.comment_content = comment_content;
	}

	public Commentator getCommentator() {
		return commentator;
	}

	public void setCommentator(Commentator commentator) {
		this.commentator = commentator;
	}

	public String getComment_time() {
		return comment_time;
	}

	public void setComment_time(String comment_time) {
		this.comment_time = comment_time;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<PicAllInfo> getPicInfos() {
		return picInfos;
	}

	public void setPicInfos(List<PicAllInfo> picInfos) {
		this.picInfos = picInfos;
	}

}
