package com.cmall.newscenter.young.api;

import java.util.ArrayList;
import java.util.List;
import com.cmall.newscenter.young.model.VideoChannel;
import com.cmall.newscenter.young.model.VideoChannelInput;
import com.cmall.newscenter.young.model.VideoChannelResult;
import com.cmall.newscenter.young.model.VideoList;
import com.cmall.productcenter.model.PicAllInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 频道分类 
 * 
 * @author shiyz
 * 
 */
public class VideoChannelApi extends
		RootApiForManage<VideoChannelResult, VideoChannelInput> {

	public VideoChannelResult Process(VideoChannelInput inputParam,
			MDataMap mRequestMap) {

		VideoChannelResult result = new VideoChannelResult();
    
	 	if (result.upFlagTrue()) {

			List<MDataMap> list = DbUp.upTable("nc_video_channel").queryAll("",
					"channel_creattime,-channel_page", "channel_use='449747170002' and app_code='"+ getManageCode()+"'", new MDataMap());

			List<VideoChannel> channelList = new ArrayList<VideoChannel>();
  
			for (MDataMap lMap : list) {

				VideoChannel channel = new VideoChannel();

				channel.setChannel_code(lMap.get("channel_code"));

				List<MDataMap> reList = DbUp.upTable("nc_recreation").queryAll(
						"", "-recreation_weight,-recreation_updatetime", "recreation_channel = '"+
				            lMap.get("channel_code")+"'"+" and recreation_use='449747170002' and recreation_app = '"+getManageCode()+"'",new MDataMap());

				ProductService productPric = new ProductService();
				
				List<VideoList> videoList = new ArrayList<VideoList>();
				
				for (MDataMap reMap : reList) {

					VideoList video = new VideoList();
					
					video.setRecreation_name(reMap.get("recreation_name"));

					video.setRecreation_updatetime(reMap.get("recreation_updatetime"));

					video.setRecreation_url(reMap.get("recreation_url"));

					video.setRecreation_updatesum(Integer.valueOf(reMap
							.get("recreation_updatesum")));
					
					video.setPlaying_time(reMap.get("playing_time"));

					String album = reMap.get("recreation_photo");
					//String image = reMap.get("recreation_image");
					List<PicAllInfo> picInfo = new ArrayList<PicAllInfo>();
					//List<PicAllInfo> recreationImage = new ArrayList<PicAllInfo>();
					
					if (album != "") {

						String[] str = album.split("\\|");

						for (int j = 0; j < str.length; j++) {

							picInfo = productPric.getPicForMany(Integer.valueOf(inputParam.getPicWidth()), str[j]);
							
						}
					}
					   video.setPicInfos(picInfo);
					
					
					
					videoList.add(video);
				}
				channel.setVideoList(videoList);

				channel.setChannel_name(lMap.get("channel_name"));

				channel.setChannel_page(lMap.get("channel_page"));

				channelList.add(channel);
			}

			result.setChannel(channelList);

		}

		return result;
	}

}
