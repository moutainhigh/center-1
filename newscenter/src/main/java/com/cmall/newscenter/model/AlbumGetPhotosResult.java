package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 获取相册信息
 * @author shiyz
 * @version 1.0
 */
public class AlbumGetPhotosResult extends RootResultWeb {
	
	@ZapcomApi(value = "相册照片")
	private List<CommentdityAppPhotos> photos = new ArrayList<CommentdityAppPhotos>();

	public List<CommentdityAppPhotos> getPhotos() {
		return photos;
	}

	public void setPhotos(List<CommentdityAppPhotos> photos) {
		this.photos = photos;
	}

}
