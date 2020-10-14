package com.cmall.newscenter.beauty.api;


import java.text.SimpleDateFormat;
import java.util.Date;
import com.cmall.newscenter.beauty.model.PostAllListInput;
import com.cmall.newscenter.beauty.model.PostListResult;
import com.cmall.newscenter.beauty.model.PostPublisherList;
import com.cmall.newscenter.beauty.model.PostsList;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.cmall.productcenter.model.PicInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 获取姐妹圈发布帖子信息列表  --点击全部  ，未登录也可查看
 * @author houwen
 * date 2014-08-26
 * @version 1.0
 */
public class PostAllListApi extends RootApiForManage<PostListResult, PostAllListInput> {

	public PostListResult Process(PostAllListInput inputParam,
			MDataMap mRequestMap) {
		
		PostListResult result = new PostListResult();
		
		if(result.upFlagTrue()){
			
			MDataMap mWhereMap = new MDataMap();
			
			MDataMap mWhereMapReplay = new MDataMap();
			MDataMap mWhereMapUser = new MDataMap();
			MPageData mPageDataUser = new MPageData();
			/*将栏目ID编号放入map中*/
			mWhereMap.put("post_catagory", "4497465000020001");
			mWhereMap.put("post_type", "449746780001"); //类型为主帖
			mWhereMap.put("status", "449746730001");
			mWhereMap.put("app_code", getManageCode());
			mWhereMap.put("is_delete", "0");
 
			MPageData mPageData = new MPageData();
			MPageData mPageDataReplay = new MPageData();
			
			ProductService productService = new ProductService(); 
			
			//如果帖子分类传入不为空，则按分类返回数据，如果为空，则返回全部帖子
			if(!inputParam.getPost_category().equals("") && inputParam.getPost_category()!=null ){
				mWhereMap.put("post_label", inputParam.getPost_category());
			}
		    /*根据栏目ID查询帖子列表*/
			mPageData = DataPaging.upPageData("nc_posts", "", "sort,-publish_time", mWhereMap, inputParam.getPaging());
			    
			if(mPageData.getListData().size()!=0){
				
				for(MDataMap mDataMap : mPageData.getListData()){
					
					PostsList postsList = new PostsList();
					
					mWhereMapReplay.put("post_code", mDataMap.get("post_code"));
					mWhereMapReplay.put("status", "449746800001");  //审核通过的评论
					mWhereMapReplay.put("app_code", getManageCode()); //app
				   
					mPageDataReplay = DataPaging.upPageData("nc_posts_comment", "", "-publish_time", mWhereMapReplay, new PageOption());
					
					postsList.setIsofficial(mDataMap.get("isofficial"));
					
					postsList.setIssessence(mDataMap.get("issessence"));
					
					postsList.setPost_browse(mDataMap.get("post_browse"));
					
					postsList.setPost_code(mDataMap.get("post_code"));
					
					postsList.setPost_content(mDataMap.get("post_content"));
					
					postsList.setPost_count(mPageDataReplay.getListData().size());
					
					//帖子图片显示：如果主帖超过两张图片，只显示两张，如主帖不足两张图片，则依次显示追帖图片
					PostListApi postListApi = new PostListApi();
					String postType = mDataMap.get("type");
					String postImg = mDataMap.get("post_img");
					String postCode = mDataMap.get("post_code");
					String picImgs = postListApi.imgShow(postType, postImg, postCode);
			
					postsList.setPicInfos(productService.getPicForMany(Integer.valueOf(inputParam.getPicWidth()),picImgs));
					
                    PicInfo pic = productService.getPicInfoForMany(Integer.valueOf(inputParam.getPicWidth()),picImgs);
					
					postsList.setPost_img(pic.getPicNewUrl());

					postsList.setPost_label(mDataMap.get("post_label"));
					
					postsList.setPost_praise(mDataMap.get("post_praise"));
					
					postsList.setPost_title(mDataMap.get("post_title"));
			
					PostListApi pApi = new PostListApi();
					SimpleDateFormat sf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
					String time = pApi.transform(mDataMap.get("publish_time"), sf.format(new Date()));
					postsList.setPublish_time(time);
					
					mWhereMapUser.put("member_code", mDataMap.get("publisher_code"));
					mPageDataUser = DataPaging.upPageData("mc_extend_info_star", "", "", mWhereMapUser,new PageOption());
					
					/*根据发布人ID查询发布人信息列表*/
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
					
					result.getPosts().add(postsList);
				}
				
				result.setPaged(mPageData.getPageResults());
			}
			
		}
		return result;
	}

}
