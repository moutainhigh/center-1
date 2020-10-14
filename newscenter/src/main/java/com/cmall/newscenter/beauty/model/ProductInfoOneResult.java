package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.productcenter.model.PicInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 商品详情  输出类
 * @author yangrong
 * date 2014-9-15
 * @version 1.0
 */
public class ProductInfoOneResult  extends RootResultWeb {
	
	@ZapcomApi(value = "sku编码")
	private String  sku_code= "";
	
	@ZapcomApi(value = "商品编码")
	private String  product_code= "";
	
	@ZapcomApi(value = "产品名称")
	private String name = "";
	
	@ZapcomApi(value = "商品图片")
	private List<PicInfo> photos = new ArrayList<PicInfo>();
	
	@ZapcomApi(value = "月销量")
	private String stock_num = "";
	
	@ZapcomApi(value = "产品详情")
	private List<PicInfo> infophotos = new ArrayList<PicInfo>();
	
	@ZapcomApi(value = "商品状态",remark="4497153900060001=待上架    4497153900060002=已上架   4497153900060003=商家下架   4497153900060004=平台强制下架")
	private String status = "";
	
	@ZapcomApi(value = "收藏状态",remark="0为未收藏    1是已收藏")
	private String favstatus = "";
	
	@ZapcomApi(value = "库存数",remark="返回多少库存就是多少")
	private String store_num = "";
	
	@ZapcomApi(value="url")
	private String linkUrl = "";

	public String getFavstatus() {
		return favstatus;
	}

	public void setFavstatus(String favstatus) {
		this.favstatus = favstatus;
	}

	

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

	public String getProduct_code() {
		return product_code;
	}

	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}

	public List<PicInfo> getPhotos() {
		return photos;
	}

	public void setPhotos(List<PicInfo> photos) {
		this.photos = photos;
	}

	public String getStock_num() {
		return stock_num;
	}

	public void setStock_num(String stock_num) {
		this.stock_num = stock_num;
	}


	

	public List<PicInfo> getInfophotos() {
		return infophotos;
	}

	public void setInfophotos(List<PicInfo> infophotos) {
		this.infophotos = infophotos;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStore_num() {
		return store_num;
	}

	public void setStore_num(String store_num) {
		this.store_num = store_num;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	

}
