package com.cmall.productcenter.process;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.cmall.productcenter.model.SellerCategory;
import com.cmall.productcenter.model.SellerCategoryForPhpResult;
import com.cmall.productcenter.model.SellerCategoryInput;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * ClassName:店铺私有商品分类Api接口ForPHP<br/>
 * Date:     2013-11-22 下午2:34:42 <br/>
 * @author   jack
 * @version  1.0
 */
public class SellerCategoryForPhpApi extends RootApi<SellerCategoryForPhpResult, SellerCategoryInput> {

	public SellerCategoryForPhpResult Process(SellerCategoryInput inputParam, MDataMap mRequestMap) {
		SellerCategoryForPhpResult result  = new SellerCategoryForPhpResult();
		String sellerCode = inputParam.getSeller_code();
		if(sellerCode!=null&&!"".equals(sellerCode)){
			MDataMap map = new MDataMap();
			map.put("seller_code", sellerCode);
			map.put("level", "2");
			map.put("flaginable", "449746250001");
			List<SellerCategory> pList = mapToList(DbUp.upTable("uc_sellercategory").queryAll("", "sort", "", map));
			map.put("level", "3");
			List<SellerCategory> cList = mapToList(DbUp.upTable("uc_sellercategory").queryAll("", "sort", "", map));
			result.setList(getCategories(pList, cList));
		}else{
			result.setResultCode(941901034);
			result.setResultMessage(bInfo(941901034));
		}
		return result;
	}
	
	private List<SellerCategory> getCategories(List<SellerCategory> pList,List<SellerCategory> cList){
		List<SellerCategory> all = new ArrayList<SellerCategory>();
		if(!pList.isEmpty()){
			Iterator<SellerCategory> iterator = pList.iterator();
			while (iterator.hasNext()) {
				SellerCategory category = (SellerCategory) iterator.next();
				Iterator<SellerCategory> it = cList.iterator();
				while (it.hasNext()) {
					SellerCategory sc = (SellerCategory) it.next();
					if(category.getCategoryCode()!=null&&!"".equals(category.getCategoryCode())&&sc.getParentCode()!=null&&category.getCategoryCode().equals(sc.getParentCode())){
						category.getChildren().add(sc);
					}
				}
				all.add(category);
			}
		}
		return all;
	}
	
	private List<SellerCategory> mapToList(List<MDataMap> list){
		List<SellerCategory> li = new ArrayList<SellerCategory>(); 
		if(!list.isEmpty()){
			for(int i=0;i<list.size();i++){
				MDataMap map = list.get(i);
				SellerCategory category = new SellerCategory();
				category.setCategoryCode(map.get("category_code"));
				category.setCategoryName(map.get("category_name"));
				category.setFlaginable(map.get("flaginable"));
				category.setLevel(map.get("level"));
				category.setParentCode(map.get("parent_code"));
				category.setSellerCode(map.get("seller_code"));
				category.setSort(map.get("sort"));
				li.add(category);
			}
		}
		return li;
	}
}

