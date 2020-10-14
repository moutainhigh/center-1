package com.cmall.newscenter.beauty.api;

import java.util.List;
import com.cmall.newscenter.beauty.model.PostsAddResult;
import com.cmall.newscenter.beauty.model.PostsCometicAddInput;
import com.cmall.productcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 帖子化妆品发布信息
 * @author houwen	
 * date 2015-01-21
 * @version 1.0
 */

public class PostsCosmeticAddApi extends RootApiForToken<PostsAddResult, PostsCometicAddInput> {

	public PostsAddResult Process(PostsCometicAddInput inputParam,
			MDataMap mRequestMap) {

		PostsAddResult result = new PostsAddResult();
		if(result.upFlagTrue()){
			
			MDataMap mDataMap = new MDataMap();

			PostsCosmeticFollowAddApi postsCosmeticFollowAddApi = new PostsCosmeticFollowAddApi();
			
		    List<String> cosmeticList =	inputParam.getCosmetic_code();
			if(cosmeticList.size()==0 || null == inputParam.getCosmetic_code() || inputParam.getCosmetic_code().equals("")){
				result.setResultCode(969905910);
				result.setResultMessage(bInfo(969905910,"妆品id"));
				return result;
			}
		    String post_code = WebHelper.upCode("HML");
		    /**
		     * 如主帖无正文，则主帖存入第一个妆品，且无正文；追帖存入妆品，且无正文
		     */
			if(null==inputParam.getPost_content() || inputParam.getPost_content().equals("")){
				
					mDataMap.put("cosmetic_code", cosmeticList.get(0));
					//根据妆品Id，查询妆品相关信息，传入帖子表
					MDataMap map = DbUp.upTable("nc_cosmetic_bag").one("cosmetic_code",cosmeticList.get(0));
					if(null !=map){
						String img[] = map.get("photo").split(",");
						mDataMap.put("post_img", img[0]);
						mDataMap.put("cosmetic_name", map.get("cosmetic_name"));
						mDataMap.put("cosmetic_price", map.get("cosmetic_price"));
						mDataMap.put("disabled_time", map.get("disabled_time"));
						mDataMap.put("count", map.get("count"));
						mDataMap.put("unit", map.get("unit"));
					}
					this.insertPost(inputParam.getPost_title(),inputParam.getPost_content(),inputParam.getPost_label(),post_code,mDataMap);
					if(cosmeticList.size()>1){
						for(int i = 1;i<cosmeticList.size();i++){
						postsCosmeticFollowAddApi.insetFollowPosts(cosmeticList.get(i),"",post_code,getUserCode(),getManageCode());
						}
					}
					
				}else { 
					    /**
					     * 如主帖有正文，则存入主帖；妆品依次存入追帖，且无正文
					     */
						this.insertPost(inputParam.getPost_title(),inputParam.getPost_content(),inputParam.getPost_label(),post_code,mDataMap);
						for(int j = 0;j<cosmeticList.size();j++){
							postsCosmeticFollowAddApi.insetFollowPosts(cosmeticList.get(j),"",post_code,getUserCode(),getManageCode());
							}
				}
		}
	return result;
	}

	/**
	 * 存入主帖
	 * @param title
	 * @param content
	 * @param label
	 * @param postCode
	 * @param mDataMap
	 */
	public void insertPost(String title,String content,String label,String postCode,MDataMap mDataMap){
		
		mDataMap.put("publisher_code", getUserCode());
		
		mDataMap.put("post_title", title);
		
		mDataMap.put("post_content",content);
		
		mDataMap.put("app_code", getManageCode());

		mDataMap.put("publish_time",DateUtil.getSysDateTimeString());
		
		mDataMap.put("post_label", label);
		
		mDataMap.put("status","449746730001"); //前台发布一条帖子，默认为上线状态
			
		mDataMap.put("issessence","449746770002"); //是否精华帖 449746770002：否，449746770001：是
		
		mDataMap.put("isofficial","449746760002"); //是否官方帖 449746760002：否，449746760001：是
		
		mDataMap.put("ishot","449746880002"); //是否火帖 449746880002：否，449746880001：是
		
		mDataMap.put("post_type","449746780001"); // 是否主/追帖 449746780001：主帖 ;449746780002：追帖
		
		mDataMap.put("post_catagory","4497465000020001"); // 栏目ID
		
		mDataMap.put("post_code",postCode); // 帖子ID
		/*将帖子信息放入数据库中*/
		DbUp.upTable("nc_posts").dataInsert(mDataMap);
	}
}
