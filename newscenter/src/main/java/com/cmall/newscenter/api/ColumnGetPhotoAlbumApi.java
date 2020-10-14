package com.cmall.newscenter.api;


import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.Album;
import com.cmall.newscenter.model.ColumnGetPhotoAlbumInput;
import com.cmall.newscenter.model.ColumnGetPhotoAlbumResult;
import com.cmall.newscenter.model.CommentdityAppPhotos;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;
/**
 * 栏目_获取相册列表Api
 * @author lqiang
 * date: 2014-07-10
 * @version1.0
 */
public class ColumnGetPhotoAlbumApi extends RootApiForManage<ColumnGetPhotoAlbumResult, ColumnGetPhotoAlbumInput> {

	public ColumnGetPhotoAlbumResult Process(
			ColumnGetPhotoAlbumInput inputParam, MDataMap mRequestMap) {
		/**
		 * shiyz
		 */
		ColumnGetPhotoAlbumResult result = new ColumnGetPhotoAlbumResult();
		String app_code = bConfig("newscenter.app_code");
		// 设置相关信息
		if (result.upFlagTrue()) {
			
			MDataMap mWhereMap = new MDataMap();
			
			
			/*将相册分类编号放入Map中*/
			mWhereMap.put("info_category", inputParam.getColumn());
			
			mWhereMap.put("flag_show", "449746530001");
			mWhereMap.put("manage_code",app_code);
			/*查询相册所有详细信息*/
			MPageData mPageData = DataPaging.upPageData("nc_info", "", "-create_time", mWhereMap, inputParam.getPaging());
			
			if(mPageData.getListData().size()!=0){
				
				
				for(MDataMap mDataMap :mPageData.getListData()){
					
					Album albums = new Album();
					
					/*获得标题*/
					albums.setTitle(mDataMap.get("info_title"));
					
					/*内容编号*/
					albums.setId(mDataMap.get("info_code"));
					
					/*获取原图链接*/
					
					String album = mDataMap.get("photos");
					
					List<CommentdityAppPhotos> photos = new ArrayList<CommentdityAppPhotos>();
					
					/*判断是否存在图片*/
					if(album!=""){
						
				    String[] str   = album.split("\\|");
				    
					for(int i = 0; i<str.length;i++){
						
						CommentdityAppPhotos photo = new CommentdityAppPhotos();
						
						photo.setLarge(str[i]);
						
						photo.setThumb(str[i]);
						
						photos.add(photo);
						
					}
					albums.setPhotos(photos);
					}
					
					result.getAlbums().add(albums);
					
				}
			}

			
			/*返回翻页结果*/
			result.setPaged(mPageData.getPageResults());
		}
		return result;
	}

}
