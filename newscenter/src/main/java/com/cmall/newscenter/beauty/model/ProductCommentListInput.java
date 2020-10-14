package com.cmall.newscenter.beauty.model;

import com.cmall.newscenter.model.PageOption;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 商品评论列表输入类
 * @author houwen
 * date 2014-08-21
 * @version 1.0
 */
public class ProductCommentListInput extends RootInput {

	@ZapcomApi(value="sku编号",remark="sku编号",demo="8019404046",require=1)
	private String sku_code = "";

	@ZapcomApi(value="图片宽度",remark="图片宽度",demo="500")
	private Integer picWidth = 0 ;
	
	@ZapcomApi(value = "翻页选项",remark = "输入起始页码和每页10条" ,demo= "5,10",require = 1)
	private PageOption paging = new PageOption();

	public PageOption getPaging() {
		return paging;
	}

	public void setPaging(PageOption paging) {
		this.paging = paging;
	}

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public Integer getPicWidth() {
		return picWidth;
	}

	public void setPicWidth(Integer picWidth) {
		this.picWidth = picWidth;
	}

}
