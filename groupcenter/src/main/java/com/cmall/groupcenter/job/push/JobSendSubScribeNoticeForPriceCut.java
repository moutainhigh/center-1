package com.cmall.groupcenter.job.push;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.xmasorder.model.ShoppingCartCache;
import com.srnpr.xmasorder.model.ShoppingCartCacheInfo;
import com.srnpr.xmasorder.model.ShoppingCartGoodsInfoForAdd;
import com.srnpr.xmasorder.service.ShopCartServiceForCache;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuQuery;
import com.srnpr.xmassystem.service.ProductPriceService;
import com.srnpr.xmassystem.support.PlusSupportProduct;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.webwx.WxGateSupport;

public class JobSendSubScribeNoticeForPriceCut  extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		String sql = "SELECT * FROM newscenter.nc_push_news_subscribe_pro WHERE user_code != '' AND subscribe_type in ('449748680002','449748680003')  AND if_post = 0 GROUP BY open_id,product_code";
		List<Map<String,Object>> pushNews = DbUp.upTable("nc_push_news_subscribe_pro").dataSqlList(sql, new MDataMap());
		if(pushNews == null || pushNews.size() <= 0) {
			return;
		}
		for(Map<String,Object> map : pushNews) {
			MDataMap mmap = new MDataMap(map);
			boolean flag = this.pushNews(mmap);
			if(flag) {
				mmap.put("if_post", "1");
				DbUp.upTable("nc_push_news_subscribe_pro").dataUpdate(mmap, "if_post", "user_code,product_code");
			}
		}
		
	}

	/**
	 * 消息推送逻辑处理
	 * @param mmap
	 */
	private boolean pushNews(MDataMap mmap) {
		String open_id = mmap.get("open_id");
		String productCode = mmap.get("product_code");
		String userCode = mmap.get("user_code");
		if(StringUtils.isEmpty(userCode)) {
			return false;
		}
		BigDecimal saveValue = BigDecimal.ZERO;
		RootResult rootResult = this.checkIfPost(productCode,userCode,saveValue);
		if(rootResult.getResultCode()!=1) {
			return false;
		}
		String receivers  = open_id+ "|13||/pages/product_detail/product_detail?pid="+productCode;
		String thing1 = new PlusSupportProduct().upProductInfo(productCode).getProductName();
		if(thing1.length()>20) {
			thing1 = thing1.substring(0,17)+"...";
		}
		String defaultS = bConfig("groupcenter.push_price_cut");
		String message = "{\"thing1\":{\"value\":\""+thing1+"\"},\"thing4\":{\"value\":\""+rootResult.getResultMessage()+"\"},\"thing6\":{\"value\":\""+defaultS+"\"}}";
		MDataMap logMap = new MDataMap();
		WxGateSupport wxGateSupport = new WxGateSupport();
		logMap.put("create_time", DateUtil.getNowTime());
		String result = wxGateSupport.sendMsgForNotice(receivers, message);
		logMap.put("uid", UUID.randomUUID().toString().replace("-", "").trim());
		logMap.put("request_date", "{\"open_id\":"+open_id+",\"message\":"+message+"}");
		logMap.put("url", "Subscribe");
		logMap.put("response_data", result);
		logMap.put("push_target", "Subscribe");
		logMap.put("api_input", "{\"open_id\":"+open_id+",\"message\":"+message+"}");
		logMap.put("response_time", DateUtil.getNowTime());
		DbUp.upTable("lc_push_news_log").dataInsert(logMap);
		return true;
	}

	/**
	 * 校验是否发提醒。
	 * @param productCode
	 * @param userCode
	 * @return
	 * 2020-8-6
	 * Angel Joy
	 * boolean
	 */
	private RootResult checkIfPost(String productCode, String userCode,BigDecimal saveValue) {
		RootResult result = new RootResult();
		//获取收藏或加购物车时的价格
		Integer carCount = DbUp.upTable("nc_push_news_subscribe_pro").count("product_code",productCode,"user_code",userCode,"subscribe_type","449748680003","if_post","0");//购物车
		Integer collectCount = DbUp.upTable("nc_push_news_subscribe_pro").count("product_code",productCode,"user_code",userCode,"subscribe_type","449748680002","if_post","0");//购物车
		ShopCartServiceForCache serviceForCache = new ShopCartServiceForCache();
		BigDecimal orgPrice = new BigDecimal("99999999");
		BigDecimal orgColPrice = new BigDecimal("99999999");
		String skuCode = "";
		if(carCount > 0) {
			ShoppingCartCacheInfo info = serviceForCache.queryShopCart(userCode,new ArrayList<ShoppingCartGoodsInfoForAdd>());
			for(ShoppingCartCache good : info.getCaches()) {
				if(productCode.equals(good.getProduct_code())) {
					if(good.getSku_add_shop_price().compareTo(orgPrice) <= 0) {
						orgPrice = good.getSku_add_shop_price();
						skuCode = good.getSku_code();
					}
				}
			}
		}
		if(collectCount > 0) {//也收藏了
			String sql = "SELECT * FROM familyhas.fh_product_collection WHERE product_code = :product_code AND member_code = :member_code AND operate_type = '4497472000020001' limit 1";
			Map<String,Object> collectProductMap = DbUp.upTable("fh_product_collection").dataSqlOne(sql, new MDataMap("product_code",productCode,"member_code",userCode));
			if(collectProductMap != null) {
				BigDecimal coPrice = new BigDecimal(MapUtils.getString(collectProductMap, "sku_price","0.00"));
				if(coPrice.compareTo(orgColPrice) <= 0 && coPrice.compareTo(BigDecimal.ZERO) > 0) {
					orgColPrice = coPrice;
					skuCode = MapUtils.getString(collectProductMap, "sku_code","");
				}
			}
		}
		//先判断改商品是否降价了
		PlusModelSkuInfo upSkuInfoBySkuCode = new PlusSupportProduct().upSkuInfoBySkuCode(skuCode,userCode,"",1);
		BigDecimal sellPrice = upSkuInfoBySkuCode.getSellPrice();
		if("4497472600010006".equals(upSkuInfoBySkuCode.getEventType())) {
			result.setResultCode(0);
			return result;
		}
		if(orgPrice.compareTo(new BigDecimal("99999999")) <0 && orgPrice.compareTo(sellPrice) > 0) {//需要发送通知
			saveValue = orgPrice.subtract(sellPrice);
			result.setResultCode(1);
			result.setResultMessage(saveValue.toString());
			return result;
		}
		if(orgColPrice.compareTo(new BigDecimal("99999999")) <0 && orgColPrice.compareTo(sellPrice) > 0) {//需要发送通知
			saveValue = orgColPrice.subtract(sellPrice);
			result.setResultCode(1);
			result.setResultMessage(saveValue.toString());
			return result;
		}
		result.setResultCode(0);
		return result;
	}
	
}
