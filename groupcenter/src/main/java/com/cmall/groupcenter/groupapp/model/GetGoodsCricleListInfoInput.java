package com.cmall.groupcenter.groupapp.model;

import com.cmall.groupcenter.model.PageOption;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class GetGoodsCricleListInfoInput extends RootInput{
	
	@ZapcomApi(value = "热销和返利状态",require=1,demo="0:热销榜1:超返利",remark="0:热销榜1:超返利",verify="in=0,1")
	private String sectionType= "";
	
	@ZapcomApi(value = "翻页选项",remark = "输入起始页码和每页10条" ,demo= "5,10",require = 1)
	private PageOption paging = new PageOption();
	
	@ZapcomApi(value = "商品编号",require=0,remark="商品详情时需传参数")
	private String productCode="";
	
	@ZapcomApi(value = "图片宽度",require=0,remark="当前浏览器宽度")
	private int picWidth; 
	
	
	
	public int getPicWidth() {
		return picWidth;
	}


	public void setPicWidth(int picWidth) {
		this.picWidth = picWidth;
	}


	public String getProductCode() {
		return productCode;
	}


	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}


	public String getSectionType() {
		return sectionType;
	}


	public void setSectionType(String sectionType) {
		this.sectionType = sectionType;
	}


	public PageOption getPaging() {
		return paging;
	}


	public void setPaging(PageOption paging) {
		this.paging = paging;
	}


	
	
	
    

	
	
	
}
