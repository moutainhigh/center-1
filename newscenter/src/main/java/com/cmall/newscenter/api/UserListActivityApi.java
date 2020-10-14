package com.cmall.newscenter.api;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.cmall.newscenter.model.Activity;
import com.cmall.newscenter.model.CommentdityAppPhotos;
import com.cmall.newscenter.model.Location;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.UserListActivityInput;
import com.cmall.newscenter.model.UserListActivityResult;
import com.cmall.newscenter.util.DataPaging;
import com.cmall.systemcenter.model.AppPhoto;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 用户 - 我参与的活动
 * @author shiyz	
 * date 2014-7-31
 * @version 1.0
 */
public class UserListActivityApi extends RootApiForToken<UserListActivityResult, UserListActivityInput> {

	public UserListActivityResult Process(UserListActivityInput inputParam,
			MDataMap mRequestMap) {
		
		UserListActivityResult result = new UserListActivityResult();
		MPageData userActList= new MPageData();
		if(result.upFlagTrue()){
			List<MDataMap> wMDataMap = new ArrayList<MDataMap>();
			MDataMap whereInfoMember = new MDataMap();
			whereInfoMember.put("info_member", getUserCode());
			userActList = DataPaging.upPageData("nc_registration", "", "", whereInfoMember, inputParam.getPaging());
			
			result.setPaged(userActList.getPageResults());//分页信息
			wMDataMap = userActList.getListData();//用户参与的活动列表
			/*活动编号*/
			String info_code = "";

			/*判断该用户是否存在参与的活动*/
			if(wMDataMap.size()!=0){
				
				for(int i=0;i<wMDataMap.size();i++){
					
					MDataMap smDataMap = wMDataMap.get(i);
					
					info_code = smDataMap.get("info_code");
					Activity activities = new Activity();
					//活动详细信息
					MDataMap actDetail = DbUp.upTable("nc_info").one("info_code",info_code);
					if(null != actDetail){
						activities.setCreated_at(actDetail.get("create_time"));
						activities.setId(actDetail.get("info_code"));
						activities.getUser().setMember_code(actDetail.get("create_member"));
						activities.setText(actDetail.get("info_content"));
						
						activities.setTitle(actDetail.get("info_title"));
						
						
						/*获取单图或者多图*/
						
						String album = actDetail.get("photos");
						
						/* 分享链接 */
						activities.setLinkUrl(bConfig("newscenter.shareLink")+"/capp/web/introduction/shareActivity.ftl?activity="+actDetail.get("info_code"));
						
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
						activities.setPhoto(photos);
						}
						
						
						activities.setLevel(actDetail.get("min_level"));
						
						activities.setLike_count(Integer.valueOf(actDetail.get("num_like")));
						
						activities.setFav_count(Integer.valueOf(actDetail.get("num_favorite")));
						
						activities.setShare_count(Integer.valueOf(actDetail.get("num_share")));
						
						activities.setComment_count(Integer.valueOf(actDetail.get("num_comment")));
						
					    activities.setLevel(actDetail.get("min_level"));
					    
					  //经纬度
						WebClientSupport webClientSupport = new WebClientSupport();
						String sResponse = "";
						try {
							sResponse = webClientSupport
									.doGet("http://api.map.baidu.com/geocoder/v2/?address="+actDetail.get("address")+"&output=json&ak=479efee5e54b5d3f6cd724008a81659b");
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
						tmap.put("name", actDetail.get("address"));
						//转成实体类
						JsonHelper<Location> jsonHelper=new JsonHelper<Location>();
						Location location=jsonHelper.StringToObj(JSON.toJSONString(tmap), new Location());
						 activities.setLocation(location);
					}
					String sSql = "select * from mc_extend_info_star ms ,mc_member_level mg where ms.member_code =:member_code and ms.member_level = mg.level_code";
					MDataMap  mMemberMap = new MDataMap();
					mMemberMap.put("member_code", activities.getUser().getMember_code());
					mMemberMap.put("app_code",getManageCode());
				    Map<String, Object> map = DbUp.upTable("mc_extend_info_star").dataSqlOne(sSql, mMemberMap);
				    

				    if(null != map){
						activities.getUser().setNickname(String.valueOf(map.get("nickname")));
						
						activities.getUser().setGroup(BigInteger.valueOf(Long.valueOf(map.get("member_group").toString())));
						
						if(null == map.get("member_sex") || "".equals(map.get("member_sex"))){
							activities.getUser().setGender(BigInteger.valueOf(Long.valueOf("4497465100010001")));	
						}else{
							activities.getUser().setGender(BigInteger.valueOf(Long.valueOf(map.get("member_sex").toString())));       //性别
						}
						
						activities.getUser().setLevel(Integer.valueOf(map.get("member_level").toString().substring(map.get("member_level").toString().length()-4, map.get("member_level").toString().length())));
						
						activities.getUser().setLevel_name(String.valueOf(map.get("level_name").toString()));
						
						activities.getUser().setScore(Integer.valueOf(map.get("member_score").toString()));
						
						activities.getUser().setCreate_time(String.valueOf(map.get("create_time").toString()));
						
						activities.getUser().setMobile(String.valueOf(map.get("mobile_phone").toString()));
						
						String Score_unit = bConfig("newscenter.Score_unit");
						
						activities.getUser().setScore_unit(Score_unit);
						
						/*头像*/
						AppPhoto avatar = new AppPhoto();
						
						avatar.setLarge(String.valueOf(map.get("member_avatar").toString()));
						
						avatar.setThumb(String.valueOf(map.get("member_avatar").toString()));
						
						activities.getUser().setAvatar(avatar);
				    }

				  
				    /* 根据评价编号查询相关已审核通过评价的数量 */
					int commentNum = DbUp.upTable("nc_comment").count("info_code",info_code,"create_member", getUserCode());
					
					
					/*查询该活动用户是否喜欢过、收藏过、评论过、分享过*/
					List<MDataMap> ncNum = DbUp.upTable("nc_num").queryByWhere("num_code",info_code,"member_code",getUserCode());//查询该用户与该条活动相关信息
				    int isLike = 0;
				    int isFav = 0;
				    int isShare = 0;
				    if(ncNum.size() !=0){
					    for(MDataMap m: ncNum){
					    	if(m.get("num_type").equals("4497464900030003")){//喜欢过
					    		isLike = 1;
					    	}else if(m.get("num_type").equals("4497464900030005")){//收藏过
					    		isFav = 1;
					    	}else if(m.get("num_type").equals("4497464900030004")){//分享过
					    		isShare = 1;
					    	}
					    }
				    }
				    activities.setLiked(isLike);
				    activities.setFaved(isFav);
				    activities.setShared(isShare);
				    if(commentNum!=0){
				    	activities.setCommented(1);
				    }
				    
				    List<MDataMap> actList = DbUp.upTable("nc_registration").queryByWhere("info_code",info_code);//根据活动编码查出表中所有数据
				    activities.setApply_count(actList.size());

//				    activities.setLevel_name(level_name);
				    activities.setCan_join(1);
				    activities.setJoined(1);
				    activities.setJoined_at(smDataMap.get("registration_time"));

				    result.getActivities().add(activities);
				}
				
			}
		}
		return result;
	}

}
