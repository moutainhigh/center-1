package com.cmall.productcenter.process;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.SellerCategoryThreeInput;
import com.cmall.productcenter.model.SellerCategoryThreeResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;

/**
 * ClassName:所有商品分类Api<br/>
 * @author   lgx
 */
public class SellerCategoryAllApi extends RootApi<SellerCategoryThreeResult, SellerCategoryThreeInput> {

	public SellerCategoryThreeResult Process(SellerCategoryThreeInput inputParam, MDataMap mRequestMap) {
		SellerCategoryThreeResult result  = new SellerCategoryThreeResult();
		
		String categoryName = "";
		String parentCategoryCode = "";
		
		String sellerCode = UserFactory.INSTANCE.create().getManageCode();
		if(StringUtils.isNotEmpty(inputParam.getSeller_code())){
			sellerCode = inputParam.getSeller_code();
		}
		MDataMap map = new MDataMap();
		map.put("seller_code", sellerCode);
		if(inputParam.getParentCode() == null || "".equals(inputParam.getParentCode()) || "44971604".equals(inputParam.getParentCode())) {
			map.put("parent_code", "44971604");
			MDataMap one = DbUp.upTable("uc_sellercategory").one("seller_code",sellerCode,"category_code","44971604");
			categoryName = one.get("category_name");
			parentCategoryCode = "44971604";
		}else {
			map.put("parent_code", inputParam.getParentCode());
			MDataMap one = DbUp.upTable("uc_sellercategory").one("seller_code",sellerCode,"category_code",inputParam.getParentCode());
			categoryName = one.get("category_name");
			parentCategoryCode = one.get("parent_code");
		}
		
		String whereCondition = "seller_code = :seller_code and parent_code = :parent_code";
		List<MDataMap> categoryList = DbUp.upTable("uc_sellercategory").queryAll("", "sort", whereCondition, map);
		
		result.setCategoryName(categoryName);
		result.setParentCategoryCode(parentCategoryCode);
		result.setList(categoryList);
		
		return result;
	}
}


