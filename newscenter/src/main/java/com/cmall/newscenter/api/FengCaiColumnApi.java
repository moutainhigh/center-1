package com.cmall.newscenter.api;

import com.cmall.newscenter.model.Column;
import com.cmall.newscenter.model.FengCaiColumnInput;
import com.cmall.newscenter.model.FengCaiColumnResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;
/**
 * 风采栏目列表Api
 * @author lqiang
 * date: 2014-07-10
 * @version1.0
 */
public class FengCaiColumnApi extends RootApiForManage<FengCaiColumnResult, FengCaiColumnInput> {
	/**
	 * @author yangrong
	 */
	public FengCaiColumnResult Process(FengCaiColumnInput inputParam,
			MDataMap mRequestMap) {
		FengCaiColumnResult result = new FengCaiColumnResult();
		String app_code = bConfig("newscenter.app_code");
		// 设置相关信息
		if (result.upFlagTrue()) {
			
			MDataMap mWhereMap=new MDataMap();
			
			mWhereMap.put("parent_code","4497465000010002");
			mWhereMap.put("manage_code",app_code);
			//根据父分類查出栏目信息
			MPageData mPageData=DataPaging.upPageData("nc_category", "", "", mWhereMap, inputParam.getPaging());
			
			for( MDataMap mDataMap: mPageData.getListData())
			{
				Column column = new Column();
				
				column.setId(mDataMap.get("category_code"));
				column.setName(mDataMap.get("category_name"));
				
				if(mDataMap.get("category_name").equals("动态")){
					
					column.setType(0);		
					
				}else if(mDataMap.get("category_name").equals("新闻")){
					
					column.setType(0);	
					
				}else if(mDataMap.get("category_name").equals("行程")){
					
					column.setType(3);
					
				}else if(mDataMap.get("category_name").equals("介绍")){
					
					column.setType(2);
					
				}else if(mDataMap.get("category_name").equals("影视")){
					
					column.setType(0);
					
				}else if(mDataMap.get("category_name").equals("相册")){
					
					column.setType(1);	
					
				}else if(mDataMap.get("category_name").equals("公益")){
					
					column.setType(0);	
					
				}
				
				result.getColumns().add(column);
			
			}
		}
		return result;
	}

}
