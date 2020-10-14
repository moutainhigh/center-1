package com.cmall.productcenter.service.api;

import com.cmall.productcenter.model.api.ApiCheckRepeatSkuProInput;
import com.cmall.productcenter.model.api.ApiCheckRepeatSkuProResult;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
/**
 * 添加sku时判断商品下是否已经存在所选sku属性（商户后台js调用）
 * @author ligj
 *
 */
public class ApiCheckRepeatSkuPro extends RootApi<ApiCheckRepeatSkuProResult,ApiCheckRepeatSkuProInput>  {

	public ApiCheckRepeatSkuProResult Process(ApiCheckRepeatSkuProInput api,MDataMap mRequestMap) {
		ApiCheckRepeatSkuProResult result = new ApiCheckRepeatSkuProResult();
		String productCode = api.getProductCode();
		String colorPro = api.getColorPro();
		String stylePro = api.getStylePro();
		String colorProName = api.getColorProName();
		String styleProName = api.getStyleProName();
		result.setFlag(new ProductService().checkRepeatSku(productCode,null,null, colorPro, stylePro,colorProName,styleProName));
		return result;
	}

}
