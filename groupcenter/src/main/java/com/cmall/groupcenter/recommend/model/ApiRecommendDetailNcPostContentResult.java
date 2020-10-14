package com.cmall.groupcenter.recommend.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 好物推荐帖子内容中的图文内容
 * @author gaozx
 *
 */
public class ApiRecommendDetailNcPostContentResult{
	/**内容-文本**/
	public final static String TYPE_TEXT = "0";
	/**内容-图片**/
	public final static String TYPE_IMG = "1";
	
	@ZapcomApi(value = "内容类型", remark="0：文本，1：图片")
	private String type;
	
	@ZapcomApi(value = "内容排序")
	private String sort = "";
	
	@ZapcomApi(value = "内容")
	private String content;
	
	@ZapcomApi(value = "宽度", remark="360px")
	private String width;
	
	@ZapcomApi(value = "高度", remark="360px")
	private String height;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * 获取  sort
	 */
	public String getSort() {
		return sort;
	}

	/**
	 * 设置 
	 * @param sort 
	 */
	public void setSort(String sort) {
		this.sort = sort;
	}

	
	
}
