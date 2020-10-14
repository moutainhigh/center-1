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
public class CategoryPropertyApi extends RootApi<MApiCategoryResult,SellerCategoryProductsInput> {

	public MApiCategoryResult Process(SellerCategoryProductsInput inputParam,
			MDataMap mRequestMap) {MApiCategoryResult result = new MApiCategoryResult();
			String categoryCode = inputParam.getCategoryCode();//私有类目编号
			MDataMap mWhereMap = new MDataMap();
			mWhereMap.put("category_code",categoryCode);
			List<MDataMap> listP = DbUp.upTable("pc_categoryproperty_rel").queryAll("uid,property_code", "", "", mWhereMap);
			List<MDataMap> re =  new ArrayList<MDataMap>();
			String sql = "";
			if(!listP.isEmpty()){//获取对应的商品编号
				String propertyCodes = "";
				Map<String, String> pu = new HashMap<String, String>();//key:property_code value:uid
				for(MDataMap m:listP){
					String propertyCode = m.get("property_code");
					if("".equals(propertyCodes)){
						propertyCodes+=" property_code in ('" +propertyCode+"'";
					}else{
						propertyCodes+=",'"+propertyCode+"'";
					}
					pu.put(propertyCode, m.get("uid"));
				}
				if(!"".equals(propertyCodes)){
					sql = sql + propertyCodes+")";
				}
				List<MDataMap> ps= DbUp.upTable("pc_propertyinfo").queryAll("", "", sql, mWhereMap);
				for(int i=0;i<ps.size();i++){
					MDataMap dataMap = ps.get(i);
					dataMap.put("uid", pu.get(dataMap.get("property_code")));
					re.add(i, dataMap);
				}
			}
			result.setListProperty(re);
			return result;
}


}

