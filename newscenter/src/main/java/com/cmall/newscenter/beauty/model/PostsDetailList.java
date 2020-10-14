package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 帖子列表类输出
 * @author houwen
 * date 2014-08-26
 * @version 1.0
 */
public class PostsDetailList {
	
	/*@ZapcomApi(value="用户ID")
	private String publisher_code  = "";*/
	
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

	@ZapcomApi(value="商品")
	private String product_code  = "";
	
	@ZapcomApi(value="发布时间")
	private String publish_time  = "";
	
	@ZapcomApi(value="点赞量")
	private String post_praise  = "";
	
	@ZapcomApi(value="是否收藏",remark="0:被收藏；空值代表未被收藏")
	private String iscollect  = "";
	
	@ZapcomApi(value="是否点赞",remark="0:被点赞；空值代表未被点赞")
	private String ispraise  = "";
	
	@ZapcomApi(value="回复数")
	private int  reply_acount  ;
	
/*	@ZapcomApi(value="精华帖")
	private String issessence  = "";*/
	
	@ZapcomApi(value="浏览量")
	private String post_browse  = "";
	
		
	@ZapcomApi(value = "追帖信息列表")
	private List<PostFollowList> postFollowLists = new ArrayList<PostFollowList>();
	
	/*@ZapcomApi(value = "评论帖子信息列表")
	private List<PostCommentList> postCommentLists = new ArrayList<PostCommentList>();
*/	

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

/*	public List<PostCommentList> getPostCommentLists() {
		return postCommentLists;
	}

	public void setPostCommentLists(List<PostCommentList> postCommentLists) {
		this.postCommentLists = postCommentLists;
	}*/

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
	
}
