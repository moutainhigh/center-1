package com.cmall.newscenter.model;


import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 订单-评价输入类
 * @author liqiang
 * date 2014-7-22
 * @version 1.0
 */
public class OrderRateInput extends RootInput{
	
	@ZapcomApi(value="订单序号",require=1)
	private String order="";

	@ZapcomApi(value="文字",demo="不错不错")
	private String text="";
	
	@ZapcomApi(value="sku编号",require=1)
	private String skuid = "";

	@ZapcomApi(value="xxx.jpg",demo="图片组")
	private List<CommentdityAppPhotos> photos = new ArrayList<CommentdityAppPhotos>();

	
	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<CommentdityAppPhotos> getPhotos() {
		return photos;
	}

	public void setPhotos(List<CommentdityAppPhotos> photos) {
		this.photos = photos;
	}

	public String getSkuid() {
		return skuid;
	}

	public void setSkuid(String skuid) {
		this.skuid = skuid;
	}


}
