package com.cmall.productcenter.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
public class SellerCategoryBrandsApi extends RootApi<MApiCategoryResult,SellerCategoryProductsInput> {

	public MApiCategoryResult Process(SellerCategoryProductsInput inputParam,
			MDataMap mRequestMap) {
		MApiCategoryResult result = new MApiCategoryResult();
		String categoryCode = inputParam.getCategoryCode();//私有类目编号
		String sellercode = UserFactory.INSTANCE.create().getManageCode();//店铺编号
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("category_code",categoryCode);
		mWhereMap.put("seller_code", sellercode);
		List<MDataMap> listP = DbUp.upTable("uc_sellercategory_brand_relation").queryAll("uid,brand_code,create_time", "create_time", "", mWhereMap);
		String sql = "";
		List<MDataMap> re =  new ArrayList<MDataMap>();
		MDataMap br = new MDataMap();
		if(!listP.isEmpty()){//获取对应的商品编号
			String brandCodes = "";
			Map<String, String> pu = new HashMap<String, String>();//key:product_code value:uid
			for(MDataMap m:listP){
				String brandCode = m.get("brand_code");
				br.put(brandCode, m.get("create_time"));
				if("".equals(brandCodes)){
					brandCodes+=" a.brand_code in ('" +brandCode+"'";
				}else{
					brandCodes+=",'"+brandCode+"'";
				}
				pu.put(brandCode, m.get("uid"));
			}
			if(!"".equals(brandCodes)){
				sql = sql + brandCodes+")";
			}
//			List<MDataMap> ps = DbUp.upTable("pc_brandinfo").queryAll("", "", sql, mWhereMap);
			String sqlStr = "select a.* from productcenter.pc_brandinfo a,usercenter.uc_sellercategory_brand_relation b where a.brand_code=b.brand_code and b.category_code=:category_code and b.seller_code=:seller_code and "+sql+" order by b.create_time";
			List<Map<String, Object>> ps = DbUp.upTable("pc_brandinfo").dataSqlList(sqlStr, mWhereMap);
			for(int i=0;i<ps.size();i++){
				Map<String, Object> ob = ps.get(i);
				ob.put("uid", pu.get(ob.get("brand_code")));
				ob.put("create_time", br.get(ob.get("brand_code")));
				Iterator<String> iterator = ob.keySet().iterator();
				MDataMap dataMap = new MDataMap();
				while (iterator.hasNext()) {
					String key = (String) iterator.next();
					if(ob.get(key)!=null){
						dataMap.put(key, ob.get(key).toString());
					}else{
						dataMap.put(key, "");
					}
				}
				re.add(i, dataMap);
			}
		}
		result.setListProperty(re);
		return result;
	}


}

