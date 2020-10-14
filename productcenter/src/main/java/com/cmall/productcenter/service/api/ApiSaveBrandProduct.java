package com.cmall.productcenter.service.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.api.ApiBrandProductForInput;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootResultWeb;


/** 
* @ClassName: ApiSaveBrandProduct 
* @Description: 添加品牌特惠关联商品
* @author 张海生
* @date 2015-5-7 下午2:54:49 
*  
*/
public class ApiSaveBrandProduct extends RootApi<RootResultWeb,ApiBrandProductForInput> {
	
	public RootResultWeb Process(ApiBrandProductForInput input, MDataMap mRequestMap) {
		RootResultWeb result = new RootResultWeb();
		String infoCode = input.getInfoCode();
		String products[] = input.getProductCodes().split(",");
		try {
			MDataMap saveMap = new MDataMap();
			int count = 0;
			List<String> existCodes = new ArrayList<String>();//存放已存在的商品编号
			for (int i = 0; i < products.length; i++) {//查询专题下商品是否已经存在
				count = DbUp.upTable("pc_brand_rel_product").count("product_code",products[i], "info_code", infoCode);
				if(count > 0) {
					existCodes.add(products[i]);
					continue;
				}
			}
			if(existCodes.size() > 0){//已存在商品
				result.inErrorMessage(941901109, StringUtils.join(existCodes, ","));
			}else{
				for (String productCode : products) {
					saveMap.put("uid", WebHelper.upUuid());
					saveMap.put("info_code", infoCode);
					saveMap.put("product_code", productCode);
					DbUp.upTable("pc_brand_rel_product").dataInsert(saveMap);//插入关联商品
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
