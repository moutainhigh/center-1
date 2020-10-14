package com.cmall.newscenter.api;

import com.cmall.newscenter.model.Column;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.ShareColumnInput;
import com.cmall.newscenter.model.ShareColumnResult;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;
/**
 * 分享栏目列表Api
 * @author lqiang
 * date: 2014-07-10
 * @version1.0
 */
public class ShareColumnApi extends RootApiForManage<ShareColumnResult, ShareColumnInput> {

	/**
	 * @author yangrong
	 */
	public ShareColumnResult Process(ShareColumnInput inputParam,
			MDataMap mRequestMap) {
		ShareColumnResult result = new ShareColumnResult();
		String app_code = bConfig("newscenter.app_code");
		// 设置相关信息
		if (result.upFlagTrue()) {
			
			MDataMap mWhereMap=new MDataMap();
			
			mWhereMap.put("parent_code", "4497465000010003");
			mWhereMap.put("manage_code",app_code);
			//根据父分類查出栏目信息
			MPageData mPageData=DataPaging.upPageData("nc_category", "", "", mWhereMap, inputParam.getPaging());
			
			for( MDataMap mDataMap: mPageData.getListData())
			{
				Column column = new Column();
				
				column.setId(mDataMap.get("category_code"));
				column.setName(mDataMap.get("category_name"));
				
				if(mDataMap.get("category_name").equals("时尚")){
					
					column.setType(0);
					
				}else if(mDataMap.get("category_name").equals("美容")){
					
					column.setType(0);
					
				}else if(mDataMap.get("category_name").equals("养生")){
					
					column.setType(0);
					
				}else if(mDataMap.get("category_name").equals("家居")){
					
					column.setType(0);
					
				}
				
				
				result.getColumns().add(column);
			
			}
		}
		return result;
	}

}
