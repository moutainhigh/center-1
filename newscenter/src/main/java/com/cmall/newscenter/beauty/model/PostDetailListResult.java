package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.productcenter.model.PicAllInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 帖子详情列表输出类
 * @author houwen
 * date 2014-08-26
 * @version 1.0
 */
public class PostDetailListResult extends RootResultWeb {

	/*@ZapcomApi(value = "主帖帖列表")
	private PostsDetailList postsDetailLists = new PostsDetailList();*/

	@ZapcomApi(value = "发布人信息列表")
	private PostPublisherList postPublisherLists = new PostPublisherList();
	
	@ZapcomApi(value="帖子编号")
	private String post_code = "" ;
	
	@ZapcomApi(value="标题")
	private String post_title = "" ;
	
	@ZapcomApi(value="正文")
	private String post_content = "";
	
	@ZapcomApi(value="图片")
	private String post_img  = "";
	
	@ZapcomApi(value="图片")
	private List<PicAllInfo> picInfos = new ArrayList<PicAllInfo>();

	@ZapcomApi(value="商品")
	private ProductInfo productinfo  = new ProductInfo();
	
	@ZapcomApi(value="化妆包")
	private CosmeticInfo cosmetictinfo  = new CosmeticInfo();
	
	@ZapcomApi(value="发布时间")
	private String publish_time  = "";
	
	@ZapcomApi(value="主贴点赞量")
	private String post_praise  = "";
	
	@ZapcomApi(value="帖子总点赞量(主帖和追帖点赞之和)")
	private String post_praise_count  = "";
	
	@ZapcomApi(value="是否收藏过",remark="是：449746860001；否：449746860002")
	private String iscollect  = "";
	
	@ZapcomApi(value="是否点赞过",remark="是：449746870001；否：449746870002")
	private String ispraise  = "";
	
	@ZapcomApi(value="回复数")
	private int  reply_acount  ;
	
	@ZapcomApi(value="官方帖",remark="是：449746880001；否：449746880002")
	private String isoffical  = "";
	
	@ZapcomApi(value="浏览量")
	private String post_browse  = "";
		
	@ZapcomApi(value = "追帖信息列表")
	private List<PostFollowList> postFollowLists = new ArrayList<PostFollowList>();
	
	@ZapcomApi(value="url")
	private String linkUrl = "";

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	@ZapcomApi(value="分享链接",remark="")
	private String share_url  = "";
	
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

	public String getPublish_time() {
		return publish_time;
	}

	public void setPublish_time(String publish_time) {
		this.publish_time = publish_time;
	}

	public List<PostFollowList> getPostFollowLists() {
		return postFollowLists;
	}

	public void setPostFollowLists(List<PostFollowList> postFollowLists) {
		this.postFollowLists = postFollowLists;
	}


	public PostPublisherList getPostPublisherLists() {
		return postPublisherLists;
	}

	public void setPostPublisherLists(PostPublisherList postPublisherLists) {
		this.postPublisherLists = postPublisherLists;
	}

	public String getIscollect() {
		return iscollect;
	}

	public void setIscollect(String iscollect) {
		this.iscollect = iscollect;
	}

	public String getIspraise() {
		return ispraise;
	}

	public void setIspraise(String ispraise) {
		this.ispraise = ispraise;
	}

	public String getPost_code() {
		return post_code;
	}

	public void setPost_code(String post_code) {
		this.post_code = post_code;
	}

	public int getReply_acount() {
		return reply_acount;
	}

	public void setReply_acount(int reply_acount) {
		this.reply_acount = reply_acount;
	}

	public ProductInfo getProductinfo() {
		return productinfo;
	}

	public void setProductinfo(ProductInfo productinfo) {
		this.productinfo = productinfo;
	}

	public String getShare_url() {
		return share_url;
	}

	public void setShare_url(String share_url) {
		this.share_url = share_url;
	}

	public String getIsoffical() {
		return isoffical;
	}

	public void setIsoffical(String isoffical) {
		this.isoffical = isoffical;
	}

	public String getPost_img() {
		return post_img;
	}

	public void setPost_img(String post_img) {
		this.post_img = post_img;
	}

	public String getPost_praise_count() {
		return post_praise_count;
	}

	public void setPost_praise_count(String post_praise_count) {
		this.post_praise_count = post_praise_count;
	}

	public CosmeticInfo getCosmetictinfo() {
		return cosmetictinfo;
	}

	public void setCosmetictinfo(CosmeticInfo cosmetictinfo) {
		this.cosmetictinfo = cosmetictinfo;
	}
}
