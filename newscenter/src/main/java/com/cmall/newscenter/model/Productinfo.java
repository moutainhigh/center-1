package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 在售商品类
 * @author liqiang
 * date 2014-7-4
 * @version 1.0
 */
public class Productinfo {

	@ZapcomApi(value = "skuid") 
	String id = "";
	@ZapcomApi(value="商品id")
	String product_id ="";
	
	@ZapcomApi(value = "单图或多图")
	private List<CommentdityAppPhotos> photos= new ArrayList<CommentdityAppPhotos>();

	@ZapcomApi(value = "标题",demo="海百合XXX面膜")
	private String title ="";
	
	@ZapcomApi(value = "售价",demo="￥198.00")
	private double sale_price ;
	
	@ZapcomApi(value = "原价",demo="￥223.00")
	private double orig_price ;
	
	@ZapcomApi(value = "推荐理由",demo="推荐理由")
	private String reason ="";
	
	@ZapcomApi(value = "介绍文字",demo="介绍介绍介绍介绍")
	private String intro ="";
	
	@ZapcomApi(value = "销售量",demo="100")
	private int sale_count ;
	
	@ZapcomApi(value = "库存量",demo="100")
	private int repo_count ;
	
	@ZapcomApi(value = "折扣",demo="9.5折")
	private String discount ="";
	
	@ZapcomApi(value = "图文详情",demo="http://")
	private String detail_url ="";
	
	@ZapcomApi(value = "产品参数",demo="http://")
	private String param_url ="";

	@ZapcomApi(value = "口碑评价")
	private List<CommentdityApp> comments = new ArrayList<CommentdityApp>();
	
	@ZapcomApi(value = "我是否收藏过，0-否，1-是",demo="1")
	private int faved =0;
	
	@ZapcomApi(value = "多少人收藏过",demo="1000")
	private int fav_count ;

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

	public double getSale_price() {
		return sale_price;
	}

	public void setSale_price(double sale_price) {
		this.sale_price = sale_price;
	}

	public double getOrig_price() {
		return orig_price;
	}

	public void setOrig_price(double orig_price) {
		this.orig_price = orig_price;
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

	public int getSale_count() {
		return sale_count;
	}

	public void setSale_count(int sale_count) {
		this.sale_count = sale_count;
	}

	public int getRepo_count() {
		return repo_count;
	}

	public void setRepo_count(int repo_count) {
		this.repo_count = repo_count;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
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

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	
}
