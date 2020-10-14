package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 发布帖子输入类
 * @author houwen
 * date 2014-08-26
 * @version 1.0
 */
public class PostsAddInput extends RootInput {


	@ZapcomApi(value="选择标签",remark="选择标签",demo="化妆品",require=1)
	private String post_label = "";
	
	@ZapcomApi(value="标题",remark="标题",demo="XX化妆品太好用了",require=1)
	private String post_title = "" ;
	
	@ZapcomApi(value="正文",remark="正文",demo="XX化妆品太好用了",require=1)
	private String post_content = "";
	
	@ZapcomApi(value="图片",remark="图片",demo="http://8016200024.img")
	private String post_img  = "";

	@ZapcomApi(value="商品id",remark="商品id",demo="8019406881")
	private String product_code  = "";

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

	public String getPost_img() {
		return post_img;
	}

	public void setPost_img(String post_img) {
		this.post_img = post_img;
	}

	public String getProduct_code() {
		return product_code;
	}

	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}
	
}
