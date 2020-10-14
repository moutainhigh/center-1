package com.cmall.newscenter.beauty.api;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.cmall.newscenter.beauty.model.PostsAddInput;
import com.cmall.newscenter.beauty.model.PostsAddResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 帖子发布信息
 * @author houwen	
 * date 2014-08-26
 * @version 1.0
 */

public class PostsAddApi extends RootApiForToken<PostsAddResult, PostsAddInput> {

	public PostsAddResult Process(PostsAddInput inputParam,
			MDataMap mRequestMap) {

		PostsAddResult result = new PostsAddResult();
		if(result.upFlagTrue()){
			
			MDataMap mDataMap = new MDataMap();
			MDataMap mWhereMapUser = new MDataMap();
			MPageData mPageDataUser = new MPageData();
			//帖子相关信息
			//mDataMap.put("publisher_code", inputParam.getPostsAdd().getPublisher_code());
			
			mWhereMapUser.put("member_code", getUserCode());
			
			/*根据发布人ID查询发布人信息列表*/
			mPageDataUser = DataPaging.upPageData("mc_extend_info_star", "", "", mWhereMapUser,new PageOption());

			mDataMap.put("publisher_code", getUserCode());
			
			mDataMap.put("post_title", inputParam.getPost_title());
			
			mDataMap.put("post_content",inputParam.getPost_content());
			
			mDataMap.put("app_code", getManageCode());
			//评论时间 为系统时间
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
			
			mDataMap.put("publish_time",df.format(new Date()));
			
			mDataMap.put("post_label", inputParam.getPost_label());
			
			mDataMap.put("post_img", inputParam.getPost_img());
			
			mDataMap.put("product_code",inputParam.getProduct_code());
			
			mDataMap.put("status","449746730001"); //前台发布一条帖子，默认为上线状态
			
/*			mDataMap.put("post_browse","0");  //浏览量,默认为0

			mDataMap.put("post_praise","0");  //点赞量,默认为0
*/			
/*			mDataMap.put("sort","1");
	*/		
			mDataMap.put("issessence","449746770002"); //是否精华帖 449746770002：否，449746770001：是
			
			mDataMap.put("isofficial","449746760002"); //是否官方帖 449746760002：否，449746760001：是
			
			mDataMap.put("ishot","449746880002"); //是否火帖 449746880002：否，449746880001：是
			
			/*mDataMap.put("ispraise", "449746870002");   //是否点赞过
			
			mDataMap.put("iscollect", "449746860002");   //是否收藏过
*/			
			mDataMap.put("post_type","449746780001"); // 是否主/追帖 0：主帖，1：追帖
			
			mDataMap.put("post_catagory","4497465000020001"); // 栏目ID
			
			mDataMap.put("post_code",WebHelper.upCode("HML")); // 帖子ID
			/*将帖子信息放入数据库中*/
			DbUp.upTable("nc_posts").dataInsert(mDataMap);
			
		}
	
	
	return result;
	}

}
