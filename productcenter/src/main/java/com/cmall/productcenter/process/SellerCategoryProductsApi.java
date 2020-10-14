package com.cmall.productcenter.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cmall.productcenter.model.MApiCategoryResult;
import com.cmall.productcenter.model.SellerCategoryProductsInput;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;

/**
 *店铺私有类目下的商品
 * @author   jack
 * @version  1.0
 */
public class SellerCategoryProductsApi extends RootApi<MApiCategoryResult,SellerCategoryProductsInput> {

	public MApiCategoryResult Process(SellerCategoryProductsInput inputParam,
			MDataMap mRequestMap) {
		MApiCategoryResult result = new MApiCategoryResult();
		String categoryCode = inputParam.getCategoryCode();//私有类目编号
		String sellercode = UserFactory.INSTANCE.create().getManageCode();//店铺编号
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("category_code",categoryCode);
		mWhereMap.put("seller_code", sellercode);
		List<MDataMap> listP = DbUp.upTable("uc_sellercategory_product_relation").queryAll("uid,product_code", "", "", mWhereMap);
		String sql = " seller_code =:seller_code ";
		List<MDataMap> re =  new ArrayList<MDataMap>();
		if(!listP.isEmpty()){//获取对应的商品编号
			String productCodes = "";
			Map<String, String> pu = new HashMap<String, String>();//key:product_code value:uid
			for(MDataMap m:listP){
				String productCode = m.get("product_code");
				if("".equals(productCodes)){
					productCodes+=" and product_code in ('" +productCode+"'";
				}else{
					productCodes+=",'"+productCode+"'";
				}
				pu.put(productCode, m.get("uid"));
			}
			if(!"".equals(productCodes)){
				sql = sql + productCodes+")";
			}
			List<MDataMap> ps = DbUp.upTable("pc_productinfo").queryAll("", "", sql, mWhereMap);
			for(int i=0;i<ps.size();i++){
				MDataMap dataMap = ps.get(i);
				dataMap.put("uid", pu.get(dataMap.get("product_code")));
				re.add(i, dataMap);
			}
		}
		result.setListProperty(re);
		return result;
	}


}

