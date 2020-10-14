package com.cmall.newscenter.beauty.api;

import java.util.List;

import com.cmall.newscenter.beauty.model.ProductPriceInput;
import com.cmall.newscenter.beauty.model.ProductPriceResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.cmall.productcenter.model.PcProductPrice;
import com.cmall.productcenter.service.ProductService;
import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.service.StoreService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 商品价格API
 * 
 * @author yangrong date 2014-9-20
 * @version 1.0
 */
public class ProductPriceApi extends RootApiForManage<ProductPriceResult, ProductPriceInput> {

	public ProductPriceResult Process(ProductPriceInput inputParam,MDataMap mRequestMap) {

		ProductPriceResult result = new ProductPriceResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

			String sku_code = inputParam.getSku_code();

			// 判断传入的是商品编码还是sku编码 如果传入的是商品编码 查出对应的sku编码（广告管理传入的是商品编码）
			if (sku_code.substring(0, 4).equals("8016") || sku_code.substring(0, 5).equals("i8016")) {
				MDataMap whereMap = new MDataMap();
				whereMap.put("product_code", sku_code);
				List<MDataMap> skucodelist = DbUp.upTable("pc_skuinfo").queryAll("sku_code", "", "", whereMap);
				if (skucodelist != null && !"".equals(skucodelist)&& skucodelist.size() != 0) {
					sku_code = skucodelist.get(0).get("sku_code");
				}
			}

			ProductService productService = new ProductService();

			PcProductPrice productPrice = productService.getSkuProductPrice(sku_code, getManageCode());

			result.setOldPrice(productPrice.getMarketPrice().toString());

			// 有活动价格显示活动价格 没有活动价格显示销售价
			if (("").equals(productPrice.getVipPrice())
					|| null == productPrice.getVipPrice()) {

				result.setNewPrice(productPrice.getSellPrice().toString());
			} else {

				result.setNewPrice(productPrice.getVipPrice());
			}
			result.setStartTime(productPrice.getStartTime());
			result.setEndTime(productPrice.getEndTime());
			StoreService storeService = new StoreService();
			if(AppConst.MANAGE_CODE_CAPP.equals(getManageCode())){
				result.setStock(String.valueOf(storeService.getStockNumByMaxFor7(sku_code)));
			}else if(AppConst.MANAGE_CODE_CYOUNG.equals(getManageCode())){
				result.setStock(String.valueOf(storeService.getStockNumByMaxFor13(sku_code)));
			}
			
			result.setRebate(productPrice.getDiscount());

			MDataMap mWhereMap = new MDataMap();
			MPageData mPageData = new MPageData();
			int num = 0;

			/* 将sku,app编号放入map中 查询商品评论数 */
			mWhereMap.put("order_skuid", sku_code);
			mWhereMap.put("manage_code", getManageCode());
			mWhereMap.put("check_flag", "4497172100030002");
			/* 根据app_code,sku_code查询商品评论列表 */
			mPageData = DataPaging.upPageData("nc_order_evaluation", "",
					"-oder_creattime", mWhereMap, new PageOption());
			if (mPageData != null) {

				num = mPageData.getListData().size();
			}

			result.setCommentCount(String.valueOf(num)); // 商品评论数

		}
		return result;
	}

}
