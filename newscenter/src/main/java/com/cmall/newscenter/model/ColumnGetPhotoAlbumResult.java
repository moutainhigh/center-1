package com.cmall.newscenter.model;


import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 栏目 - 获取相册列表输出类
 * @author liqiang
 * date 2014-7-10
 * @version 1.0
 */
public class ColumnGetPhotoAlbumResult extends RootResultWeb{
	
	/**
	 * shiyz
	 */
	@ZapcomApi(value = "")
	private List<Album> albums = new ArrayList<Album>();

	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}

	public List<Album> getAlbums() {
		return albums;
	}

	public void setAlbums(List<Album> albums) {
		this.albums = albums;
	}

}
