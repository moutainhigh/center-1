package com.cmall.newscenter.beauty.api;

import java.util.List;
import java.util.Map;

import com.cmall.newscenter.beauty.model.ProductInfoOneInput;
import com.cmall.newscenter.beauty.model.ProductInfoOneResult;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.service.ProductService;
import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.service.StoreService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForMember;

/**
 * 商品详情 API
 * 
 * @author yangrong date 2014-9-15
 * @version 1.0
 */
public class ProductInfoOneApi extends RootApiForMember<ProductInfoOneResult, ProductInfoOneInput> {

	public ProductInfoOneResult Process(ProductInfoOneInput inputParam,MDataMap mRequestMap) {

		ProductInfoOneResult result = new ProductInfoOneResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

			String sku_code = inputParam.getSku_code();
			if("SI2007".equals(getManageCode())){
				result.setLinkUrl(bConfig("systemcenter.shareLink")+ "/cbeauty/web/product/productDetail?sku_code=" + sku_code);
			}else if("SI2013".equals(getManageCode())){
				result.setLinkUrl(bConfig("systemcenter.cyoungLink")+ "/cyoung/web/product/productDetail?sku_code=" + sku_code);
			}
			

			// 判断传入的是商品编码还是sku编码 如果传入的是商品编码 查出对应的sku编码（广告管理传入的是商品编码）
			if (sku_code.substring(0, 4).equals("8016") || sku_code.substring(0, 5).equals("i8016")) {
				MDataMap whereMap = new MDataMap();
				whereMap.put("product_code", sku_code);
				List<MDataMap> skucodelist = DbUp.upTable("pc_skuinfo")
						.queryAll("sku_code", "", "", whereMap);
				if (skucodelist != null && !"".equals(skucodelist) && skucodelist.size() != 0) {
					sku_code = skucodelist.get(0).get("sku_code");
				}
			}

			if (!getFlagLogin()) {
				result.setFavstatus("0");
			} else {

				// 查出是否收藏过
				MDataMap mFavMap = DbUp.upTable("nc_productfav").one("member_code", getOauthInfo().getUserCode(),"app_code", getManageCode(), "product_code", sku_code);

				if (mFavMap != null) {

					if (mFavMap.get("flag").equals("1")) {

						result.setFavstatus("1");
					} else {
						result.setFavstatus("0");
					}
				} else {
					result.setFavstatus("0");
				}
			}

			StoreService storeService = new StoreService();

			int store_num = 0;
			
			if(AppConst.MANAGE_CODE_CAPP.equals(getManageCode())){
				store_num = storeService.getStockNumByMaxFor7(sku_code);
			}else if(AppConst.MANAGE_CODE_CYOUNG.equals(getManageCode())){
				store_num = storeService.getStockNumByMaxFor13(sku_code);
			}
			

			result.setStore_num(String.valueOf(store_num));

			ProductService productService = new ProductService();

			Map<String, Object> resultMap = productService.getSkuView(getManageCode(), sku_code);

			PcProductinfo productinfo = (PcProductinfo) resultMap.get("productInfo");

			if (productinfo != null) {

				result.setProduct_code(productinfo.getProductCode());
				result.setSku_code(productinfo.getProductSkuInfoList().get(0).getSkuCode());
				result.setName(productinfo.getProductSkuInfoList().get(0).getSkuName());
				for (int i = 0; i < productinfo.getPcPicList().size(); i++) {

					if ("".equals(inputParam.getWidth()) || null == inputParam.getWidth()) {
						result.getPhotos().add(productService.getPicInfo(null, productinfo.getPcPicList().get(i).getPicUrl()));

					} else {
						result.getPhotos().add(productService.getPicInfo(Integer.valueOf(inputParam.getWidth()),productinfo.getPcPicList().get(i).getPicUrl()));

					}

				}

				// 产品详情图片
				String[] ptotos = productinfo.getDescription().getDescriptionPic().split("\\|");

				if (ptotos != null) {
					for (int j = 0; j < ptotos.length; j++) {
						if ("".equals(inputParam.getWidth()) || null == inputParam.getWidth()) {
							result.getInfophotos().add(productService.getPicInfo(null, ptotos[j]));

						} else {
							result.getInfophotos().add(productService.getPicInfo(Integer.valueOf(inputParam.getWidth()),ptotos[j]));

						}

					}
				}

				result.setStock_num(resultMap.get("skuSellNum").toString());
				result.setStatus(productinfo.getProductStatus().toString());

			}
		}
		return result;
	}

}
