package com.cmall.productcenter.process;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.cmall.productcenter.model.SellerCategoryInput;
import com.cmall.productcenter.model.SellerCategoryResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;

/**
 * ClassName:店铺私有商品分类Api<br/>
 * Date:     2013-10-21 下午1:34:42 <br/>
 * @author   jack
 * @version  1.0
 */
public class SellerCategoryApi extends RootApi<SellerCategoryResult, SellerCategoryInput> {

	public SellerCategoryResult Process(SellerCategoryInput inputParam, MDataMap mRequestMap) {
		SellerCategoryResult result  = new SellerCategoryResult();
		//当前用户所属店铺编号
		String sellerCode = UserFactory.INSTANCE.create().getManageCode();
		if(StringUtils.isNotEmpty(inputParam.getSeller_code())){
			sellerCode = inputParam.getSeller_code();
		}
		
		// 默认现在全部层级
		int maxLevel = NumberUtils.toInt(inputParam.getMaxLevel());
		if(maxLevel < 1) maxLevel = 99; 
		
		MDataMap map = new MDataMap();
		map.put("maxLevel", maxLevel+"");
		if("1".equals(inputParam.getShowAll())) {
			map.put("seller_code", sellerCode);
			result.setList(resultToList(DbUp.upTable("uc_sellercategory").queryAll("", "sort", "seller_code = :seller_code and level <= :maxLevel" , map)));
		}else {
			map.put("flaginable", "449746250001");
			map.put("seller_code", sellerCode);
			result.setList(resultToList(DbUp.upTable("uc_sellercategory").queryAll("", "sort", "flaginable = :flaginable and seller_code = :seller_code and level <= :maxLevel" , map)));
		}
		if(result.getList().isEmpty()){
			MDataMap insertMap = new MDataMap();
			String sUid = UUID.randomUUID().toString().replace("-", "");
			insertMap.put("uid", sUid);
			insertMap.put("seller_code", sellerCode);
			insertMap.put("category_code", "44971604");
			insertMap.put("category_name", "总分类");
			insertMap.put("parent_code", "4497");
			insertMap.put("sort", "44971604");
			insertMap.put("level", "1");
			insertMap.put("flaginable", "449746250001");
			DbUp.upTable("uc_sellercategory").dataInsert(insertMap);
			result.setList(resultToList(DbUp.upTable("uc_sellercategory").queryAll("category_code,category_name,parent_code,uid,sort,flaginable,level", "sort", "", map)));
		}
		return result;
	}
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

