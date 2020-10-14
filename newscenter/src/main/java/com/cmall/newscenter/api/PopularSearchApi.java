package com.cmall.newscenter.api;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.newscenter.model.CommentdityAppPhotos;
import com.cmall.newscenter.model.InforMationFeed;
import com.cmall.newscenter.model.InforMationFeedVideo;
import com.cmall.newscenter.model.PopularSearchInput;
import com.cmall.newscenter.model.PopularSearchResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basemodel.MObjMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForMember;
/**
 * 热门搜索
 * @author shiyz
 * date 2014-7-18
 * @version 1.0
 */
public class PopularSearchApi extends RootApiForMember<PopularSearchResult, PopularSearchInput>{


	public PopularSearchResult Process(PopularSearchInput inputParam,
			MDataMap mRequestMap) {
		PopularSearchResult result = new PopularSearchResult();
		String app_code = bConfig("newscenter.app_code");
		/*获取关键字*/
		String keyword = inputParam.getKeyword();
		
		MDataMap keymMap = new MDataMap();
		
		MDataMap keywordMap = new MDataMap();
		
		keywordMap.put("search_keyword", "%"+inputParam.getKeyword()+"%");
		
		if(getFlagLogin()){
			
		keymMap.put("search_usercode", getOauthInfo().getUserCode());
		
		}
        keymMap.put("search_keyword", inputParam.getKeyword());
		
		keymMap.put("search_catetime", FormatHelper.upDateTime());
		
		keymMap.put("search_appcode", getManageCode());
		
		DbUp.upTable("nc_record_search").dataInsert(keymMap);
		
		String sSql = "select * from nc_info where flag_show ='449746530001' and (info_title like:search_keyword or info_content like:search_keyword) and manage_code='"+app_code+"'";
		
		if(result.upFlagTrue()){
			
			List <Map<String,Object>>  mList =  DbUp.upTable("nc_info").dataSqlList(sSql, keywordMap);
			
		if(mList.size()!=0){
			
		    int size = mList.size();
			
		    Map<String, Object> urMapParam = new MObjMap<String, Object>();
			
				
			for( int i = 0;i<size;i++)
			{
				urMapParam = mList.get(i);
				InforMationFeed info=new InforMationFeed();
				
				/*资讯ID*/
				info.setId(String.valueOf(urMapParam.get("info_code")));
				
				/*所属分类*/
				info.setFeedId(String.valueOf(urMapParam.get("info_category")));
				
                String nickName = String.valueOf(urMapParam.get("create_user"));
				
				if(nickName!=""){
					/*发布者*/
				info.getUser().setNickname(bConfig("newscenter.nick_name"));
				
				}else{
					
				String memberCode = String.valueOf(urMapParam.get("create_member"));	
				
				String sql = "select * from mc_extend_info_star ms ,mc_member_level mg where ms.member_code =:member_code and ms.member_level = mg.level_code and ms.app_code = mg.manage_code and ms.app_code =:app_code";
				
				MDataMap  mberMap = new MDataMap();
				
				mberMap.put("member_code", memberCode);
				mberMap.put("app_code", app_code);
			    Map<String, Object> mMemberMap = DbUp.upTable("mc_extend_info_star").dataSqlOne(sql, mberMap);
			
			    if(mMemberMap!=null&&!mMemberMap.isEmpty()){
				
			    	info.getUser().setCreate_time(String.valueOf(mMemberMap.get("create_time")));
			    	
			    	info.getUser().setGender(BigInteger.valueOf(Long.valueOf((String.valueOf(mMemberMap.get("member_sex"))))));
			    	
			    	info.getUser().setGroup(BigInteger.valueOf(Long.valueOf(String.valueOf(mMemberMap.get("member_group")))));
			    	
			    	info.getUser().setLevel(Integer.valueOf(mMemberMap.get("member_level").toString().substring(mMemberMap.get("member_level").toString().length()-4, mMemberMap.get("member_level").toString().length())));
			    	
			    	info.getUser().setLevel_name(String.valueOf(mMemberMap.get("level_name")));
			    	
			    	info.getUser().setMember_code(String.valueOf(mMemberMap.get("member_code")));
			    	
			    	info.getUser().setNickname(String.valueOf(mMemberMap.get("nickname")));
			    	
			    	info.getUser().setScore(Integer.valueOf(String.valueOf(mMemberMap.get("member_score"))));
					
			    	info.getUser().getAvatar().setLarge(String.valueOf(mMemberMap.get("member_avatar")));
			    	
			    	info.getUser().getAvatar().setThumb(String.valueOf(mMemberMap.get("member_avatar")));
			    	
			    	info.getUser().setScore_unit(bConfig("newscenter.Score_unit"));
			    	
					
				}
				
				}
				
				int num = DbUp.upTable("nc_comment").count("info_code",urMapParam.get("info_code").toString(),"manage_code", app_code,"flag_show", "4497172100030002");
				
				/*用户如果是登陆状态返回用户操作信息*/
				if(getFlagLogin()){
					
					/*用户编号*/
					String userCode = getOauthInfo().getUserCode();
					/*用户喜欢*/
					MDataMap likeMap = DbUp.upTable("nc_num").one("member_code",userCode,"num_code",urMapParam.get("info_code").toString(),"num_type","4497464900030003");
					
					if(likeMap!=null){
					
						info.setLiked(1);
						
					}
					
					/*用户分享*/
					MDataMap shareMap = DbUp.upTable("nc_num").one("member_code",userCode,"num_code",urMapParam.get("info_code").toString(),"num_type","4497464900030004");
					
					if(shareMap!=null){
						info.setShared(1);
					}
					
					/*用户收藏*/
					MDataMap favoriteMap = DbUp.upTable("nc_num").one("member_code",userCode,"num_code",urMapParam.get("info_code").toString(),"num_type","4497464900030005");
					
					if(favoriteMap!=null){
						info.setFaved(Integer.parseInt(favoriteMap.get("flag_enable")));
					}
					
					/* 根据评价编号查询相关已审核通过评价的数量 */
					int commentNum = DbUp.upTable("nc_comment").count("info_code",urMapParam.get("info_code").toString(),"create_member", userCode);
					
					if(commentNum!=0){
						info.setCommented(1);
					}
					
					List<MDataMap> mdata = DbUp.upTable("nc_comment").queryIn("1", "-create_time", "info_code='"+urMapParam.get("info_code").toString()+"' and create_member='"+userCode+"' and manage_code='"+app_code+"'", new MDataMap(), -1, -1, "flag_show", "4497172100030001,4497172100030003");
					num += mdata.size();
				}
				
				
				/*创建时间*/
				info.setCreated_at(String.valueOf(urMapParam.get("create_time")).toString());
				
				/*标题*/
				info.setTitle(String.valueOf(urMapParam.get("info_title")).toString());
				
				String info_content = "";
				
				info_content = String.valueOf(urMapParam.get("info_content")).toString();
				
				if(info_content.equals("null")){
					
					info_content="";
				}
				
				/*内容*/
				info.setText(info_content);
				
				/*相关链接*/
				info.setLink(String.valueOf(urMapParam.get("link_url")).toString());
				
				/*多少人喜欢过*/
				info.setLike_count(Integer.valueOf(urMapParam.get("num_like").toString()));
				
				/*多少人评论过*/
				info.setComment_count(num);
				
				/*多少人收藏过*/
				info.setFav_count(Integer.valueOf(urMapParam.get("num_favorite").toString()));
				
				/*多少人分享过*/
				info.setShare_count(Integer.valueOf(urMapParam.get("num_share").toString()));
				
				/*资讯类型*/
				info.setFeed_type(BigInteger.valueOf(Long.valueOf(String.valueOf(urMapParam.get("feed_type")))));
				
                String album = urMapParam.get("photos").toString();
				
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
				
               List<InforMationFeedVideo> videos = new ArrayList<InforMationFeedVideo>();
				
				InforMationFeedVideo video = new InforMationFeedVideo();
				
				video.setHd_url(String.valueOf(urMapParam.get("video_link")));
				
				video.setLd_url(String.valueOf(urMapParam.get("video_link")));
				
				video.setCover(String.valueOf(urMapParam.get("video_photos")));
				
				videos.add(video);
				
				info.setVideos(videos);
				
				
				result.getFeeds().add(info);
				
			}
			}
				
			}
		
		return result;
	}

}
