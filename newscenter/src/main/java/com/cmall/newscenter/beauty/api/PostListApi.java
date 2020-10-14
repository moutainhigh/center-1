package com.cmall.newscenter.beauty.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cmall.newscenter.beauty.model.PostListInput;
import com.cmall.newscenter.beauty.model.PostListResult;
import com.cmall.newscenter.beauty.model.PostPublisherList;
import com.cmall.newscenter.beauty.model.PostsList;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.model.PageResults;
import com.cmall.newscenter.util.DataPaging;
import com.cmall.productcenter.model.PicInfo;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 获取姐妹圈发布帖子信息列表
 * @author houwen
 * date 2014-08-26
 * @version 1.0
 */
public class PostListApi extends RootApiForToken<PostListResult, PostListInput> {

	public PostListResult Process(PostListInput inputParam,
			MDataMap mRequestMap) {
		
		PostListResult result = new PostListResult();
		
		ProductService productService = new ProductService();
		
		if(result.upFlagTrue()){
			
			MDataMap mWhereMap = new MDataMap();
			
			MDataMap mWhereMapReplay = new MDataMap();
			MDataMap mWhereMapPraise = new MDataMap();
			MDataMap mWhereMapComment = new MDataMap();
			MDataMap mWhereMapUser = new MDataMap();
			MPageData mPageDataUser = new MPageData();
			/*将栏目ID编号放入map中*/
			mWhereMap.put("post_catagory", "4497465000020001");
			mWhereMap.put("post_type", "449746780001"); //类型为主帖
			mWhereMap.put("status", "449746730001");
			mWhereMap.put("app_code", getManageCode());
			mWhereMap.put("is_delete", "0");
			
			//帖子列表类型： 0：全部，1：我发布，2：我参与，3：我收藏
			String listType = inputParam.getListType();
			
			MPageData mPageData = new MPageData();
			MPageData mPageDataReplay = new MPageData();
			MPageData mPageDataPraise = new MPageData();
			MPageData mPageDataComment = new MPageData();
			
			SimpleDateFormat sf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
			if(listType.equals("1")){
				
			   if(listType.equals("1")){
					//我发布的帖子
					mWhereMap.put("publisher_code", getUserCode());
					mPageData = DataPaging.upPageData("nc_posts", "", "-publish_time", mWhereMap, inputParam.getPaging());
				}
				if(mPageData.getListData().size()!=0){
					
					for(MDataMap mDataMap : mPageData.getListData()){
						
						PostsList postsList = new PostsList();
						
						mWhereMapReplay.put("post_code", mDataMap.get("post_code"));
						mWhereMapReplay.put("status", "449746800001");  //审核通过的评论
						mWhereMapReplay.put("app_code", getManageCode());
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
						
						String time = transform(mDataMap.get("publish_time"),sf.format(new Date()));
						postsList.setPublish_time(time);
					
						mWhereMapUser.put("member_code", mDataMap.get("publisher_code"));
						/*根据发布人ID查询发布人信息列表*/
				
						mPageDataUser = DataPaging.upPageData("mc_extend_info_star", "", "", mWhereMapUser,new PageOption());
						PostPublisherList postPublisherList = new PostPublisherList();
						postPublisherList.setMember_code(mDataMap.get("publisher_code"));
			
						for(MDataMap mDataMapUser : mPageDataUser.getListData()){
							
							if(mDataMapUser.get("nickname").equals("") || mDataMapUser.get("nickname")==null){
								String mobilePhone = mDataMapUser.get("mobile_phone");
								mobilePhone = mobilePhone.substring(0,mobilePhone.length()-(mobilePhone.substring(3)).length())+"*****"+mobilePhone.substring(8);
								postPublisherList.setNickname(mobilePhone);
							}else {
								postPublisherList.setNickname(mDataMapUser.get("nickname"));
							}
							postPublisherList.setMember_avatar(mDataMapUser.get("member_avatar"));
							postPublisherList.setSkin_type(mDataMapUser.get("skin_type"));
						   
						
						}
						 postsList.setPostPublisherLists(postPublisherList);
						
						postsList.setIshot(mDataMap.get("ishot"));
						
						result.getPosts().add(postsList);
					}
					
					result.setPaged(mPageData.getPageResults());
				}
				
			}
		 
		if(listType.equals("2") || listType.equals("3")){
			List<Map<String, Object>> list = null ;
			if(listType.equals("2")){
				//我参与的帖子：即评论和点赞的帖子
				String Opost_code = "";
				mWhereMapPraise.put("operater_code", getUserCode());
				mWhereMapPraise.put("operate_type", "4497464900030006");
				mWhereMapPraise.put("flag", "1");
				mPageDataPraise = DataPaging.upPageData("nc_post_operate", "", "", mWhereMapPraise, new PageOption());
				if(mPageDataPraise.getListData().size()!=0){
					for(MDataMap mpraiseDataMap :mPageDataPraise.getListData()){
						
						if(mpraiseDataMap.get("post_parent_code")!=null && !mpraiseDataMap.get("post_parent_code").equals("")){
							Opost_code = Opost_code +"'"+ mpraiseDataMap.get("post_parent_code")+"',";
						}else {
							Opost_code = Opost_code +"'"+ mpraiseDataMap.get("info_code")+"',";
						}
					}
				}
				mWhereMapComment.put("publisher_code", getUserCode());
				mWhereMapComment.put("app_code", getManageCode());
				mPageDataComment = DataPaging.upPageData("nc_posts_comment", "", "", mWhereMapComment, new PageOption());
				
				if(mPageDataComment.getListData().size()!=0){
					for(MDataMap mcommentDataMap :mPageDataComment.getListData()){
						Opost_code = Opost_code+"'" + mcommentDataMap.get("post_code")+"',";
					}
				}
				if(Opost_code.length()!=0){
				String post_code = Opost_code.substring(0,Opost_code.length()-1);
				String sql = "select * from nc_posts n where n.post_code in (" +post_code+ ") order by publish_time desc";
				list = DbUp.upTable("nc_posts").dataSqlList(sql,mWhereMap);
				}
				
			}else if(listType.equals("3")){
				//我收藏的帖子
				String Opost_code = "";
				mWhereMapPraise.put("operater_code", getUserCode());
				mWhereMapPraise.put("operate_type", "4497464900030005");
				mWhereMapPraise.put("flag", "1");
				mPageDataPraise = DataPaging.upPageData("nc_post_operate", "", "", mWhereMapPraise, new PageOption());
				if(mPageDataPraise.getListData().size()!=0){
					for(MDataMap mpraiseDataMap :mPageDataPraise.getListData()){
						Opost_code = Opost_code +"'"+ mpraiseDataMap.get("info_code")+"',";
					}
				
				String post_code = Opost_code.substring(0,Opost_code.length()-1);
				String sql = "select * from nc_posts n where n.post_code in (" +post_code+ ") order by publish_time desc";
				list = DbUp.upTable("nc_posts").dataSqlList(sql,mWhereMap);
				}
			}
			if(list!=null){
			int totalNum = list.size();
			int offset = inputParam.getPaging().getOffset();//起始页
			int limit = inputParam.getPaging().getLimit();//每页条数
			int startNum = limit*offset;//开始条数
			int endNum = startNum+limit;//结束条数
			int more = 1;//有更多数据
			Boolean flag = true;
			if(startNum<totalNum){
				flag = false;
			}
			if(endNum>=totalNum){
				if(0==totalNum){
					startNum = 0;
				}
				endNum = totalNum;
				more = 0;
			}
			
			//分页信息
			PageResults pageResults = new PageResults();
			pageResults.setTotal(totalNum);
			pageResults.setCount(endNum-startNum);
			pageResults.setMore(more);
			result.setPaged(pageResults);
			if(!flag){
			if(list.size()!=0){
				
				List<Map<String, Object>> subList = list.subList(startNum, endNum);
				
				for(int i = 0;i<subList.size();i++){
					
	               PostsList postsList = new PostsList();
					
					mWhereMapReplay.put("post_code", (String) subList.get(i).get("post_code"));
					mWhereMapReplay.put("status", "449746800001");  //审核通过的评论
					mWhereMapReplay.put("app_code", getManageCode());
					mPageDataReplay = DataPaging.upPageData("nc_posts_comment", "", "-publish_time", mWhereMapReplay, new PageOption());
					
					postsList.setIsofficial((String)subList.get(i).get("isofficial"));
					
					postsList.setIssessence((String)subList.get(i).get("issessence"));
					
					postsList.setPost_browse((String)subList.get(i).get("post_browse"));
					
					postsList.setPost_code((String)subList.get(i).get("post_code"));
					
					postsList.setPost_content((String)subList.get(i).get("post_content"));
					
					postsList.setPost_count(mPageDataReplay.getListData().size());
					
					//帖子图片显示：如果主帖超过两张图片，只显示两张，如主帖不足两张图片，则依次显示追帖图片
					PostListApi postListApi = new PostListApi();
					String postType = (String)subList.get(i).get("type");
					String postImg = (String)subList.get(i).get("post_img");
					String postCode = (String)subList.get(i).get("post_code");
					String picImgs = postListApi.imgShow(postType, postImg, postCode);
					
					postsList.setPicInfos(productService.getPicForMany(Integer.valueOf(inputParam.getPicWidth()), picImgs));

					PicInfo pic = productService.getPicInfoForMany(Integer.valueOf(inputParam.getPicWidth()), picImgs);
					
					postsList.setPost_img(pic.getPicNewUrl());

					postsList.setPost_label((String)subList.get(i).get("post_label"));
					
					postsList.setPost_praise((String)subList.get(i).get("post_praise"));
					
					postsList.setPost_title((String)subList.get(i).get("post_title"));
					
					String time = transform((String)list.get(i).get("publish_time"),sf.format(new Date()));
					postsList.setPublish_time(time);
					
					mWhereMapUser.put("member_code", (String)subList.get(i).get("publisher_code"));
				
					mPageDataUser = DataPaging.upPageData("mc_extend_info_star", "", "", mWhereMapUser,new PageOption());
					PostPublisherList postPublisherList = new PostPublisherList();
					postPublisherList.setMember_code((String)subList.get(i).get("publisher_code"));
					for(MDataMap mDataMapUser : mPageDataUser.getListData()){
						
						postPublisherList.setNickname(mDataMapUser.get("nickname"));
						postPublisherList.setMember_avatar(mDataMapUser.get("member_avatar"));
						postPublisherList.setSkin_type(mDataMapUser.get("skin_type"));
					}
					postsList.setPostPublisherLists(postPublisherList);
					
					postsList.setIshot((String)subList.get(i).get("ishot"));
					
					result.getPosts().add(postsList);
					
				}
				
			}
			}
			}
		}
		
		}
		return result;
	}
	
	
	  //时间格式转换
	 public  String transform(String starttime,String endtime) {
		  String  timeString = "";
		  String starttemp[] = starttime.split(" ");
		  String start = starttemp[0];
		  String end = starttemp[1];
		  String starttime1[] = start.split("-");
		  String endtime1[] = end.split(":");
		  int startyears = Integer.valueOf(starttime1[0]);
		  int startmonths = Integer.valueOf(starttime1[1]);
		  int startdays = Integer.valueOf(starttime1[2]);
		  int starthours = Integer.valueOf(endtime1[0]);
		  int startminutes = Integer.valueOf(endtime1[1]);
		  float startseconds = Float.valueOf(endtime1[2]);
		  float allstartSeconds ;
		  if(startmonths==1 || startmonths==3 || startmonths==5 || startmonths==7 || startmonths==8||startmonths==10 || startmonths==12){
			  allstartSeconds = startmonths*31 *24 *60 *60 + startdays *24*60*60 +starthours * 60 * 60 + startminutes * 60 + startseconds;
		  }else if(startmonths==2){
			  allstartSeconds = startmonths*28 *24 *60 *60 + startdays *24*60*60 +starthours * 60 * 60 + startminutes * 60 + startseconds;
		  }else{
			  allstartSeconds = startmonths*30 *24 *60 *60 + startdays *24*60*60 +starthours * 60 * 60 + startminutes * 60 + startseconds;
		  }
		 
		  
		  String endtemp[] = endtime.split(" ");
		  String start2 = endtemp[0];
		  String end2 = endtemp[1];
		  String starttime2[] = start2.split(":");
		  String endtime2[] = end2.split(":");
		  int endyears = Integer.valueOf(starttime2[0]);
		  int endmonths = Integer.valueOf(starttime2[1]);
		  int enddays = Integer.valueOf(starttime2[2]);
		  int endhours = Integer.valueOf(endtime2[0]);
		  int endminutes = Integer.valueOf(endtime2[1]);
		  float endseconds = Float.valueOf(endtime2[2]);
		  float allendSeconds;
		  if(startmonths==1 || startmonths==3 || startmonths==5 || startmonths==7 || startmonths==8||startmonths==10 || startmonths==12){
		     allendSeconds = endmonths*31 *24 *60 *60 + enddays *24*60*60 + endhours * 60 * 60 + endminutes * 60 + endseconds;
		  }else if(endmonths==2){
			  allendSeconds = endmonths*28 *24 *60 *60 + enddays *24*60*60 + endhours * 60 * 60 + endminutes * 60 + endseconds;
		  }else{
			  allendSeconds = endmonths*30 *24 *60 *60 + enddays *24*60*60 + endhours * 60 * 60 + endminutes * 60 + endseconds;
		  }
		  float seconds = allendSeconds-allstartSeconds;
		
		  float sevenDays = 7*24*60*60;
		  float days = 24*60*60;
		  float hours = 60*60;
		  float minutes = 60;
		  String startdays1;
		  String startmonths1;
		 if(allstartSeconds==allendSeconds){
			 timeString = "刚刚";
		 }else {
		
		if(startyears!=endyears){
			if(startdays<10){
				startdays1 = "0"+String.valueOf(startdays);
			}else {
				startdays1 = String.valueOf(startdays);
			}
			if(startmonths<10){
				startmonths1 = "0" + String.valueOf(startmonths);
			}else {
				startmonths1 =  String.valueOf(startmonths);
			}
			timeString = String.valueOf(startyears)+"-"+startmonths1+"-"+startdays1;
		}else if(seconds>sevenDays) {
			if(startdays<10){
				startdays1 = "0"+String.valueOf(startdays);
			}else {
				startdays1 = String.valueOf(startdays);
			}
			if(startmonths<10){
				startmonths1 = "0" + String.valueOf(startmonths);
			}else {
				startmonths1 =  String.valueOf(startmonths);
			}
			timeString = startmonths1+"-"+startdays1;
		}else if(seconds>days){
			timeString = String.valueOf((int)Math.floor(seconds/days))+"天前";
		}else if(seconds>hours){
			timeString = String.valueOf((int)Math.floor(seconds/hours))+"小时前";
		}else if(seconds>minutes){
			timeString = String.valueOf((int)Math.floor(seconds/minutes))+"分钟前";
		}else {
			timeString = String.valueOf((int)seconds)+"秒前";
		}
	}
		
		return timeString;
	 }
	 
	 //帖子图片显示：如果主帖超过两张图片，只显示两张，如主帖不足两张图片，则依次显示追帖图片
	 public String imgShow(String postType,String postImg,String postCode) {
		 
			String picImgs = "";
			int imgSize = 0;
			if(null == postType||postType.equals("449747130001") || postType.equals("")){
				if(null !=postImg && !postImg.equals("")){
					imgSize = postImg.split("\\|").length;
				}
			 
			if(imgSize>2){
				String imgs[] = postImg.split("\\|");
				picImgs = imgs[0]+"|"+imgs[1];
			}else if(imgSize==2) {
					 picImgs = postImg; 
			}else {
				
				String sql = "select post_img from nc_posts where post_parent_code='"+postCode+"'";
				MDataMap mwhereMap = new MDataMap();
				mwhereMap.put("post_parent_code", postCode);
				List<Map<String,Object>> followList = DbUp.upTable("nc_posts").dataSqlList(sql, mwhereMap);
				if(followList.size()>0){
					if(imgSize>0){
						picImgs = postImg+"|"; 
					}
				    boolean flag = false;
				    for(int m=0;m<followList.size()&&!flag;m++){
						if(null !=followList.get(m).get("post_img") && !followList.get(m).get("post_img").equals("")){
						String followImg[] = followList.get(m).get("post_img").toString().split("\\|");
						for(int j =0;j<followImg.length;j++){
							picImgs += followImg[j]+"|";
							if(picImgs.split("\\|").length==2){
								flag = true;
								break;
							}
							
						}
						}
				   }
				   if(null != picImgs && !picImgs.equals("")){
				   picImgs = picImgs.substring(0,picImgs.length()-1);
				   }else {
					   picImgs = postImg;
				   }
				    
			   
			 }else {
				 picImgs = postImg; 
			}
			}
			}else {
				picImgs = postImg; 
			}
			return picImgs;
	}
	 
}
