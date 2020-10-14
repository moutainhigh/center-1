package com.cmall.newscenter.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.cmall.membercenter.model.MemberInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 资讯类
 * @author shiyz
 * date 2014-7-7
 * @version 1.0
 */
public class InforCollectionFeed {

	@ZapcomApi(value = "资讯id")
	private String id = "";
	
	@ZapcomApi(value = "资讯所属分类")
	private String feedId = "";
	
	@ZapcomApi(value = "限制条件")
	private CommentdityAppLimt limit = new CommentdityAppLimt();
	
	@ZapcomApi(value = "发布者")
	private MemberInfo user = new MemberInfo();
	
	@ZapcomApi(value = "标题")
	private String title = "";
	
	@ZapcomApi(value = "文字",require=1,verify={"minlength=20","maxlength=2000"})
	private String text = "";
	
	@ZapcomApi(value = "单图或多图")
	private List<CommentdityAppPhotos> photos = new ArrayList<CommentdityAppPhotos>();
	
	@ZapcomApi(value = "单视频")
	private List<InforMationFeedVideo> videos = new ArrayList<InforMationFeedVideo>();
	
	@ZapcomApi(value="商品信息")
	private Sale_Product product = new Sale_Product();
	
	@ZapcomApi(value="活动信息")
	private Activity activity = new Activity();
	
	@ZapcomApi(value = "链接地址")
	private String link = "";
	
	@ZapcomApi(value = "我是否喜欢过")
	private int liked ;
	
	@ZapcomApi(value = "多少人喜欢")
	private int like_count = 0; 
	
	@ZapcomApi(value = "我是否收藏过")
	private int faved =0;
	
	@ZapcomApi(value = "多少人收藏过")
	private int fav_count = 0;
	
	@ZapcomApi(value = "我是否分享过")
	private int shared =0;
	
	@ZapcomApi(value = "多少人分享过")
	private int share_count = 0;
	
	@ZapcomApi(value = "我是否评论过")
	private int commented =0;
	
	@ZapcomApi(value = "多少人评论过")
	private int comment_count = 0;
	
	@ZapcomApi(value = "创建时间")
	private String created_at = "";
	
	@ZapcomApi(value = "资讯所属分类",demo = "4497465000040001",require=1, verify = { "in=4497465000040001,4497465000040002,4497465000040003" })
	BigInteger feed_type = new BigInteger("0");
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public CommentdityAppLimt getLimit() {
		return limit;
	}

	public void setLimit(CommentdityAppLimt limit) {
		this.limit = limit;
	}

	public MemberInfo getUser() {
		return user;
	}

	public void setUser(MemberInfo user) {
		this.user = user;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getLiked() {
		return liked;
	}

	public void setLiked(int liked) {
		this.liked = liked;
	}

	public int getLike_count() {
		return like_count;
	}

	public void setLike_count(int like_count) {
		this.like_count = like_count;
	}

	public int getFaved() {
		return faved;
	}

	public void setFaved(int faved) {
		this.faved = faved;
	}

	public int getFav_count() {
		return fav_count;
	}

	public void setFav_count(int fav_count) {
		this.fav_count = fav_count;
	}

	public int getShared() {
		return shared;
	}

	public void setShared(int shared) {
		this.shared = shared;
	}

	public int getShare_count() {
		return share_count;
	}

	public void setShare_count(int share_count) {
		this.share_count = share_count;
	}

	public int getCommented() {
		return commented;
	}

	public void setCommented(int commented) {
		this.commented = commented;
	}

	public int getComment_count() {
		return comment_count;
	}

	public void setComment_count(int comment_count) {
		this.comment_count = comment_count;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}


	public List<CommentdityAppPhotos> getPhotos() {
		return photos;
	}

	public void setPhotos(List<CommentdityAppPhotos> photos) {
		this.photos = photos;
	}

	public List<InforMationFeedVideo> getVideos() {
		return videos;
	}

	public void setVideos(List<InforMationFeedVideo> videos) {
		this.videos = videos;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getFeedId() {
		return feedId;
	}

	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}

	public BigInteger getFeed_type() {
		return feed_type;
	}

	public void setFeed_type(BigInteger feed_type) {
		this.feed_type = feed_type;
	}

	public Sale_Product getProduct() {
		return product;
	}

	public void setProduct(Sale_Product product) {
		this.product = product;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	

}
