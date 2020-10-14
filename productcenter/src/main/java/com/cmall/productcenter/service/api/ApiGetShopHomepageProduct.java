package com.cmall.productcenter.service.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.productcenter.model.PcProductInfoForI;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.model.api.ApiGetShopHomepageProductInput;
import com.cmall.productcenter.model.api.ApiGetShopHomepageProductResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 
 * 项目名称：productcenter 
 * 类名称：     ApiGetProductBySeller
 * 类描述：     获取店家商品信息
 * 创建人：     GaoYang
 * 创建时间：2014年4月4日下午1:22:15
 * 修改人：     GaoYang
 * 修改时间：2014年4月4日下午1:22:15
 * 修改备注：
 *
 */
public class ApiGetShopHomepageProduct extends RootApi<ApiGetShopHomepageProductResult,ApiGetShopHomepageProductInput>{

	public ApiGetShopHomepageProductResult Process(
			ApiGetShopHomepageProductInput inputParam, MDataMap mRequestMap) {
		
		ApiGetShopHomepageProductResult result = new ApiGetShopHomepageProductResult();
		List<PcProductInfoForI> ret = new ArrayList<PcProductInfoForI>();
		SerializeSupport<PcProductInfoForI> sProduct = new SerializeSupport<PcProductInfoForI>();
		
		if(inputParam == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			//获取传入的卖家编号
			String sellerCode = inputParam.getSelleCode();
			//获取传入的数量
			int number = inputParam.getSize();
			
			if(number <= 0){
				number = 8;
			}
			
			String sOrders = "create_time desc";
			String sWhere = "seller_code = '" + sellerCode + "' and flag_sale = '1'";
			List<MDataMap> pListMap = DbUp.upTable("pc_productinfo").query("", sOrders, sWhere, null, 0, number);
			
			if (pListMap != null) {
				
				int size = pListMap.size();

				for (int j = 0; j < size; j++) {
					PcProductInfoForI pic = new PcProductInfoForI();
					sProduct.serialize(pListMap.get(j), pic);

					MDataMap skuMapParam = new MDataMap();
					skuMapParam.put("product_code", pic.getProductCode());
					pic.setProdutName(pListMap.get(j).get("product_name"));
					ret.add(pic);
					
					List<ProductSkuInfo> productSkuInfoList = new ArrayList<ProductSkuInfo>();
					
					List<MDataMap> itemMap = DbUp.upTable("pc_skuinfo")
							.query("sku_code,sku_picurl", "", "product_code=:product_code", skuMapParam, -1, -1);
					
					if(itemMap!=null){
						for(int i=0;i<itemMap.size();i++){
							
							ProductSkuInfo pItem = new ProductSkuInfo();
							pItem.setSkuCode(itemMap.get(i).get("sku_code"));
							pItem.setSkuPicUrl(itemMap.get(i).get("sku_picurl"));
							productSkuInfoList.add(pItem);
						}
					}
					pic.setProductSkuInfoList(productSkuInfoList);
				}
			}
			result.setProductList(ret);
		}
		return result;
	}
}
