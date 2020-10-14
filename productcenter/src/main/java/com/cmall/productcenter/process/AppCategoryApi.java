package com.cmall.productcenter.process;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.cmall.productcenter.model.AppcategoryInput;

import com.cmall.productcenter.model.AppCategoryResult;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;

public class AppCategoryApi extends RootApi<AppCategoryResult, AppcategoryInput> {

	public AppCategoryResult Process(AppcategoryInput inputParam,
			MDataMap mRequestMap) {
		
		AppCategoryResult  result = new AppCategoryResult();
		
		
		/**获取当前所属APP*/
		String appCode =  UserFactory.INSTANCE.create().getManageCode();
		
		MDataMap map = new MDataMap();
		
		
		
		map.put("seller_code", appCode);
		/**查询所属APP是否存在数据，并将APP数据放入List中*/
		result.setList(resultToList(DbUp.upTable("uc_sellercategory").queryAll("", "sort", "", map)));
		/**判断是否存在数据，是：遍历数据并放入Map中，然后插入数据库中*/
		if(result.getList().isEmpty()){
			MDataMap inputMap = new MDataMap();
			String sUid = UUID.randomUUID().toString().replace("-", "");
			inputMap.put("uid", sUid);
			inputMap.put("seller_code", appCode);
			inputMap.put("category_code", "44971604");
			inputMap.put("category_name", "总分类");
			inputMap.put("parent_code", "4497");
			inputMap.put("sort", "44971604");
			inputMap.put("level", "1");
			inputMap.put("flaginable", "449746250001");
			DbUp.upTable("uc_sellercategory").dataInsert(inputMap);
			result.setList(resultToList(DbUp.upTable("uc_sellercategory").queryAll("category_code,category_name,parent_code,uid,sort,flaginable,level", "sort", "", map)));
			
		}
		return result;

	}

	/**将数据放入List中*/
	private List<List<String>> resultToList(List<MDataMap> dataMap){
		
		List<List<String>> list = new ArrayList<List<String>>();
		
		if(!dataMap.isEmpty()){
			for(MDataMap map:dataMap){
				List<String> li = new ArrayList<String>();
				li.add(map.get("category_code"));
				li.add(map.get("category_name"));
				li.add(map.get("parent_code"));
				li.add(map.get("uid"));
				li.add(map.get("sort"));
				li.add(map.get("flaginable"));
				li.add(map.get("level"));
				list.add(li);
			}
		}
		return list;
	}
}
