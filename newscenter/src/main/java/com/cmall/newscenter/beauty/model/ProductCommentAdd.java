package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 商品评论列表类输入
 * @author houwen
 * date 2014-08-21
 * @version 1.0
 */
public class ProductCommentAdd {
	
	@ZapcomApi(value="app编号",remark="app编号",demo="SI2001",require=1)
	private String app_code  = "";

	@ZapcomApi(value="app名称",remark="app名称",demo="123456抱抱熊",require=1)
	private String app_name  = "";
	
	@ZapcomApi(value="sku编号",remark="sku编号",demo="8019404046",require=1)
	private String sku_code = "" ;
	
	@ZapcomApi(value="sku名称",remark="sku名称",demo="12Q2抱抱熊亲子带604E浅蓝",require=1)
	private String sku_name = "";
	
	@ZapcomApi(value="商品编号",remark="商品编号",demo="8016200024",require=1)
	private String product_code  = "";

	@ZapcomApi(value="商品名称",remark="app名称",demo="12Q2抱抱熊婴儿学步带",require=1)
	private String product_name  = "";
	
	@ZapcomApi(value="评论内容",remark="评论内容",demo="good",require=1)
	private String comment_content = "";

	@ZapcomApi(value="评论人登录账号",remark="评论人登录账号",demo="123456",require=1)
	private String member_code = "";

	@ZapcomApi(value="评论时间",remark="app名称",demo="123456",require=1)
	private String comment_time = "";
	
	@ZapcomApi(value="印象标签",remark="app名称",demo="123456",require=1)
	private String label = "";
	
	@ZapcomApi(value="审核状态",remark="审核状态",demo="449746680002",require=1)
	private String review_status = "";
	
	@ZapcomApi(value="状态",remark="状态",demo="449746670001",require=1)
	private String status = "";

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

	public String getMember_code() {
		return member_code;
	}

	public void setMember_code(String member_code) {
		this.member_code = member_code;
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

	public String getApp_name() {
		return app_name;
	}

	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}

	public String getProduct_code() {
		return product_code;
	}

	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getReview_status() {
		return review_status;
	}

	public void setReview_status(String review_status) {
		this.review_status = review_status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
