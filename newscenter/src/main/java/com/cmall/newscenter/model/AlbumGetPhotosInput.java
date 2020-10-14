package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class AlbumGetPhotosInput extends RootInput {
	
	@ZapcomApi(value = "相册",remark = "相册", demo = "0",require = 1)
	private String album = "";

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}
	
}
