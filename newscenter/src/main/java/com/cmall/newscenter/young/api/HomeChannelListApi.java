package com.cmall.newscenter.young.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.beauty.api.PostListApi;
import com.cmall.newscenter.beauty.model.PostPublisherList;
import com.cmall.newscenter.beauty.model.PostsList;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.model.PageResults;
import com.cmall.newscenter.util.DataPaging;
import com.cmall.newscenter.young.model.HomeChannelListInput;
import com.cmall.newscenter.young.model.HomeChannelListResult;
import com.cmall.newscenter.young.model.VideoChannel;
import com.cmall.newscenter.young.model.VideoList;
import com.cmall.productcenter.model.PicAllInfo;
import com.cmall.productcenter.model.PicInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 首页小时代列表 
 * @author shiyz
 * date 2015-03-01
 * @version 1.0
 */
public class HomeChannelListApi extends RootApiForManage<HomeChannelListResult, HomeChannelListInput> {

	public HomeChannelListResult Process(HomeChannelListInput inputParam,
			MDataMap mRequestMap) {
		  
		  HomeChannelListResult result = new HomeChannelListResult();
		
		if(result.upFlagTrue()){
			
			MDataMap mWhereMap = new MDataMap();
			
			MDataMap mWhereMapReplay = new MDataMap();
			
			MDataMap mWhereMapUser = new MDataMap();
			MPageData mPageDataUser = new MPageData();
			/*将栏目ID编号放入map中*/
			mWhereMap.put("post_catagory", "4497465000020001");
			mWhereMap.put("post_type", "449746780001"); //类型为主帖
			mWhereMap.put("status", "449746730001");
			mWhereMap.put("issessence", "449746770001");  //精华帖
			mWhereMap.put("is_delete", "0");
			mWhereMap.put("app_code", getManageCode());
			
			MPageData mPageData = new MPageData();
			MPageData mPageDataReplay = new MPageData();
			
			ProductService productService = new ProductService();
			 
			List<VideoChannel> channelList = new ArrayList<VideoChannel>();
			    
			List<MDataMap> listMaps = DbUp.upTable("nc_recreation").queryAll("", "-recreation_updatetime,recreation_weight", 
					"recreation_page='449747180002' and recreation_use='449747170002' and recreation_app='"+ getManageCode()+"'", new MDataMap());
			  
			ProductService productPric = new ProductService();
			
			List<PicAllInfo> picInfo = new ArrayList<PicAllInfo>();
			List<PicAllInfo> recreationImage = new ArrayList<PicAllInfo>();
			
				if(listMaps.size()!=0){
					
				MDataMap	reDataMap = listMaps.get(0);
				
				List<VideoList> videoList = new ArrayList<VideoList>();
				
				VideoChannel channel = new VideoChannel();
				
				VideoList video = new VideoList();

				video.setRecreation_name(reDataMap.get("recreation_name"));

				video.setRecreation_updatetime(reDataMap.get(
						"recreation_updatetime"));

				video.setRecreation_url(reDataMap.get("recreation_url"));
				
				video.setRecreation_updatesum(Integer.valueOf(reDataMap.get("recreation_updatesum")));
				
				video.setPlaying_time(reDataMap.get("playing_time"));
				
				//video.setRecreation_image(recreation_image);(reDataMap.get("recreation_image"));
				
				String album = reDataMap.get("recreation_photo");
				String image = reDataMap.get("recreation_image");

				MDataMap map =DbUp.upTable("nc_video_channel").one("channel_code",reDataMap.get("recreation_channel"));
				
				if(map!=null){
				
					channel.setChannel_code("");
					
					channel.setChannel_name("新料抢先看");
					
				}
				
				 
				/* 判断是否存在封面图 */
				if (image != "") {

					String[] str = image.split("\\|");

					for (int j = 0; j < str.length; j++) {

						recreationImage =  productPric.getPicForMany(Integer.valueOf(inputParam.getPicWidth()), str[j]);
						
					}
					
					video.setPicInfos(recreationImage);
					
				}
				else{
					/* 判断是否存在图片 */
					if (album != "") {

						String[] str = album.split("\\|");

						for (int j = 0; j < str.length; j++) {

							picInfo =  productPric.getPicForMany(Integer.valueOf(inputParam.getPicWidth()), str[j]);
							
						}
						video.setPicInfos(picInfo);
						
						
					}
					
				}
				
				videoList.add(video);
				
				channel.setVideoList(videoList);
				
				channelList.add(channel);
			}
			
		    /*根据栏目ID查询帖子列表*/
			mPageData = DataPaging.upPageData("nc_posts", "", "-publish_time", mWhereMap, inputParam.getPaging());
			    
			if(mPageData.getListData().size()!=0){
				
				List<PostsList> posts = new ArrayList<PostsList>();
				
				VideoChannel channel = new VideoChannel();
				
				channel.setChannel_name(bConfig("newscenter.post_title"));
				
				for(MDataMap mDataMap : mPageData.getListData()){
					
					PostsList postsList = new PostsList();
					
					mWhereMapReplay.put("post_code", mDataMap.get("post_code"));
				   
					mWhereMapReplay.put("status", "449746800001");  //审核通过的评论
					
					mPageDataReplay = DataPaging.upPageData("nc_posts_comment", "", "-publish_time", mWhereMapReplay, new PageOption());
					
					postsList.setIsofficial(mDataMap.get("isofficial"));
					
					postsList.setIssessence(mDataMap.get("issessence"));
					
					postsList.setPost_browse(mDataMap.get("post_browse"));
					
					postsList.setPost_code(mDataMap.get("post_code"));
					
					postsList.setPost_content(mDataMap.get("post_content"));
					
					postsList.setPost_count(mPageDataReplay.getListData().size());
					
					if(mDataMap.get("cover_img")!=null && !mDataMap.get("cover_img").equals("")){
					
						postsList.setPicInfos(productService.getPicForMany(Integer.valueOf(inputParam.getPicWidth()), mDataMap.get("cover_img")));

						PicInfo pic = productService.getPicInfoForMany(Integer.valueOf(inputParam.getPicWidth()), mDataMap.get("cover_img"));
						
						postsList.setPost_img(pic.getPicNewUrl());
						
					}else {
						
						//帖子图片显示：如果主帖超过两张图片，只显示两张，如主帖不足两张图片，则依次显示追帖图片
						PostListApi postListApi = new PostListApi();
						String postType = mDataMap.get("type");
						String postImg = mDataMap.get("post_img");
						String postCode = mDataMap.get("post_code");
						String picImgs = postListApi.imgShow(postType, postImg, postCode);
						
						postsList.setPicInfos(productService.getPicForMany(Integer.valueOf(inputParam.getPicWidth()),picImgs));
						
						PicInfo pic = productService.getPicInfoForMany(Integer.valueOf(inputParam.getPicWidth()),picImgs);
						
						postsList.setPost_img(pic.getPicNewUrl());
						
					}
					
					postsList.setPost_label(mDataMap.get("post_label"));
					
					postsList.setPost_praise(mDataMap.get("post_praise"));
					
					postsList.setPost_title(mDataMap.get("post_title"));
					
					postsList.setPublish_time(mDataMap.get("publish_time"));
					
					mWhereMapUser.put("member_code", mDataMap.get("publisher_code"));
					/*根据发布人ID查询发布人信息列表*/
		
					mPageDataUser = DataPaging.upPageData("mc_extend_info_star", "", "", mWhereMapUser,new PageOption());
         
					for(MDataMap mDataMapUser : mPageDataUser.getListData()){
						
						PostPublisherList postPublisherList = new PostPublisherList();
						postPublisherList.setMember_code(mDataMap.get("publisher_code"));
						if(mDataMapUser.get("nickname").equals("") || mDataMapUser.get("nickname")==null){
							String mobilePhone = mDataMapUser.get("mobile_phone");
							mobilePhone = mobilePhone.substring(0,mobilePhone.length()-(mobilePhone.substring(3)).length())+"*****"+mobilePhone.substring(8);
							postPublisherList.setNickname(mobilePhone);
						}else {
							postPublisherList.setNickname(mDataMapUser.get("nickname"));
						}
						
						postPublisherList.setMember_avatar(mDataMapUser.get("member_avatar"));
						postPublisherList.setSkin_type(mDataMapUser.get("skin_type"));
					    postsList.setPostPublisherLists(postPublisherList);
					}
					
					
					postsList.setIshot(mDataMap.get("ishot"));
					
					posts.add(postsList);
					
					channel.setPosts(posts);
				}
				channelList.add(channel);
				
			}
			
				int totalNum = channelList.size();
				int offset = inputParam.getPaging().getOffset();// 起始页
				int limit = inputParam.getPaging().getLimit();// 每页条数
				int startNum = limit * offset;// 开始条数
				int endNum = startNum + limit;// 结束条数
				int more = 1;// 有更多数据
				Boolean flag = true;
				if (startNum < totalNum) {
					flag = false;
				}
				if (endNum >= totalNum) {
					if (0 == totalNum) {
						startNum = 0;
					}
					endNum = totalNum;
					more = 0;
				}

				// 分页信息
				PageResults pageResults = new PageResults();
				pageResults.setTotal(totalNum);
				pageResults.setCount(endNum - startNum);
				pageResults.setMore(more);
				result.setPaged(pageResults);
				result.setChannel(channelList);
				
			}
			
		return result;
	}


}
