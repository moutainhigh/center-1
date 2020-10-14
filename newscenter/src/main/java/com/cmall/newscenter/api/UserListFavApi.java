package com.cmall.newscenter.api;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.cmall.membercenter.model.MemberInfo;
import com.cmall.newscenter.model.Activity;
import com.cmall.newscenter.model.CommentdityAppPhotos;
import com.cmall.newscenter.model.InforCollectionFeed;
import com.cmall.newscenter.model.InforMationFeedVideo;
import com.cmall.newscenter.model.Location;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageResults;
import com.cmall.newscenter.model.Sale_Product;
import com.cmall.newscenter.model.UserListFavInput;
import com.cmall.newscenter.model.UserListFavResult;
import com.cmall.newscenter.util.DataPaging;
import com.cmall.newscenter.webfunc.FuncQueryProductInfo;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 用户 - 我的收藏
 * @author shiyz	
 * date 2014-08-01
 * @version 1.0
 */
public class UserListFavApi extends RootApiForToken<UserListFavResult, UserListFavInput> {

	public UserListFavResult Process(UserListFavInput inputParam,
			MDataMap mRequestMap) {
		
		UserListFavResult result = new UserListFavResult();
		
		MDataMap mWhereMap = new MDataMap();
		
		/*用户编号*/
		String info_code = "";
		
		if(result.upFlagTrue()){
			
			List<MDataMap> wMDataMap = new ArrayList<MDataMap>();
			
			MDataMap mwDataMap = new MDataMap();
			
			mwDataMap.put("member_code", getUserCode());
			
			mwDataMap.put("num_type", "4497464900030005");
			
			mwDataMap.put("flag_enable", "1");
			
			wMDataMap = DbUp.upTable("nc_num").queryAll("", "-create_time", "", mwDataMap);
			
			if(wMDataMap.size()!=0){
				
                 for(int i=0;i<wMDataMap.size();i++){
					
					
					MDataMap smDataMap = wMDataMap.get(i);
					
					info_code = smDataMap.get("num_code");
					
					InforCollectionFeed info = new InforCollectionFeed();
					
					if(info_code.substring(0, 2).equals("JL")){
					
					
						mWhereMap.put("info_code", info_code);
						
						MDataMap mPageData=DbUp.upTable("nc_info").one("info_code",info_code);
					
						//查出是否收藏过                       
						MDataMap mFavMap = DbUp.upTable("nc_num").one("member_code",getUserCode(),"num_code",info_code,"num_type","4497464900030005");
						
						//查出是否喜欢过             
						MDataMap mLikeMap = DbUp.upTable("nc_num").one("member_code",getUserCode(),"num_code",info_code,"num_type","4497464900030003");
						
						//查出是否分享过
						MDataMap mShareMap = DbUp.upTable("nc_num").one("member_code",getUserCode(),"num_code",info_code,"num_type","4497464900030004");
						
						/* 根据评价编号查询相关已审核通过评价的数量 */
						int commentNum = DbUp.upTable("nc_comment").count("info_code",info_code,"create_member", getUserCode());
						
						
						int num = DbUp.upTable("nc_comment").count("info_code",info_code);
						
						if(mPageData!=null){
							
							//查出发起人信息 
							MemberInfo user = new MemberInfo();
							MDataMap mUserMap = DbUp.upTable("mc_extend_info_star").one("member_code",mPageData.get("create_member"));
							
							if(mUserMap!=null){
								//发起人信息
								user.setMember_code(mUserMap.get("member_code"));                                   //用户编号
								user.setNickname(mUserMap.get("nickname"));                                         //昵称
								user.setMobile((mUserMap.get("mobile_phone")));                                    //电话
								user.setGroup(BigInteger.valueOf(Long.valueOf(mUserMap.get("member_group"))));      //用户组
								if(null == mUserMap.get("member_sex") || "".equals(mUserMap.get("member_sex"))){
									user.setGender(BigInteger.valueOf(Long.valueOf("4497465100010001")));	
								}else{
									user.setGender(BigInteger.valueOf(Long.valueOf(mUserMap.get("member_sex"))));       //性别
								}
								user.setScore(Integer.parseInt(mUserMap.get("member_score")));                      //积分
								user.getAvatar().setLarge(mUserMap.get("member_avatar"));                           //用户头像
								user.getAvatar().setThumb(mUserMap.get("member_avatar"));
								user.setScore_unit(bConfig("newscenter.Score_unit"));                               //积分单位
								user.setLevel(Integer.valueOf(mUserMap.get("member_level").substring(mUserMap.get("member_level").length()-4, mUserMap.get("member_level").length())));                                        //等级
								//根据等级编号查出等级名称
								MDataMap mLevelMap = DbUp.upTable("mc_member_level").one("level_code",mUserMap.get("member_level"));
								if(mLevelMap!=null){
									
									user.setLevel_name(mLevelMap.get("level_name"));                                 //等级名称
								}
								user.setCreate_time(mUserMap.get("create_time"));                                    //加入时间
							}
							
							if(mPageData.get("info_category")!=null&&mPageData.get("info_category").equals("44974650000100060001")){
								//这条收藏记录为活动
								Activity activity = new Activity();
								activity.setId(info_code);//活动信息id
//								activity.setLimit(limit);//限制条件
								activity.setUser(user);//发起人
								activity.setTitle(mPageData.get("info_title"));//标题
								activity.setText(mPageData.get("info_content"));//内容
									
								
								/* 分享链接 */
								activity.setLinkUrl(bConfig("newscenter.shareLink")+"/capp/web/introduction/shareActivity.ftl?activity="+info_code);
								/*获取单图或者多图*/
								
								String album = mPageData.get("photos");
								
								List<CommentdityAppPhotos> photos = new ArrayList<CommentdityAppPhotos>();
								
								
								/*判断是否存在图片*/
								if(album!=""){
									
							    String[] str   = album.split("\\|");
							    
								for(int j = 0; j<str.length;j++){
									
									CommentdityAppPhotos photo = new CommentdityAppPhotos();
									
									photo.setLarge(str[j]);
									
									photo.setThumb(str[j]);
									
									photos.add(photo);
									
								}
								activity.setPhoto(photos);
								}
								if(mLikeMap!=null){
									activity.setLiked(1);//我是否喜欢过，0-否，1-是
								}
								
								activity.setLike_count(Integer.valueOf(mPageData.get("num_like")));//多少人喜欢过
								
								activity.setFaved(mFavMap == null ? 0 :1);//我是否收藏过，0-否，1-是
								
								activity.setFav_count(Integer.valueOf(mPageData.get("num_favorite")));//多少人收藏过
								
								
								if(mShareMap!=null){
									activity.setShared(1);//我是否喜欢过，0-否，1-是
								}
								
								
								activity.setShare_count(Integer.valueOf(mPageData.get("num_share")));//多少人分享过
								if(commentNum!=0){
									activity.setCommented(1);//我是否评论过，0-否，1-是
								}
								
								activity.setComment_count(num);//多少人评论过
								
								List<MDataMap> actList = DbUp.upTable("nc_registration").queryByWhere("info_code",info_code);//根据活动编码查出表中所有数据
							    activity.setApply_count(actList.size());//多少人报名过
								activity.setCreated_at(mFavMap == null ? "" : mFavMap.get("create_time"));//创建时间
								activity.setLevel(mPageData.get("min_level"));//限制等级
								String sSql = "select * from mc_member_level where level_code =:level_code";
								MDataMap  mMemberMap = new MDataMap();
								mMemberMap.put("level_code", mPageData.get("min_level"));
								mMemberMap.put("app_code",getManageCode());
							    Map<String, Object> map = DbUp.upTable("mc_extend_info_star").dataSqlOne(sSql, mMemberMap);
								activity.setLevel_name(map!=null?map.get("level_name").toString():"");//限制等级名称
								
								MDataMap me = DbUp.upTable("mc_extend_info_star").one("member_code",getUserCode());
								long mylevel = Long.parseLong(me.get("member_level"));
								long minLevel = 0;
								if(mPageData.get("min_level")!=null&&(!mPageData.get("min_level").equals(""))){
									minLevel= Long.parseLong(mPageData.get("min_level"));
								}
								activity.setCan_join(mylevel>=minLevel?1:0);//我是否可以报名，0-否，1-是
								MDataMap register = DbUp.upTable("nc_registration").one("info_code",info_code,"info_member",getUserCode());
								if(register!=null){
									activity.setJoined(Integer.valueOf(register.get("is_enable")));//我是否报名了，0-否，1-是
									activity.setJoined_at(register.get("registration_time"));//报名时间
								}
								
								//经纬度
								WebClientSupport webClientSupport = new WebClientSupport();
								String sResponse = "";
								try {
									sResponse = webClientSupport
											.doGet("http://api.map.baidu.com/geocoder/v2/?address="+mPageData.get("address")+"&output=json&ak=479efee5e54b5d3f6cd724008a81659b");
								} catch (Exception e) {

									e.printStackTrace();
								}

								bLogInfo(0, sResponse);
								
								//将location解析出来
								com.alibaba.fastjson.JSONObject parseObject = JSON.parseObject(sResponse);
								com.alibaba.fastjson.JSONObject parseObject1 = (com.alibaba.fastjson.JSONObject) parseObject.get("result");
								com.alibaba.fastjson.JSONObject parseObject2 = (com.alibaba.fastjson.JSONObject) parseObject1.get("location");

								//转成map
								Map<String ,Object> tmap = new HashMap<String ,Object>();
								tmap.put("lat", parseObject2.get("lat"));
								tmap.put("lon", parseObject2.get("lng"));
								tmap.put("name", mPageData.get("address"));
								//转成实体类
								JsonHelper<Location> jsonHelper=new JsonHelper<Location>();
								Location location=jsonHelper.StringToObj(JSON.toJSONString(tmap), new Location());
								
								activity.setLocation(location);//位置信息
								info.setActivity(activity);
							}else{
								//这条收藏记录为资讯
								
								/*资讯ID*/
								info.setId(mPageData.get("info_code"));
								/*资讯所属分类*/
								info.setFeedId(mPageData.get("info_category"));
								/*发布者*/
								info.setUser(user);
								
								/*标题*/
								info.setTitle(mPageData.get("info_title"));
								
								/*内容*/
								info.setText(mPageData.get("info_content"));
								
								/*相关链接*/
								info.setLink(mPageData.get("link_url"));
								
								if(mLikeMap!=null){
									info.setLiked(1);//我是否喜欢过，0-否，1-是
								}
								
								info.setLike_count(Integer.valueOf(mPageData.get("num_like")));// 多少人喜欢过
								
								info.setFaved(mFavMap == null ? 0 : Integer.valueOf(mFavMap.get("flag_enable")));// 我是否收藏过，0-否，1-是
								
								info.setCreated_at(mFavMap == null ? "" : mFavMap.get("create_time"));// 创建时间
								
								info.setFav_count(Integer.valueOf(mPageData.get("num_favorite")));// 多少人收藏过
								
								if(mShareMap!=null){
									info.setShared(1);//我是否喜欢过，0-否，1-是
								}
								
								
								info.setShare_count(Integer.valueOf(mPageData.get("num_share")));//  多少人分享过
								if(commentNum!=0){
									info.setCommented(1);//我是否评论过，0-否，1-是
								}
								info.setComment_count(num);//多少人评论过
								info.setFeed_type(BigInteger.valueOf(Long.valueOf(mPageData.get("feed_type"))));
								
								String album = mPageData.get("photos");
								
								List<CommentdityAppPhotos> photos = new ArrayList<CommentdityAppPhotos>();
								
								/*判断是否存在图片*/
								if(album!=""){
									
								    String[] str   = album.split("\\|");
								    
									for(int j = 0; j<str.length;j++){
										
										CommentdityAppPhotos photo = new CommentdityAppPhotos();
										
										photo.setLarge(str[j]);
										
										photo.setThumb(str[j]);
										
										photos.add(photo);
										
									}
									info.setPhotos(photos);
								}else{
									
									info.setPhotos(photos);
									
								}
								
								/*视频*/
								List<InforMationFeedVideo> videos = new ArrayList<InforMationFeedVideo>();
								
								String videoLink = "";
										
								videoLink =	mPageData.get("video_link");
								
								String video_photos = "";
										
								video_photos = 	mPageData.get("video_photos");
								
								InforMationFeedVideo video = new InforMationFeedVideo();
								
								video.setHd_url(videoLink);
								
								video.setCover(video_photos);
								
								video.setLd_url(videoLink);
								
								videos.add(video);
								
								info.setVideos(videos);
							}
							result.getFeeds().add(info);
						}
					}else {
						
						MDataMap map = DbUp.upTable("pc_skuinfo").one("sku_code",info_code);
						
						if(map!=null){
						
						FuncQueryProductInfo funcQueryProductInfo = new FuncQueryProductInfo();
						
						List<Sale_Product> saleProduct = funcQueryProductInfo.qryProInSaleService(info_code,map.get("product_code"),getUserCode(),getManageCode());
						
						//一个商品的sku对应该一个商品信息
						Sale_Product sale_Product = saleProduct.get(0);
						
						info.setProduct(sale_Product);
						
						result.getFeeds().add(info);
						
						} 
	                	 
	                	 
					}
					
                 }
					
				}

			}
		
		int totalNum = result.getFeeds().size();
		int offset = inputParam.getPaging().getOffset();//起始页
		int limit = inputParam.getPaging().getLimit();//每页条数
		int startNum = limit*offset;//开始条数
		int endNum = startNum+limit;//结束条数
		int more = 1;//有更多数据
		if(endNum>totalNum){
			endNum = totalNum;
			more = 0;
		}
		//如果起始条件大于总数则返回0条数据
		if(startNum>totalNum){
			startNum = 0;
			endNum = 0;
			more = 0;
		}
		//分页信息
		PageResults pageResults = new PageResults();
		pageResults.setTotal(totalNum);
		pageResults.setCount(endNum-startNum);
		pageResults.setMore(more);
		result.setPaged(pageResults);
		
		result.setFeeds(result.getFeeds().subList(startNum, endNum));
		
		return result;
	}
}
