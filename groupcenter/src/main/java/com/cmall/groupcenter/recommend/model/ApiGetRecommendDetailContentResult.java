package com.cmall.groupcenter.recommend.model;

import java.util.LinkedList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 好物推荐帖子内容
 * @author gaozx
 *
 */
public class ApiGetRecommendDetailContentResult{
	@ZapcomApi(value = "帖子内容ID")
	private String p_cid;
	
	@ZapcomApi(value = "帖子标题", remark="目前为\"\"，无意义")
	private String p_title;
	
	@ZapcomApi(value = "帖子内容")
	private String p_content;
	
	@ZapcomApi(value = "帖子图文内容", remark="切割自@p_content")
	private List<ApiRecommendDetailNcPostContentResult> textAndImgList;
	
	@ZapcomApi(value="帖子内容关联商品")
	private List<ApiGetRecommendDetailProductResult> listAdProduct = new LinkedList<ApiGetRecommendDetailProductResult>();

	public String getP_cid() {
		return p_cid;
	}

	public void setP_cid(String p_cid) {
		this.p_cid = p_cid;
	}

	public String getP_content() {
		return p_content;
	}

	public void setP_content(String p_content) {
		this.p_content = p_content;
	}

	public String getP_title() {
		return p_title;
	}

	public void setP_title(String p_title) {
		this.p_title = p_title;
	}

	public List<ApiRecommendDetailNcPostContentResult> getTextAndImgList() {
		return textAndImgList;
	}

	public void setTextAndImgList(
			List<ApiRecommendDetailNcPostContentResult> textAndImgList) {
		this.textAndImgList = textAndImgList;
	}

	public List<ApiGetRecommendDetailProductResult> getListAdProduct() {
		return listAdProduct;
	}

	public void setListAdProduct(
			List<ApiGetRecommendDetailProductResult> listAdProduct) {
		this.listAdProduct = listAdProduct;
	}
	
}
