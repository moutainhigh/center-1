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
public class ProductCommentListResult extends RootResultWeb {

	@ZapcomApi(value = "商品评论列表")
	private List<ProductComment> productComment = new ArrayList<ProductComment>();
	
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();
	
	public List<ProductComment> getProductComment() {
		return productComment;
	}

	public void setProductComment(List<ProductComment> productComment) {
		this.productComment = productComment;
	}

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}

}
