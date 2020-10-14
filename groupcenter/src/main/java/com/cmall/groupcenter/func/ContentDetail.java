package com.cmall.groupcenter.func;

import java.util.ArrayList;
import java.util.List;

public class ContentDetail {
	private String title="";
	private List<TextAndImg> textandimg = new ArrayList<TextAndImg>();
	/**
	 * 获取  title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * 设置 
	 * @param title 
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * 获取  textandimg
	 */
	public List<TextAndImg> getTextandimg() {
		return textandimg;
	}
	/**
	 * 设置 
	 * @param textandimg 
	 */
	public void setTextandimg(List<TextAndImg> textandimg) {
		this.textandimg = textandimg;
	}
	
	
}
