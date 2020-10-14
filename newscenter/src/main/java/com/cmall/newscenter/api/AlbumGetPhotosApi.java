package com.cmall.newscenter.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.AlbumGetPhotosInput;
import com.cmall.newscenter.model.AlbumGetPhotosResult;
import com.cmall.newscenter.model.CommentdityAppPhotos;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;
/**
 * 相册获取照片
 * @author shiyz
 * date 2014-7-18
 * @version 1.0
 */
public class AlbumGetPhotosApi extends RootApiForManage<AlbumGetPhotosResult, AlbumGetPhotosInput> {

	public AlbumGetPhotosResult Process(AlbumGetPhotosInput inputParam,
			MDataMap mRequestMap) {
		
		AlbumGetPhotosResult result = new AlbumGetPhotosResult();
		String app_code = bConfig("newscenter.app_code");
		if(result.upFlagTrue()){
			
			/*根据内容编号查询相册详细信息*/
			MDataMap mDataMap = DbUp.upTable("nc_info").one("info_code",inputParam.getAlbum(),"manage_code",app_code);
			
			if(mDataMap!=null){
				
			/*获取相册链接*/
			String album = mDataMap.get("photos");
			
			/*判断是否存在图片*/
			if(album!=""){
				
				
		    String[] str   = album.split("\\|");
		
		    List<CommentdityAppPhotos> photos = new ArrayList<CommentdityAppPhotos>();
		    
			for(int i = 0; i<str.length;i++){
				
				CommentdityAppPhotos photo = new CommentdityAppPhotos();
				
				photo.setLarge(str[i]);
				
				photo.setThumb(str[i]);
				
				photos.add(photo);
				
				/*放入相册实体类中*/
				result.setPhotos(photos);
			}
			
			}
			}
			
		}
		return result;
	}

}
