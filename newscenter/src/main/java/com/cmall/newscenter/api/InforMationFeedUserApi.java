package com.cmall.newscenter.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.CommentdityAppPhotos;
import com.cmall.newscenter.model.InforMationFeed;
import com.cmall.newscenter.model.InforMationFeedUserInput;
import com.cmall.newscenter.model.InforMationFeedUserResult;
import com.cmall.newscenter.model.InforMationFeedVideo;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 刘嘉玲-发布资讯列表
 * @author shiyz
 * date 2014-7-16
 * @version 1.0
 */
public class InforMationFeedUserApi extends RootApiForToken<InforMationFeedUserResult, InforMationFeedUserInput> {

	public InforMationFeedUserResult Process(
			InforMationFeedUserInput inputParam, MDataMap mRequestMap) {
		
		InforMationFeedUserResult result = new InforMationFeedUserResult();
		
		MDataMap userMap = new MDataMap(); 
		userMap = DbUp.upTable("mc_extend_info_star").one("member_code",getUserCode(),"app_code",getManageCode());
		
		if(userMap!=null){
			
			if(userMap.get("member_group").equals("4497465000020003")){
				
		
		
		InforMationFeed feed=inputParam.getFeed();
		
		MDataMap mDataMap = new MDataMap();
		
		/*内容编号*/
		String info_code = WebHelper.upCode("JL");
		
		/*资讯ID*/
		mDataMap.put("info_code", info_code);
		
		/*创建时间*/
		mDataMap.put("create_time", FormatHelper.upDateTime());
		
		/*前台创建人*/
		mDataMap.put("create_member", getUserCode());
		
		/*标题*/
		mDataMap.put("info_title", feed.getTitle());
		
		/*内容*/
		mDataMap.put("info_content", feed.getText());
		
		/*资讯类型*/
		mDataMap.put("feed_type", String.valueOf(feed.getFeed_type()));
		
		/**图片*/
		List<CommentdityAppPhotos> photos = new ArrayList<CommentdityAppPhotos>(); 
		
		photos = feed.getPhotos();
		
		CommentdityAppPhotos photo = new CommentdityAppPhotos();
		
		String large = "";
		
		if(photos.size()!=0){
			for(int i=0;i<photos.size();i++){
				
				photo = photos.get(i);
				
				 String largephoto = photo.getLarge();
				   
				 large += largephoto+"|";
				
			}
			/*图片*/
			mDataMap.put("photos", large.substring(0,large.length()-1));
			
		}
		
		/**视频图片*/
		List<InforMationFeedVideo> videos = new ArrayList<InforMationFeedVideo>();
		
		videos = feed.getVideos();
		
		InforMationFeedVideo video = new InforMationFeedVideo();
		
		
		if(videos.size()!=0){
			for(int i=0;i<videos.size();i++){
				
				video = videos.get(i);
				
				 String largephoto = video.getCover();
				   
				 large += largephoto+"|";
				
			}
			/*图片*/
			mDataMap.put("video_photos", large.substring(0,large.length()-1));
			
			mDataMap.put("video_link", video.getHd_url());
			
		}
		
		mDataMap.put("manage_code", getManageCode());
		
		/*链接地址*/
		mDataMap.put("link_url", feed.getLink());
		
		mDataMap.put("flag_show", "449746530001");
		
		/*所属分类*/
		mDataMap.put("info_category", feed.getFeedId());
		
		if(result.upFlagTrue()){
			
			/*将前台发布的资讯插入数据表中*/
			DbUp.upTable("nc_info").dataInsert(mDataMap);
			
		}
		
	}else{
		
		result.setResultCode(969905923);
		
		result.setResultMessage(bInfo(969905923));
		
	}
			
		}
		
		return result;
		
	}

}
