package com.cmall.newscenter.api;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.cmall.newscenter.model.Activity;
import com.cmall.newscenter.model.CommentdityAppPhotos;
import com.cmall.newscenter.model.CommunityListActivityInput;
import com.cmall.newscenter.model.CommunityListActivityResult;
import com.cmall.newscenter.model.Location;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForMember;
/**
 * 社区 - 活动列表
 * @author yangrong
 * date 2014-7-21
 * @version 1.0
 */
public class CommunityListActivityApi extends RootApiForMember<CommunityListActivityResult, CommunityListActivityInput> {

	public CommunityListActivityResult Process(
			CommunityListActivityInput inputParam, MDataMap mRequestMap) {
		
		
		CommunityListActivityResult result = new CommunityListActivityResult();
		
		if(result.upFlagTrue()){
			
			
			MDataMap mWhereMap=new MDataMap();
			
			//查出社区下的活动列表
			mWhereMap.put("info_category","44974650000100060001");
		
			MPageData mPageData=DataPaging.upPageData("nc_info", "", "-create_time", mWhereMap, inputParam.getPaging());
			
			
			if(mPageData!=null){
				
				for( MDataMap mDataMap: mPageData.getListData()){
					
					Activity activity = new Activity();
						
					
					int num = DbUp.upTable("nc_comment").count("info_code",mDataMap.get("info_code"),"manage_code", getManageCode(),"flag_show", "4497172100030002");
					
					/*用户如果是登陆状态返回用户操作信息*/
					if(getFlagLogin()){
						
						/*用户编号*/
						String userCode = getOauthInfo().getUserCode();
					//查出是否收藏过                       
					MDataMap mFavMap = DbUp.upTable("nc_num").one("member_code",userCode,"num_code",mDataMap.get("info_code"),"num_type","4497464900030005");
					
					//查出是否喜欢过             
					MDataMap mLikeMap = DbUp.upTable("nc_num").one("member_code",userCode,"num_code",mDataMap.get("info_code"),"num_type","4497464900030003");
					
					//查出是否分享过
					MDataMap mShareMap = DbUp.upTable("nc_num").one("member_code",userCode,"num_code",mDataMap.get("info_code"),"num_type","4497464900030004");
					
					/* 根据评价编号查询相关已审核通过评价的数量 */
					int commentNum = DbUp.upTable("nc_comment").count("info_code",mDataMap.get("info_code"),"create_member", userCode);
					
					List<MDataMap> mdata = DbUp.upTable("nc_comment").queryIn("1", "-create_time", "info_code='"+mDataMap.get("info_code")+"' and create_member='"+userCode+"' and manage_code='"+getManageCode()+"'", new MDataMap(), -1, -1, "flag_show", "4497172100030001,4497172100030003");
					num += mdata.size();
					
					//查出是否报名过             
					MDataMap mBmMap = DbUp.upTable("nc_registration").one("info_member",userCode,"info_code",mDataMap.get("info_code"));
					
                    if(mLikeMap!=null){
						
						activity.setLiked(1);                 //我是否喜欢过
					}
					if(mFavMap!=null){
						
						activity.setFaved(Integer.valueOf(mFavMap.get("flag_enable")));                 //我是否收藏过
					}else{
						
						activity.setFaved(0); 
					}
					if(mShareMap!=null){
	
						activity.setShared(1);               //我是否分享过
					}
					if(commentNum!=0){
	
						activity.setCommented(1);           //我是否评论过
					}
					
					if(mBmMap!=null){
						
						activity.setJoined(Integer.valueOf(mBmMap.get("is_enable")));                        //我是否报名了
						//如果我没报名  我可以报名
						if(mBmMap.get("is_enable").equals("0")){
							 
							activity.setCan_join(1);                                      //我可以报名
							activity.setJoined_at("");                                    //报名时间
						}else{
							
							activity.setCan_join(0);                                      //我不可以报名
							activity.setJoined_at(mBmMap.get("registration_time"));       //报名时间
						}
						                                  
						
					}else{
						//报名信息为空
						activity.setJoined(0);                                       //我没报过名
						activity.setCan_join(1);                                     //我可以报名
						activity.setJoined_at("");                                    //报名时间
					}
					
					}
					
					
					//查出多少人报名过
					MDataMap mWhereMap2=new MDataMap();
					mWhereMap2.put("info_code",mDataMap.get("info_code"));
					MPageData mBmInfoMap =DataPaging.upPageData("nc_registration", "", "", mWhereMap2, inputParam.getPaging());
					
					
					
					
					activity.setApply_count(mBmInfoMap.getListData().size());
					activity.setId(mDataMap.get("info_code"));                 //id
					activity.setTitle(mDataMap.get("info_title"));             //标题
					activity.setText(mDataMap.get("info_content"));             //内容
					activity.setLike_count(Integer.valueOf(mDataMap.get("num_like")));            //喜欢人数
					activity.setComment_count(num);       //评论人数
					activity.setFav_count(Integer.valueOf(mDataMap.get("num_favorite")));          //收藏人数
					activity.setShare_count(Integer.valueOf(mDataMap.get("num_share")));           //分享人数
					activity.setCreated_at(mDataMap.get("create_time"));
					activity.setLevel(mDataMap.get("min_level"));                        //限制等级
					
					//根据限制等级编号查出限制等级名称
					MDataMap XLevelMap = DbUp.upTable("mc_member_level").one("level_code",mDataMap.get("min_level"));
					if(XLevelMap!=null){
						
						activity.setLevel_name(XLevelMap.get("level_name"));                         //限制等级名称
					}
					
					/*获取单图或者多图*/
					
					String album = mDataMap.get("photos");
					
					/* 分享链接 */
					activity.setLinkUrl(bConfig("newscenter.shareLink")+"/capp/web/introduction/shareActivity.ftl?activity="+mDataMap.get("info_code"));
					
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
					activity.setPhoto(photos);
					}
					
					activity.getLocation().setName( mDataMap.get("address"));           //位置信息     
					
					
					//经纬度
					WebClientSupport webClientSupport = new WebClientSupport();
					String sResponse = "";
					try {
						sResponse = webClientSupport
								.doGet("http://api.map.baidu.com/geocoder/v2/?address='"+mDataMap.get("address")+"'&output=json&ak=479efee5e54b5d3f6cd724008a81659b");
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
					
					//转成实体类
					JsonHelper<Location> jsonHelper=new JsonHelper<Location>();
					Location location=jsonHelper.StringToObj(JSON.toJSONString(tmap), new Location());
					
					
					activity.getLocation().setLat(location.getLat());                                   //经度
					activity.getLocation().setLon(location.getLon());                                   //纬度
					
					//查出发起人信息                           
					MDataMap mUserMap = DbUp.upTable("mc_extend_info_star").one("member_code",mDataMap.get("create_member"));
					
					if(mUserMap!=null){
						
						//发起人信息
						activity.getUser().setMember_code(mUserMap.get("member_code"));                                   //用户编号
						activity.getUser().setNickname(mUserMap.get("nickname"));                                         //昵称
						activity.getUser().setMobile((mUserMap.get("mobile_phone")));                                    //电话
						activity.getUser().setGroup(BigInteger.valueOf(Long.valueOf(mUserMap.get("member_group"))));      //用户组
						activity.getUser().setGender(BigInteger.valueOf(Long.valueOf(mUserMap.get("member_sex"))));       //性别
						activity.getUser().setScore(Integer.parseInt(mUserMap.get("member_score")));                      //积分
						activity.getUser().getAvatar().setLarge(mUserMap.get("member_avatar"));                           //用户头像
						activity.getUser().getAvatar().setThumb(mUserMap.get("member_avatar"));
						activity.getUser().setScore_unit(bConfig("newscenter.Score_unit"));                               //积分单位
						activity.getUser().setLevel(Integer.valueOf(mUserMap.get("member_level").substring(mUserMap.get("member_level").length()-4,mUserMap.get("member_level").length())));                                        //等级
						//根据等级编号查出等级名称
						MDataMap mLevelMap = DbUp.upTable("mc_member_level").one("level_code",mUserMap.get("member_level"));
						if(mLevelMap!=null){
							
							activity.getUser().setLevel_name(mLevelMap.get("level_name"));                                 //等级名称
						}
						activity.getUser().setCreate_time(mUserMap.get("create_time"));                                    //加入时间
					}
					
					result.getActivities().add(activity);
				}
			}
	
			result.setPaged(mPageData.getPageResults());
			
		}
		return result;
	}

}
