package com.cmall.newscenter.beauty.api;


import com.cmall.newscenter.beauty.model.PostCollectResult;
import com.cmall.newscenter.beauty.model.PostPraiseInput;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 帖子收藏
 * @author houwen	
 * date 2014-09-10
 * @version 1.0
 */

public class PostsCollectApi extends RootApiForToken<PostCollectResult, PostPraiseInput> {

	public PostCollectResult Process(PostPraiseInput inputParam,
			MDataMap mRequestMap) {

		PostCollectResult result = new PostCollectResult();
		if(result.upFlagTrue()){
			
			//MDataMap mDataMap = new MDataMap();
			
			MDataMap minsertDataMap = new MDataMap();
			
			MDataMap mwhereDataMap = new MDataMap();
			
			MPageData mPageData = new MPageData();
			
			MDataMap mpostDataMap = new MDataMap();
			
			boolean count = true;
			int collect = 0;
            MDataMap mWhereMap = new MDataMap();
			
			mWhereMap.put("operater_code",getUserCode());
			
			mWhereMap.put("operate_type","4497464900030005");
			
			mWhereMap.put("info_code",inputParam.getPost_code());
			
			mWhereMap.put("app_code",getManageCode());
			
			MPageData moperatePageData = DataPaging.upPageData("nc_post_operate", "", "", mWhereMap,new PageOption());
			
			if(moperatePageData.getListData().size()!=0){
			for(MDataMap mDataMap : moperatePageData.getListData()){
				if(moperatePageData.getListData().get(0).get("flag").equals("1")){    //是否取消收藏： 0：是；1：否
					mDataMap.put("flag", "0");
					count = false;
				}else {
					mDataMap.put("flag", "1");
				}
				DbUp.upTable("nc_post_operate").update(mDataMap);
			}
			}else {
				//把收藏这一操作信息插入到表中  nc_post_operate
                minsertDataMap.put("operater_code", getUserCode());
				
				minsertDataMap.put("info_code", inputParam.getPost_code());
				
				minsertDataMap.put("operate_type","4497464900030005");   //操作类型：是执行的点赞还是收藏操作;  4497464900030006:点赞，4497464900030005：收藏 
				
				minsertDataMap.put("flag", "1");
				
				minsertDataMap.put("app_code", getManageCode());
				
				DbUp.upTable("nc_post_operate").dataInsert(minsertDataMap);
				
				//mpostDataMap.put("iscollect", "449746860001");  //是否被收藏过：449746860001： 被收藏过 ；未被收藏过:449746860002
			}
			
           //根据帖子ID向nc_posts表中添加收藏数   每操作一次  +1  或-1
			
            mwhereDataMap.put("post_code", inputParam.getPost_code());
			
			mPageData = DataPaging.upPageData("nc_posts", "", "", mwhereDataMap, new PageOption());
			
			if(mPageData.getListData().size()!=0){
			    collect = Integer.parseInt(mPageData.getListData().get(0).get("post_collect"));
				if(count){
					collect = collect+1;
				}else {
					collect = collect-1;
				}
			
			mpostDataMap.put("post_collect", String.valueOf(collect));
			
			mpostDataMap.put("post_code", inputParam.getPost_code());
			
			DbUp.upTable("nc_posts").dataUpdate(mpostDataMap, "", "post_code");
			result.setPost_collect(collect);
			}
			
			
		}
	
	return result;
	}

}
