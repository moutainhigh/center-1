package com.cmall.newscenter.beauty.model;

import com.cmall.newscenter.model.PageOption;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 商品评论列表输入类
 * @author houwen
 * date 2014-08-25
 * @version 1.0
 */
public class ProductCommentLabelListInput extends RootInput {

	@ZapcomApi(value="sku编号",remark="sku编号",demo="8019404046",require=1)
	private String sku_code = "";

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

}
