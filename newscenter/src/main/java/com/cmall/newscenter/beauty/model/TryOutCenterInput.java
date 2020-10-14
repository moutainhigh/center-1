package com.cmall.newscenter.beauty.model;

import com.cmall.newscenter.model.PageOption;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 试用中心输入类
 * @author yangrong
 * date: 2014-09-16
 * @version1.0
 */
public class TryOutCenterInput  extends RootInput {
	
	
/*	@ZapcomApi(value = "翻页选项",remark = "输入起始页码和每页10条" ,demo= "5,10",require = 1)
	private PageOption paging = new PageOption();*/
	
	@ZapcomApi(value="图片宽度")
	private  	Integer  picWidth = 0 ;


	public Integer getPicWidth() {
		return picWidth;
	}

	public void setPicWidth(Integer picWidth) {
		this.picWidth = picWidth;
	}
	
}
