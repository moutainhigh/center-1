package com.cmall.groupcenter.func;

import java.util.ArrayList;
import java.util.List;

/**
 * 字符串转对象的类
 * @author dyc
 * */
public class PostModel {
	
	private List<PostContentModel> postContents = new ArrayList<PostContentModel>();

	/**
	 * 获取  postContents
	 */
	public List<PostContentModel> getPostContents() {
		return postContents;
	}

	/**
	 * 设置 
	 * @param postContents 
	 */
	public void setPostContents(List<PostContentModel> postContents) {
		this.postContents = postContents;
	}
	
	
}
