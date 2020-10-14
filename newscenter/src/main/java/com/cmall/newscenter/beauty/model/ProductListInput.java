package com.cmall.newscenter.beauty.model;


import com.cmall.newscenter.model.PageOption;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 商品列表输入类
 * @author houwen
 * date 2014-8-28
 * @version 1.0
 */
public class ProductListInput extends RootInput {
	
	@ZapcomApi(value = "翻页选项",remark = "输入起始页码和每页10条" ,demo= "5,10",require = 1)
	private PageOption paging = new PageOption();
	
	@ZapcomApi(value = "按分类",remark = "按分类可选  为空查询全部" ,demo= "到商品分类接口中查询分类编号")
	private String category="";
	
	@ZapcomApi(value = "排序规则",remark = "默认=449746820001   销量=449746820002  新品=449746820003" ,demo= "449746820001")
	private String sort="";

	@ZapcomApi(value="图片宽度")
	private  	Integer  picWidth = 0 ;
	
	public PageOption getPaging() {
		return paging;
	}

	public void setPaging(PageOption paging) {
		this.paging = paging;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public Integer getPicWidth() {
		return picWidth;
	}

	public void setPicWidth(Integer picWidth) {
		this.picWidth = picWidth;
	}

	
}
