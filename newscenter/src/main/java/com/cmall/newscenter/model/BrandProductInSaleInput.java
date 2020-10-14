package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 品牌 - 在售商品列表输入类
 * @author liqiang
 * date 2014-7-10
 * @version 1.0
 */
public class BrandProductInSaleInput extends RootInput {
	
	@ZapcomApi(value = "翻页选项",remark = "输入起始页码和每页10条" ,demo= "5,10",require = 1)
	private PageOption paging = new PageOption();
	
	@ZapcomApi(value = "按分类",remark = "按分类可选" ,demo= "0")
	private String category="";
	
	@ZapcomApi(value = "商品编号",remark="9108956")
	private String product = "";

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

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}
	
}
