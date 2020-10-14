package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 商品评论标签列表类
 * @author houwen
 * date 2014-08-21
 * @version 1.0
 */
public class ProductCommentLabel {
	
	/*@ZapcomApi(value="app编号")
	private String app_code  = "";

	@ZapcomApi(value="sku编号")
	private String sku_code = "" ;
	
	@ZapcomApi(value="sku名称")
	private String sku_name = "";*/
	
	@ZapcomApi(value="印象标签名称")
	private String label = "";
	
	@ZapcomApi(value="印象标签数量")
	private int label_amount;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getLabel_amount() {
		return label_amount;
	}

	public void setLabel_amount(int label_amount) {
		this.label_amount = label_amount;
	}
	
}
