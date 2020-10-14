package com.cmall.newscenter.beauty.api;

import com.cmall.newscenter.beauty.model.PostLabelList;
import com.cmall.newscenter.beauty.model.PostLabelListInput;
import com.cmall.newscenter.beauty.model.PostLabelListResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;


/**
 * 获取姐妹圈发帖标签信息列表
 * @author houwen
 * date 2014-09-28
 * @version 1.0
 */
public class PostLabelListApi extends RootApiForManage<PostLabelListResult, PostLabelListInput> {

	public PostLabelListResult Process(PostLabelListInput inputParam,
			MDataMap mRequestMap) {
		
		PostLabelListResult result = new PostLabelListResult();
		
		if(result.upFlagTrue()){
			
			MDataMap mWhereMap = new MDataMap();
			MPageData mPageData = new MPageData();
			mWhereMap.put("app_code", getManageCode());
				
			mPageData = DataPaging.upPageData("nc_label_manager", "", "", mWhereMap, new PageOption());
				
			if(mPageData.getListData().size()!=0){
					
					for(MDataMap mDataMap : mPageData.getListData()){
						
						PostLabelList postLabelList = new PostLabelList();
						
						postLabelList.setLabel_name(mDataMap.get("label_name"));
						
						result.getPostlabel().add(postLabelList);
					}
						
				}
		
		}
		return result;
	}

}
