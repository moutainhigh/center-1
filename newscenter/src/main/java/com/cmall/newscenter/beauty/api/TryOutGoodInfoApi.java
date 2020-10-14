package com.cmall.newscenter.beauty.api;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.cmall.newscenter.beauty.model.TryOutGoodInfoInput;
import com.cmall.newscenter.beauty.model.TryOutGoodInfoResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.cmall.productcenter.model.PcFreeTryOutGood;
import com.cmall.productcenter.model.PcProductPrice;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForMember;

/**
 * 试用商品详情Api
 * 
 * @author yangrong date: 2014-09-16
 * @version1.0
 */
public class TryOutGoodInfoApi extends RootApiForMember<TryOutGoodInfoResult, TryOutGoodInfoInput> {

	public TryOutGoodInfoResult Process(TryOutGoodInfoInput inputParam,MDataMap mRequestMap) {

		TryOutGoodInfoResult result = new TryOutGoodInfoResult();
		MDataMap mWhereMap = new MDataMap();
		MPageData mPageData = new MPageData();
		int num = 0;
		// 设置相关信息
		if (result.upFlagTrue()) {

			String sku_code = inputParam.getSku_code();

			// 判断传入的是商品编码还是sku编码 如果传入的是商品编码 查出对应的sku编码（广告管理传入的是商品编码）
			if (sku_code.substring(0, 4).equals("8016") || sku_code.substring(0, 5).equals("i8016") ) {
				MDataMap whereMap = new MDataMap();
				whereMap.put("product_code", sku_code);
				List<MDataMap> skucodelist = DbUp.upTable("pc_skuinfo").queryAll("sku_code", "", "", whereMap);
				if (skucodelist != null && !"".equals(skucodelist)&& skucodelist.size() != 0) {
					sku_code = skucodelist.get(0).get("sku_code");
				}
			}

			ProductService productService = new ProductService();

			List<Map<String, Object>> product = productService.getMyTryOutGoodsForSkuCode(sku_code, "",getFlagLogin() ? getOauthInfo().getUserCode(): null, getManageCode(), inputParam.getEnd_time());
			for (Map<String, Object> map : product) {

				PcFreeTryOutGood free = (PcFreeTryOutGood) map.get("freeGood");
				String isFreeShipping = free.getIsFreeShipping();
				if (!StringUtils.isEmpty(isFreeShipping)) {
					if (isFreeShipping.equals("449746930002")) {
						
						if("SI2007".equals(getManageCode())){
							result.setLinkUrl(bConfig("systemcenter.shareLink")+ "/cbeauty/web/product/payProductDetail?sku_code="+ sku_code + "&end_time="+ URLEncoder.encode(inputParam.getEnd_time()));
						}else if("SI2013".equals(getManageCode())){
							result.setLinkUrl(bConfig("systemcenter.cyoungLink")+ "/cyoung/web/product/payProductDetail?sku_code="+ sku_code + "&end_time="+ URLEncoder.encode(inputParam.getEnd_time()));
						}
						
					} else {
						if("SI2007".equals(getManageCode())){
							result.setLinkUrl(bConfig("systemcenter.shareLink")+ "/cbeauty/web/product/tryProductDetail?sku_code="+ sku_code + "&end_time="+ URLEncoder.encode(inputParam.getEnd_time()));
						}else if("SI2013".equals(getManageCode())){
							result.setLinkUrl(bConfig("systemcenter.cyoungLink")+ "/cyoung/web/product/tryProductDetail?sku_code="+ sku_code + "&end_time="+ URLEncoder.encode(inputParam.getEnd_time()));
						}
					}
				}
				// 商品图片
				for (int i = 0; i < free.getpInfo().getPcPicList().size(); i++) {

					// result.getPhotos().add(free.getpInfo().getPcPicList().get(i).getPicUrl());
					if ("".equals(inputParam.getWidth())|| null == inputParam.getWidth()) {
						result.getPhotos().add(productService.getPicInfo(null, free.getpInfo().getPcPicList().get(i).getPicUrl()));

					} else {
						result.getPhotos().add(productService.getPicInfo(Integer.valueOf(inputParam.getWidth()),free.getpInfo().getPcPicList().get(i).getPicUrl()));

					}

				}
				result.setName(free.getSkuName());
				result.setSku_code(free.getSkuCode());

				PcProductPrice productPrice = productService.getSkuProductPrice(sku_code, getManageCode());

				result.setOldPrice(productPrice.getMarketPrice().toString());

				// 有活动价格显示活动价格 没有活动价格显示销售价
				if (("").equals(productPrice.getVipPrice())|| null == productPrice.getVipPrice()) {

					result.setNewPrice(productPrice.getSellPrice().toString());
				} else {

					result.setNewPrice(productPrice.getVipPrice());
				}
				result.setRebate(productPrice.getDiscount()); // 商品折扣

				result.setStock_num(map.get("skuSellNum").toString());
				result.setProduct_code(free.getpInfo().getProductCode());
				// 商品标签
				String[] labls = free.getpInfo().getLabels().split(",");

				if (labls != null) {
					for (int j = 0; j < labls.length; j++) {
						result.getLabels().add(labls[j]);
					}
				}
				// 产品详情图片
				String[] ptotos = free.getpInfo().getDescription().getDescriptionPic().split("\\|");

				if (ptotos != null) {
					for (int j = 0; j < ptotos.length; j++) {
						if ("".equals(inputParam.getWidth())|| null == inputParam.getWidth()) {
							result.getInfophotos().add(productService.getPicInfo(null, ptotos[j]));

						} else {
							result.getInfophotos().add(productService.getPicInfo(Integer.valueOf(inputParam.getWidth()),ptotos[j]));

						}

					}
				}

				/* 将sku,app编号放入map中 查询商品评论数 */
				mWhereMap.put("order_skuid", sku_code);
				mWhereMap.put("manage_code", getManageCode());
				mWhereMap.put("check_flag", "4497172100030002");

				/* 根据app_code,sku_code查询商品评论列表 */
				mPageData = DataPaging.upPageData("nc_order_evaluation", "","-oder_creattime", mWhereMap, new PageOption());
				if (mPageData != null) {
					num = mPageData.getListData().size();
				}

				result.setComment_count(String.valueOf(num));

			}
		}
		return result;
	}
}
