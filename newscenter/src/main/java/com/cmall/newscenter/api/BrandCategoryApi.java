package com.cmall.newscenter.api;

import com.srnpr.zapweb.webapi.RootApiForManage;
import com.cmall.newscenter.model.BrandCategoryInput;
import com.cmall.newscenter.model.BrandCategoryResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.model.Product_Category;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
/**
 * 品牌_分类列表Api
 * @author yangrong
 * date: 2014-07-10
 * @version1.0
 */
public class BrandCategoryApi extends RootApiForManage<BrandCategoryResult, BrandCategoryInput>{
	public BrandCategoryResult Process(BrandCategoryInput inputParam,
			MDataMap mRequestMap) {
		
		BrandCategoryResult result = new BrandCategoryResult();
		
		// 设置相关信息
		if (result.upFlagTrue()) {
			
			MDataMap mWhereMap=new MDataMap();
			
			mWhereMap.put("seller_code", getManageCode());
			//查出分类列表
			MPageData mPageData=DataPaging.upPageData("uc_sellercategory", "", "", mWhereMap, new PageOption());
			
			for( MDataMap mDataMap: mPageData.getListData()){
				
				if(mDataMap.get("category_code").length()==12){
					
					if(mDataMap.get("flaginable").equals("449746250001")){
						
						Product_Category pc = new Product_Category();
						
						pc.setId(mDataMap.get("category_code"));
						pc.setName(mDataMap.get("category_name"));
						pc.getIcon().setLarge(mDataMap.get("photo"));
						pc.getIcon().setThumb(mDataMap.get("photo"));
						
						result.getCategories().add(pc);
					}
				}
				
			}
		}
		return result;
	}

}
