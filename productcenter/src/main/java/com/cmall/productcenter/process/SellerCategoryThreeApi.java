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
 * ClassName:店铺私有商品分类Api<br/>
 * Date:     2013-10-21 下午1:34:42 <br/>
 * @author   jack
 * @version  1.0
 */
public class SellerCategoryThreeApi extends RootApi<SellerCategoryThreeResult, SellerCategoryThreeInput> {

	public SellerCategoryThreeResult Process(SellerCategoryThreeInput inputParam, MDataMap mRequestMap) {
		SellerCategoryThreeResult result  = new SellerCategoryThreeResult();
		
		String sellerCode = UserFactory.INSTANCE.create().getManageCode();
		if(StringUtils.isNotEmpty(inputParam.getSeller_code())){
			sellerCode = inputParam.getSeller_code();
		}
		MDataMap map = new MDataMap();
		map.put("flaginable", "449746250001");
		map.put("seller_code", sellerCode);
		if(inputParam.getParentCode() == null || "".equals(inputParam.getParentCode())) {
			map.put("parent_code", "44971604");
		}else {
			map.put("parent_code", inputParam.getParentCode());
		}
		
		String whereCondition = "flaginable = :flaginable and seller_code = :seller_code and parent_code = :parent_code";
		List<MDataMap> categoryList = DbUp.upTable("uc_sellercategory").queryAll("", "sort", whereCondition, map);
		result.setList(categoryList);
		return result;
	}
}


