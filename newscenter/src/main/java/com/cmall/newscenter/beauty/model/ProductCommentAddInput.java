package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 商品评论列表输入类
 * @author houwen
 * date 2014-08-21
 * @version 1.0
 */
public class ProductCommentAddInput extends RootInput {

	
	@ZapcomApi(value="sku编号",remark="sku编号",demo="8019404046",require=1)
	private String sku_code = "" ;
	
	@ZapcomApi(value="评论内容",remark="评论内容",demo="good",require=1)
	private String comment_content = "";

	@ZapcomApi(value="印象标签",remark="印象标签",demo="123456")
	private String label = "";
	
	@ZapcomApi(value="订单编号",remark="订单编号",demo="8019404046",require=1)
	private String order_code = "";
	
	@ZapcomApi(value="图片",remark="图片",demo="http://8016200024.img")
	private String post_img  = "";
	
	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public String getComment_content() {
		return comment_content;
	}

	public void setComment_content(String comment_content) {
		this.comment_content = comment_content;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getOrder_code() {
		return order_code;
	}

	public void setOrder_code(String order_code) {
		this.order_code = order_code;
	}

	public String getPost_img() {
		return post_img;
	}

	public void setPost_img(String post_img) {
		this.post_img = post_img;
	}
}
