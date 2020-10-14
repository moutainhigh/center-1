package com.cmall.newscenter.beauty.api;

import com.cmall.newscenter.beauty.model.ProductCategory;
import com.cmall.newscenter.beauty.model.ProductCategoryResult;
import com.cmall.newscenter.beauty.model.ProductCategoryInput;
import com.cmall.newscenter.beauty.model.Sort;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 惠美丽_商品分类列表Api
 * @author yangrong
 * date: 2014-09-11
 * @version1.0
 */
public class ProductCategoryApi extends RootApiForManage<ProductCategoryResult, ProductCategoryInput>{
	public ProductCategoryResult Process(ProductCategoryInput inputParam,
			MDataMap mRequestMap) {
		
		ProductCategoryResult result = new ProductCategoryResult();
		
		// 设置相关信息
		if (result.upFlagTrue()) {
			
			MDataMap mWhereMap=new MDataMap();
			
			mWhereMap.put("seller_code", getManageCode());
			//查出分类列表
			MPageData mPageData=DataPaging.upPageData("uc_sellercategory", "", "", mWhereMap, new PageOption());
			
			for( MDataMap mDataMap: mPageData.getListData()){
				
				if(mDataMap.get("category_code").length()==12||mDataMap.get("category_code").length()==8){
					
					if(mDataMap.get("flaginable").equals("449746250001")){
						
						ProductCategory pc = new ProductCategory();
						
						pc.setId(mDataMap.get("category_code"));
						pc.setName(mDataMap.get("category_name"));
						
						result.getCategories().add(pc);
					}
				}
				
			}
			
			MDataMap WhereMap=new MDataMap();
			
			WhereMap.put("app_code", getManageCode());
			
			//查出排序列表
			MPageData mSortData=DataPaging.upPageData("nc_beautysort", "", "", WhereMap, new PageOption());
			
			if(mSortData!=null){
			
				for( MDataMap mdataMap: mSortData.getListData()){
					
					Sort sort = new Sort();
					
					sort.setId(mdataMap.get("sort_id"));
					sort.setName(mdataMap.get("sort_name"));
					
					result.getSort().add(sort);
					
				}
			}
			
		}
		return result;
	}

}

