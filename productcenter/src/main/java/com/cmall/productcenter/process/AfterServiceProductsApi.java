package com.cmall.productcenter.process;

import com.cmall.productcenter.model.AppCategoryAddProductsInput;
import com.cmall.productcenter.model.AppCategoryAddProductsResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 *售后服务添加商品接口
 * @author   jack
 * @version  1.0
 */
public class AfterServiceProductsApi extends RootApi<AppCategoryAddProductsResult,AppCategoryAddProductsInput> {

	public AppCategoryAddProductsResult Process(AppCategoryAddProductsInput inputParam,
			MDataMap mRequestMap) {
		boolean flag = false;
		AppCategoryAddProductsResult result = null;
		try {
			result = new AppCategoryAddProductsResult();
			MDataMap minsert = new MDataMap();
			minsert.put("service_code", inputParam.getCategory_code());
			String [] productCodes = inputParam.getProduct_codes().split(",");
			for(int i=0;i<productCodes.length;i++){
				String product_code = productCodes[i];
				if(product_code!=null&&!"".equals(product_code)){
					minsert.put("product_code", product_code);
					if(minsert.containsKey("uid")){
						minsert.remove("uid");
					}
					DbUp.upTable("pc_service_product").dataInsert(minsert);
					flag = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		} 
		if(flag){
			result.setCode("909101001");
			result.setName("操作成功！");
		}else {
			result.setCode("909101014");
			result.setName("操作失败!");
		}
		return result;
	}
}

