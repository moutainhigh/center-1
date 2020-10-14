
package com.cmall.newscenter.api;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.newscenter.model.CommentdityAppPhotos;
import com.cmall.newscenter.model.InforMationFeed;
import com.cmall.newscenter.model.InforMationFeedDetailsInput;
import com.cmall.newscenter.model.InforMationFeedVideo;
import com.cmall.newscenter.model.InforMationFreedDetailsResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForMember;
/**
 * 资讯详情API
 * @author shiyz
 * date 2014-10-21
 * @version 1.0
 */
public class InforMationFeedDetailsApi extends RootApiForMember<InforMationFreedDetailsResult, InforMationFeedDetailsInput> {

	public InforMationFreedDetailsResult Process(InforMationFeedDetailsInput inputParam,
			MDataMap mRequestMap) {
		
		InforMationFreedDetailsResult result = new InforMationFreedDetailsResult();
		
		/*app编号*/
		String app_code = getManageCode();
		
		if(result.upFlagTrue()){
			
            
            MDataMap mDataMap = DbUp.upTable("nc_info").one("info_code",inputParam.getFeed());
			
            if(mDataMap!=null){
            
				InforMationFeed info=new InforMationFeed();
				
				/*资讯ID*/
				info.setId(mDataMap.get("info_code"));
				
				/*所属分类*/
				info.setFeedId(mDataMap.get("info_category"));
				
				String nickName = mDataMap.get("create_user");
				
				if(nickName!=""){
					/*发布者*/
				info.getUser().setNickname(bConfig("newscenter.nick_name"));
				
				}else{
					
				String memberCode = mDataMap.get("create_member");	
				
				String sSql = "select * from mc_extend_info_star ms ,mc_member_level mg where ms.member_code =:member_code and ms.member_level = mg.level_code and mg.manage_code =:manage_code";
				
				MDataMap  mberMap = new MDataMap();
				
				mberMap.put("member_code", memberCode);
				
				mberMap.put("manage_code", app_code);
			
			    Map<String, Object> mMemberMap = DbUp.upTable("mc_extend_info_star").dataSqlOne(sSql, mberMap);
			
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
				
				int num = DbUp.upTable("nc_comment").count("info_code",mDataMap.get("info_code"),"manage_code", app_code,"flag_show", "4497172100030002");
				
				/*用户如果是登陆状态返回用户操作信息*/
				if(getFlagLogin()){
					
					/*用户编号*/
					String userCode = getOauthInfo().getUserCode();
					/*用户喜欢*/
					MDataMap likeMap = DbUp.upTable("nc_num").one("member_code",userCode,"num_code",mDataMap.get("info_code"),"num_type","4497464900030003");
					
					if(likeMap!=null){
					
						info.setLiked(1);
						
					}
					
					/*用户分享*/
					MDataMap shareMap = DbUp.upTable("nc_num").one("member_code",userCode,"num_code",mDataMap.get("info_code"),"num_type","4497464900030004");
					
					if(shareMap!=null){
						info.setShared(1);
					}
					
					/*用户收藏*/
					MDataMap favoriteMap = DbUp.upTable("nc_num").one("member_code",userCode,"num_code",mDataMap.get("info_code"),"num_type","4497464900030005");
					
					if(favoriteMap!=null){
						info.setFaved(Integer.parseInt(favoriteMap.get("flag_enable")));
					}
					
					/* 根据评价编号查询相关已审核通过评价的数量 */
					int commentNum = DbUp.upTable("nc_comment").count("info_code",mDataMap.get("info_code"),"create_member", userCode);
					
					if(commentNum!=0){
						info.setCommented(1);
					}
					
					List<MDataMap> mdata = DbUp.upTable("nc_comment").queryIn("1", "-create_time", "info_code='"+mDataMap.get("info_code")+"' and create_member='"+userCode+"' and manage_code='"+app_code+"'", new MDataMap(), -1, -1, "flag_show", "4497172100030001,4497172100030003");
					num += mdata.size();
				}
				
				
				/*创建时间*/
				info.getUser().setCreate_time(mDataMap.get("create_time"));
				
				/*标题*/
				info.setTitle(mDataMap.get("info_title"));
				
				/*内容*/
				info.setText(mDataMap.get("info_content"));
				
				/*相关链接*/
				info.setLink(mDataMap.get("link_url"));
				
				/*多少人喜欢过*/
				info.setLike_count(Integer.valueOf(mDataMap.get("num_like")));
				
				/*多少人评论过*/
				info.setComment_count(Integer.valueOf(mDataMap.get("num_comment")));
				
				/*多少人收藏过*/
				info.setFav_count(Integer.valueOf(mDataMap.get("num_favorite")));
				
				/*多少人分享过*/
				info.setShare_count(Integer.valueOf(mDataMap.get("num_share")));
				
				info.setCreated_at(mDataMap.get("create_time"));
				
				/*资讯类型*/
				info.setFeed_type(BigInteger.valueOf(Long.valueOf(String.valueOf(mDataMap.get("feed_type")))));
				
				/*分享链接*/
				info.setLinkUrl((bConfig("newscenter.shareLink")+"/capp/web/introduction/shareEssay.ftl?feed="+mDataMap.get("info_code")));
				/*获取单图或者多图*/
				
				String album = mDataMap.get("photos");
				
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
				info.setPhotos(photos);
				}
				
				List<InforMationFeedVideo> videos = new ArrayList<InforMationFeedVideo>();
				
				String videoLink = "";
						
				videoLink =	mDataMap.get("video_link");
				
				String video_photos = "";
						
				video_photos = 	mDataMap.get("video_photos");
				
				InforMationFeedVideo video = new InforMationFeedVideo();
				
				video.setHd_url(videoLink);
				
				video.setCover(video_photos);
				
				video.setLd_url(videoLink);
				
				videos.add(video);
				
				info.setVideos(videos);
				
				result.setFeed(info);
			}
			}
		
		return result;
	}

}
