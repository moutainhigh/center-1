package com.cmall.newscenter.beauty.api;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.cmall.newscenter.beauty.model.PostsFollowAddInput;
import com.cmall.newscenter.beauty.model.PostsReplyAddResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 添加追帖信息
 * @author houwen	
 * date 2014-08-26
 * @version 1.0
 */

public class PostsFollowAddApi extends RootApiForToken<PostsReplyAddResult, PostsFollowAddInput> {

	public PostsReplyAddResult Process(PostsFollowAddInput inputParam,
			MDataMap mRequestMap) {

		PostsReplyAddResult result = new PostsReplyAddResult();
	
		if(result.upFlagTrue()){
			
			MDataMap mDataMap = new MDataMap();
			MDataMap map  = new MDataMap();
			MPageData mPageData = new MPageData();
			
			mDataMap.put("publisher_code", getUserCode());
			mDataMap.put("post_code",WebHelper.upCode("HML")); // 帖子ID
			
			  /*根据帖子ID查询帖子列表*/
			map.put("post_code",inputParam.getPost_code());
			map.put("post_catagory", "4497465000020001");
			map.put("post_type", "449746780001"); //类型为主帖
			map.put("status", "449746730001");
			map.put("is_delete", "0");  //未被删除
			map.put("app_code", getManageCode());
			mPageData = DataPaging.upPageData("nc_posts", "", "", map, new PageOption());
			if(mPageData.getListData().size()!=0){  //默认和主帖子一致
				mDataMap.put("post_title",mPageData.getListData().get(0).get("post_title"));
				
				mDataMap.put("issessence",mPageData.getListData().get(0).get("issessence")); //是否精华帖 449746770002：否，449746770001：是
				
				mDataMap.put("isofficial",mPageData.getListData().get(0).get("isofficial")); //是否官方帖 449746760002：否，449746760001：是
				
				mDataMap.put("ishot",mPageData.getListData().get(0).get("ishot")); //是否火帖 449746880002：否，449746880001：是
				
				mDataMap.put("post_label",mPageData.getListData().get(0).get("post_label")); //标签
			}
			//评论时间 为系统时间
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
			
			mDataMap.put("publish_time",df.format(new Date()));
			
			mDataMap.put("post_content",inputParam.getComment_content());
			
			mDataMap.put("post_img", inputParam.getComment_img());    //多张图片，暂定以分号分隔
			
			mDataMap.put("product_code",inputParam.getProduct_code());
			
			mDataMap.put("status","449746730001"); //前台发布一条帖子，默认为上线状态
			
			mDataMap.put("post_parent_code", inputParam.getPost_code());
			
			mDataMap.put("app_code", getManageCode());
			
			mDataMap.put("post_type","449746780002"); // 是否主/追帖 449746780001：主帖，449746780002：追帖
			
			mDataMap.put("post_catagory","4497465000020001"); // 栏目ID
			/*将帖子信息放入数据库中*/
			DbUp.upTable("nc_posts").dataInsert(mDataMap);
		}
	
	return result;
	}

}
