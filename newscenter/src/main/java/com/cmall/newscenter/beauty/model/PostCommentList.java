package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.productcenter.model.PicAllInfo;
import com.cmall.productcenter.model.PicInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 回复帖子列表类输出
 * @author houwen
 * date 2014-08-26
 * @version 1.0
 */
public class PostCommentList {
	

	@ZapcomApi(value="评论人信息")
	private  PostPublisherList postPublisherList = new PostPublisherList();
	
	@ZapcomApi(value="楼层数")
	private int comment_floor ;

	@ZapcomApi(value="评论编号")
	private String comment_code ;
	
	@ZapcomApi(value="正文")
	private String post_content = "";
	
	@ZapcomApi(value="评论时间")
	private String publish_time  = "";
	
	@ZapcomApi(value="点赞数")
	private String post_praise  = "";
	
	@ZapcomApi(value="评论类型",remark="0:针对帖子的评论；1：针对帖子评论的评论")
	private String type  = "";
	
	@ZapcomApi(value="是否点赞过",remark="是：449746870001；否：449746870002")
	private String ispraise  = "";
	
	@ZapcomApi(value="被评论人信息")
	private  PostPublisherList publishedList = new PostPublisherList();
	
	@ZapcomApi(value="图片")
	private List<PicAllInfo> picInfos = new ArrayList<PicAllInfo>();
	
	@ZapcomApi(value="商品")
	private ProductInfo productinfo  = new ProductInfo();
	
	public String getPost_content() {
		return post_content;
	}

	public void setPost_content(String post_content) {
		this.post_content = post_content;
	}

	public String getPost_praise() {
		return post_praise;
	}

	public void setPost_praise(String post_praise) {
		this.post_praise = post_praise;
	}

	public PostPublisherList getPostPublisherList() {
		return postPublisherList;
	}

	public void setPostPublisherList(PostPublisherList postPublisherList) {
		this.postPublisherList = postPublisherList;
	}

	public int getComment_floor() {
		return comment_floor;
	}

	public void setComment_floor(int comment_floor) {
		this.comment_floor = comment_floor;
	}

	public String getPublish_time() {
		return publish_time;
	}

	public void setPublish_time(String publish_time) {
		this.publish_time = publish_time;
	}

	public String getComment_code() {
		return comment_code;
	}

	public void setComment_code(String comment_code) {
		this.comment_code = comment_code;
	}

	public String getIspraise() {
		return ispraise;
	}

	public void setIspraise(String ispraise) {
		this.ispraise = ispraise;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public PostPublisherList getPublishedList() {
		return publishedList;
	}

	public void setPublishedList(PostPublisherList publishedList) {
		this.publishedList = publishedList;
	}

	public ProductInfo getProductinfo() {
		return productinfo;
	}

	public void setProductinfo(ProductInfo productinfo) {
		this.productinfo = productinfo;
	}

	public List<PicAllInfo> getPicInfos() {
		return picInfos;
	}

	public void setPicInfos(List<PicAllInfo> picInfos) {
		this.picInfos = picInfos;
	}

}
