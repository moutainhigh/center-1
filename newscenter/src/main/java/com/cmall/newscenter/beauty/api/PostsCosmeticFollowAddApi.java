package com.cmall.newscenter.beauty.api;

import java.util.List;
import com.cmall.newscenter.beauty.model.PostsCosmeticFollowAddInput;
import com.cmall.newscenter.beauty.model.PostsReplyAddResult;
import com.cmall.productcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 添加追帖妆品信息
 * @author houwen	
 * date 2015-01-21
 * @version 1.0
 */

public class PostsCosmeticFollowAddApi extends RootApiForToken<PostsReplyAddResult, PostsCosmeticFollowAddInput> {
	
	String userString = getUserCode();
	
	public PostsReplyAddResult Process(PostsCosmeticFollowAddInput inputParam,
			MDataMap mRequestMap) {

		PostsReplyAddResult result = new PostsReplyAddResult();
	
		if(result.upFlagTrue()){
			
		    List<String> cosmeticList =	inputParam.getCosmetic_code();
		    
		    if(cosmeticList.size()==0 || null == inputParam.getCosmetic_code() || inputParam.getCosmetic_code().equals("")){
				result.setResultCode(969905910);
				result.setResultMessage(bInfo(969905910,"妆品id"));
				return result;
			}
	
		    /**
		     * 追贴妆品显示：如果一次追贴从化妆包中增加多个妆品，以多个追贴的形式展示，有正文为追贴的第一条；没有正文的，追贴中的第一个妆品为追贴，依次排序。
		     *   如果追帖无正文，则将妆品依次存入追帖，且为正文
		     */
				if(null==inputParam.getPost_content() || inputParam.getPost_content().equals("")){
					for(int i =0;i<cosmeticList.size();i++){	
					this.insetFollowPosts(cosmeticList.get(i),inputParam.getPost_content(),inputParam.getPost_code(),getUserCode(),getManageCode());
					}
				}else {            
					
						/**
						 * 如果追帖有正文，第一条追帖有正文，无妆品，第二条帖子及以后，依次将妆品存入，并且无正文
						 */
						this.insetFollowPosts("",inputParam.getPost_content(),inputParam.getPost_code(),getUserCode(),getManageCode());
						for (int m = 0; m < cosmeticList.size(); m++) {
							this.insetFollowPosts(cosmeticList.get(m),"",inputParam.getPost_code(),getUserCode(),getManageCode());
						}	
					}
		}
	
	return result;
	}
	
	/**
	 * 存入追帖
	 * @param cosmetic_code
	 * @param post_content
	 * @param post_code
	 * @param userCode
	 * @param appCode
	 */
	public void insetFollowPosts(String cosmetic_code,String post_content ,String post_code,String userCode,String appCode){
		
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("publisher_code", userCode);
		mDataMap.put("post_code",WebHelper.upCode("HML")); // 帖子ID
		
		//根据帖子Id查询主帖信息，追帖默认主帖的标题等信息
		MDataMap mPageData = DbUp.upTable("nc_posts").one("post_code",post_code,"post_catagory", "4497465000020001","post_type", "449746780001","status", "449746730001","is_delete", "0","app_code", appCode);
		if(null !=mPageData){  //默认和主帖一致
			mDataMap.put("post_title",mPageData.get("post_title"));
			
			mDataMap.put("issessence",mPageData.get("issessence")); //是否精华帖 449746770002：否，449746770001：是
			
			mDataMap.put("isofficial",mPageData.get("isofficial")); //是否官方帖 449746760002：否，449746760001：是
			
			mDataMap.put("ishot",mPageData.get("ishot")); //是否火帖 449746880002：否，449746880001：是
			
			mDataMap.put("post_label",mPageData.get("post_label")); //标签
		}
		mDataMap.put("publish_time",DateUtil.getSysDateTimeString());
		
		mDataMap.put("post_content",post_content);
		
		//根据妆品Id，查询妆品相关信息，传入帖子表
		MDataMap map = DbUp.upTable("nc_cosmetic_bag").one("cosmetic_code",cosmetic_code);
		if(null !=map){
			mDataMap.put("cosmetic_code", map.get("cosmetic_code"));
			String img[] = map.get("photo").split(",");
			mDataMap.put("post_img", img[0]);
			mDataMap.put("cosmetic_name", map.get("cosmetic_name"));
			mDataMap.put("cosmetic_price", map.get("cosmetic_price"));
			mDataMap.put("disabled_time", map.get("disabled_time"));
			mDataMap.put("count", map.get("count"));
			mDataMap.put("unit", map.get("unit"));
		}
	
		mDataMap.put("status","449746730001"); //前台发布一条帖子，默认为上线状态
		
		mDataMap.put("post_parent_code", post_code);
		
		mDataMap.put("app_code", appCode);
		
		mDataMap.put("post_type","449746780002"); // 是否主/追帖 449746780001：主帖，449746780002：追帖
		
		mDataMap.put("post_catagory","4497465000020001"); // 栏目ID
		/*将帖子信息放入数据库中*/
		DbUp.upTable("nc_posts").dataInsert(mDataMap);
		
	}
	

}
