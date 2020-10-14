package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 商品评论列表输出类
 * @author houwen
 * date 2014-08-21
 * @version 1.0
 */
public class ProductCommentLabelResult extends RootResultWeb {

	@ZapcomApi(value = "印象标签列表")
	private List<ProductCommentLabel> productComment = new ArrayList<ProductCommentLabel>();
	
	public List<ProductCommentLabel> getProductComment() {
		return productComment;
	}

	public void setProductComment(List<ProductCommentLabel> productComment) {
		this.productComment = productComment;
	}

	
}
