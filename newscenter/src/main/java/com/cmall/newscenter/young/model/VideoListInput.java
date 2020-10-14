package com.cmall.newscenter.young.model;

import com.cmall.newscenter.model.PageOption;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * @date 2015-02-02
 * @author shiyz 视频列表接口
 */
public class VideoListInput extends RootInput {

	@ZapcomApi(value = "频道编号", require = 1)
	private String recreation_type = "";

	@ZapcomApi(value="图片宽度")
	private  Integer  picWidth = 0 ;
	
	@ZapcomApi(value = "翻页选项")
	private PageOption paging = new PageOption();
	
	public String getRecreation_type() {
		return recreation_type;
	}

	public void setRecreation_type(String recreation_type) {
		this.recreation_type = recreation_type;
	}

	public PageOption getPaging() {
		return paging;
	}

	public void setPaging(PageOption paging) {
		this.paging = paging;
	}

	public Integer getPicWidth() {
		return picWidth;
	}

	public void setPicWidth(Integer picWidth) {
		this.picWidth = picWidth;
	}
	
	
}
