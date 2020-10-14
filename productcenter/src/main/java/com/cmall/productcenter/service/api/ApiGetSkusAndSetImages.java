package com.cmall.productcenter.service.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.model.api.ApiGetSkusInput;
import com.cmall.productcenter.model.api.ApiGetSkusResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebTemp;

/**
 * 该API专用于后台模板 如果SKU的主图没有信息则自动将商品的主图设置到SKU的主图
 * 
 * @author srnpr
 * 
 */
public class ApiGetSkusAndSetImages extends
		RootApi<ApiGetSkusResult, ApiGetSkusInput> {
	
	
	
	
	public ApiGetSkusResult Process(ApiGetSkusInput api, MDataMap mRequestMap) {
		ApiGetSkusResult result = new ApiGetSkus().Process(api, mRequestMap);

		if (result != null && result.getResultCode() == 1
				&& result.getSkuList().size() > 0) {

			List<ProductSkuInfo> lResutInfos = new ArrayList<ProductSkuInfo>();
			for (ProductSkuInfo pSkuInfo : result.getSkuList()) {

				
				if (StringUtils.isBlank(pSkuInfo.getSkuPicUrl())) {

					String sPic = WebTemp.upTempDataOne("pc_productinfo",
							"mainpic_url", "product_code",
							pSkuInfo.getProductCode());
					pSkuInfo.setSkuPicUrl(sPic);
				}
			
				
				if(StringUtils.isNotBlank(pSkuInfo.getSkuPicUrl()))
				{
					pSkuInfo.setSkuPicUrl(pSkuInfo.getSkuPicUrl().replace("/p0/", "/p1/"));
				}
				

				lResutInfos.add(pSkuInfo);
				
				//break;

			}
			
			
			
			 final String sSKuCode=api.getSkuStrs();
			Comparator comparator=new Comparator<ProductSkuInfo>() {

				public int compare(ProductSkuInfo o1, ProductSkuInfo o2) {
					// TODO Auto-generated method stub
					
					/*
					if(sSKuCode.indexOf(o1.getSkuCode())<sSKuCode.indexOf(o2.getSkuCode()))
					{
						return -1;
					}
					else {
						return 1;
					}*/;
					
					return sSKuCode.indexOf(o1.getSkuCode())-sSKuCode.indexOf(o2.getSkuCode());
				}
				
			};
			
			
			 Collections.sort(lResutInfos,	comparator);
			

			result.setSkuList(lResutInfos);
		}

		return result;
	}
}
