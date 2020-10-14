package com.cmall.newscenter.api;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.newscenter.model.CommentdityAppPhotos;
import com.cmall.newscenter.model.InforMationColumnInput;
import com.cmall.newscenter.model.InforMationColumnResult;
import com.cmall.newscenter.model.InforMationFeed;
import com.cmall.newscenter.model.InforMationFeedVideo;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForMember;
/**
 * 栏目 - 资讯列表API
 * @author shiyz
 * date 2014-7-8
 * @version 1.0
 */
public class InforMationColumnApi extends RootApiForMember<InforMationColumnResult, InforMationColumnInput> {

	public InforMationColumnResult Process(InforMationColumnInput inputParam,
			MDataMap mRequestMap) {

		InforMationColumnResult  result = new InforMationColumnResult();
		String app_code = bConfig("newscenter.app_code");
		
		if(result.upFlagTrue()){
			
			MDataMap mWhereMap=new MDataMap();
			
			mWhereMap.put("info_category", inputParam.getColumn());
			
			mWhereMap.put("flag_show", "449746530001");
			
			mWhereMap.put("manage_code",app_code);
			
			MPageData mPageData=DataPaging.upPageData("nc_info", "", "-sort_num,-create_time", mWhereMap, inputParam.getPaging());
			
			if(mPageData.getListData().size()!=0){
				
				for( MDataMap mDataMap: mPageData.getListData()){
					
					InforMationFeed info=new InforMationFeed();
					
					info.setId(mDataMap.get("info_code"));
					
					/*所属分类*/
					info.setFeedId(mDataMap.get("info_category"));
					
					String nickName = mDataMap.get("create_user");
					
					if(nickName!=""){
						/*发布者*/
					info.getUser().setNickname(bConfig("newscenter.nick_name"));
					
					}else{
						
					String memberCode = mDataMap.get("create_member");	
					
					String sSql = "select * from mc_extend_info_star ms ,mc_member_level mg where ms.member_code =:member_code and ms.member_level = mg.level_code and ms.app_code = mg.manage_code and ms.app_code =:app_code";
					
					MDataMap  mberMap = new MDataMap();
					
					mberMap.put("member_code", memberCode);
					mberMap.put("app_code", app_code);
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
					
					
					info.getUser().setCreate_time(mDataMap.get("create_time"));
					
					info.setTitle(mDataMap.get("info_title"));
					
					info.setLink(mDataMap.get("link_url"));
					
					info.setCreated_at(mDataMap.get("create_time"));
					
					info.setLike_count(Integer.valueOf(mDataMap.get("num_like").toString()));
					
					info.setFav_count(Integer.valueOf(mDataMap.get("num_favorite").toString()));
					
					info.setShare_count(Integer.valueOf(mDataMap.get("num_share").toString()));
					
					info.setComment_count(num);
					
					info.setFeed_type(BigInteger.valueOf(Long.valueOf(String.valueOf(mDataMap.get("feed_type")))));
					
					info.setText(mDataMap.get("info_content"));
	                /*获取原图链接*/
					
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
					/* 分享链接 */
					info.setLinkUrl(bConfig("newscenter.shareLink")+"/capp/web/introduction/shareEssay.ftl?feed="+mDataMap.get("info_code"));
					
					List<InforMationFeedVideo> videos = new ArrayList<InforMationFeedVideo>();
					
					InforMationFeedVideo video = new InforMationFeedVideo();
					
					
					video.setHd_url(mDataMap.get("video_link"));
					
					video.setLd_url(mDataMap.get("video_link"));
					
					video.setCover(mDataMap.get("video_photos"));
					
					videos.add(video);
					
					info.setVideos(videos);
					
					result.getFeeds().add(info);
					
				}

			}
			
			result.setPaged(mPageData.getPageResults());
			
		}
		return result;
		}
}
