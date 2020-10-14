package com.cmall.groupcenter.func;

import java.util.ArrayList;
import java.util.List;

/**
 * 字符串转对象的类
 * @author dyc
 * */
public class PostContentModel {

	private ContentDetail content = new ContentDetail();
	
	private List<ProductModel> products = new ArrayList<ProductModel>();


	/**
	 * 获取  content
	 */
	public ContentDetail getContent() {
		return content;
	}

	/**
	 * 设置 
	 * @param content 
	 */
	public void setContent(ContentDetail content) {
		this.content = content;
	}

	/**
	 * 获取  products
	 */
	public List<ProductModel> getProducts() {
		return products;
	}

	/**
	 * 设置 
	 * @param products 
	 */
	public void setProducts(List<ProductModel> products) {
		this.products = products;
	}
	
	
}
