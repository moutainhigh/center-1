package com.cmall.newscenter.beauty.model;

import com.cmall.newscenter.model.PageOption;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 帖子列表输入类
 * @author houwen
 * date 2014-08-26
 * @version 1.0
 */
public class PostListInput extends RootInput {

/*	@ZapcomApi(value="栏目ID",remark="栏目ID",demo="4497465000020001")
	private String post_catagory = "";*/

	@ZapcomApi(value="列表类型",remark="列表类型:1：我发布，2：我参与，3：我收藏",demo="0",require = 1)
	private String listType = "";

	@ZapcomApi(value = "翻页选项",remark = "输入起始页码和每页10条" ,demo= "5,10",require = 1)
	private PageOption paging = new PageOption();
	
	@ZapcomApi(value="图片宽度")
	private  	Integer  picWidth = 0 ;

	public PageOption getPaging() {
		return paging;
	}

	public void setPaging(PageOption paging) {
		this.paging = paging;
	}

	public String getListType() {
		return listType;
	}

	public void setListType(String listType) {
		this.listType = listType;
	}

	public Integer getPicWidth() {
		return picWidth;
	}

	public void setPicWidth(Integer picWidth) {
		this.picWidth = picWidth;
	}

}
