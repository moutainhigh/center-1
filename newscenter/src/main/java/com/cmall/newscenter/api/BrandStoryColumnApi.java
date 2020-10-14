package com.cmall.newscenter.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.BrandStoryColumnInput;
import com.cmall.newscenter.model.BrandStoryColumnResult;
import com.cmall.newscenter.model.Column;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;
/**
 * 品牌
 * @author shiyz
 * date 2014-8-11
 * @version 1.0
 */
public class BrandStoryColumnApi extends RootApiForManage<BrandStoryColumnResult, BrandStoryColumnInput> {

	public BrandStoryColumnResult Process(BrandStoryColumnInput inputParam,
			MDataMap mRequestMap) {
		
		BrandStoryColumnResult result = new BrandStoryColumnResult();
		
		List<Column> columns = new ArrayList<Column>();
		String app_code = bConfig("newscenter.app_code");
		if(result.upFlagTrue()){
			
			List<MDataMap> mDataMaps = new ArrayList<MDataMap>();
			
			mDataMaps = DbUp.upTable("nc_category").queryByWhere("parent_code","4497465000010004","manage_code",app_code);
			
			if(mDataMaps.size()!=0){
				
				for(MDataMap mDataMap : mDataMaps){
					
					Column column = new Column();
					
					column.setId(mDataMap.get("category_code"));
					
					column.setName(mDataMap.get("category_name"));
					
					column.setType(0);
					
					columns.add(column);
					
					result.setColumns(columns);
					
				}
				
			}
			
		}
		
		return result;
	}

}
