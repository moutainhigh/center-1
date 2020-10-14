package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 回复帖子输入类
 * @author houwen
 * date 2014-08-26
 * @version 1.0
 */
public class PostsReplyAddInput extends RootInput {


	@ZapcomApi(value="帖子ID",remark="帖子ID",demo="HML140630100001",require=1)
	private String post_code  = "";

	@ZapcomApi(value="正文",remark="正文",demo="XX化妆品太好用了",require=1)
	private String comment_content = "";
	
/*	@ZapcomApi(value="图片",remark="将url放入picOldUrl",demo="")
	private List<PicInfo> photos = new ArrayList<PicInfo>();*/

	@ZapcomApi(value="图片",remark="图片",demo="http://8016200024.img")
	private String post_img  = "";
	
	@ZapcomApi(value="商品id",remark="商品id",demo="8019406881")
	private String product_code  = "";
	
	public String getComment_content() {
		return comment_content;
	}

	public void setComment_content(String comment_content) {
		this.comment_content = comment_content;
	}

	public String getPost_code() {
		return post_code;
	}

	public void setPost_code(String post_code) {
		this.post_code = post_code;
	}

	public String getProduct_code() {
		return product_code;
	}

	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}

	public String getPost_img() {
		return post_img;
	}

	public void setPost_img(String post_img) {
		this.post_img = post_img;
	}

}
