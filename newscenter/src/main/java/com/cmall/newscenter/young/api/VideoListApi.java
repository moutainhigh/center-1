package com.cmall.newscenter.young.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.util.DataPaging;
import com.cmall.newscenter.young.model.VideoList;
import com.cmall.newscenter.young.model.VideoListInput;
import com.cmall.newscenter.young.model.VideoListResult;
import com.cmall.productcenter.model.PicAllInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * @date 2015-02-02
 * @author shiyz 视频列表接口
 */
public class VideoListApi extends
		RootApiForManage<VideoListResult, VideoListInput> {

	public VideoListResult Process(VideoListInput inputParam,
			MDataMap mRequestMap) {

		VideoListResult result = new VideoListResult();

		if (result.upFlagTrue()) {

			MDataMap mDataMap = new MDataMap();

			mDataMap.put("recreation_channel", inputParam.getRecreation_type());
			mDataMap.put("recreation_use", "449747170002");
			mDataMap.put("recreation_app", getManageCode());
			
			MPageData mPageData = DataPaging.upPageData("nc_recreation", "", "-recreation_updatetime,recreation_weight", mDataMap, inputParam.getPaging());
			
			List<VideoList> videoList = new ArrayList<VideoList>();
			
			MDataMap map = DbUp.upTable("nc_video_channel").one("channel_code",inputParam.getRecreation_type());

			if(map!=null){
				result.setChannel_name(map.get("channel_name"));
			}
			  
			ProductService productPric = new ProductService();
			
			for( MDataMap reDataMap: mPageData.getListData()){
				
				List<PicAllInfo> picInfo = new ArrayList<PicAllInfo>();
					VideoList video = new VideoList();
    
					video.setRecreation_name(reDataMap.get("recreation_name"));

					video.setRecreation_updatetime(reDataMap.get(
							"recreation_time").substring(0, 10));

					video.setRecreation_url(reDataMap.get("recreation_url"));
				 	
					video.setRecreation_updatesum(Integer.valueOf(reDataMap.get("recreation_updatesum")));
					
					video.setPlaying_time(reDataMap.get("playing_time"));
					
					video.setRecreation_watch(reDataMap.get("recreation_watch"));
					
					  

					String album = reDataMap.get("recreation_photo");
					/* 判断是否存在图片 */
					if (album != "") {

						String[] str = album.split("\\|");

						for (int j = 0; j < str.length; j++) {

							picInfo =  productPric.getPicForMany(Integer.valueOf(inputParam.getPicWidth()), str[j]);
							
						}
						video.setPicInfos(picInfo);
						 
						
					}
					
					
					videoList.add(video);
				}

			result.setVideoList(videoList);
			
			result.setPaged(mPageData.getPageResults()); 
		}

		return result;
	}

}
