package com.cmall.groupcenter.weixin.model;

/**
 * view类型的菜单
 * @author panwei
 * @date 2014年3月4日
 */
public class ViewButton extends Button{

	private String type;  
    private String url;
    
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}  
    
    
}
