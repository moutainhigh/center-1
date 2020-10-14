package com.cmall.groupcenter.recommend.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 好物推荐详情页
 * @author gaozx
 *
 */
public class ApiGetRecommendDetailInput  extends RootInput{
	
	@ZapcomApi(value="好物推荐帖子id",remark="nc_post pid",require= 1)
	private String pid = "";
	
	@ZapcomApi(value="好物推荐帖子内容中图文列表中的图片最大宽度",remark="当值为大于0的整数时转换图片",require= 0)
	private String imageMaxWidth = "";

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getImageMaxWidth() {
		return imageMaxWidth;
	}

	public void setImageMaxWidth(String imageMaxWidth) {
		this.imageMaxWidth = imageMaxWidth;
	}

}
