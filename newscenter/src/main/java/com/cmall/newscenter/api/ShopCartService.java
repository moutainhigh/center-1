package com.cmall.newscenter.api;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cmall.newscenter.model.*;
import com.cmall.groupcenter.homehas.GetGoodGiftList;
import com.cmall.groupcenter.homehas.model.ModelGoodGiftInfo;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.common.OrderConst;
import com.cmall.ordercenter.model.OcOrderActivity;
import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.OrderAddress;
import com.cmall.ordercenter.model.OrderDetail;
import com.cmall.ordercenter.service.OrderService;
import com.cmall.productcenter.model.FlashsalesSkuInfo;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.model.PcPropertyinfoForFamily;
import com.cmall.productcenter.service.ProductService;
import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.service.StoreService;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 
 * 项目名称：familyhas 类名称：ShopCartService 类描述： 创建人：xiegj 创建时间：2014-09-16 上午11:03:25
 * 
 * @version 1.0
 * 
 */
public class ShopCartService extends BaseClass {

	/**
	 *添加商品进入购物车
	 * 
	 */
	public RootResult addSkuToShopCart(List<FamilyShopCart> list){
		RootResult rootResult = new RootResult();
		try {
			if(list!=null&&!list.isEmpty()){
				for(int i=0;i<list.size();i++){
					FamilyShopCart cart = list.get(i);
					ProductService service = new ProductService();
					//判断此商品是否存在
					PcProductinfo product = service.getskuinfo(cart.getSku_code(), cart.getProduct_code());
					if(product==null){
						deleteSkuForShopCart(cart.getBuyer_code(), cart.getSku_code());//删除已不存在的商品
						continue;
					}
					//判断购物车中是否存在本商品
					if(cart!=null){
						MDataMap insert = new MDataMap();
						insert.put("sku_num", String.valueOf(cart.getSku_num()));
						insert.put("shop_type", String.valueOf(cart.getShop_type()));
						insert.put("buyer_code", cart.getBuyer_code());
						insert.put("sku_code", cart.getSku_code());
						MDataMap one = DbUp.upTable("oc_shopCart").one("buyer_code",cart.getBuyer_code(),"sku_code", cart.getSku_code());
						if(one==null&&cart.getSku_num()>0){//不存在本商品的话新增
							insert.put("account_flag", String.valueOf(cart.getAccount_flag()));
							insert.put("add_time", DateUtil.getSysDateTimeString());
							insert.put("create_time", DateUtil.getSysDateTimeString());
							DbUp.upTable("oc_shopCart").dataInsert(insert);
						}else if(one!=null&&cart.getSku_num()>0){//已存在本商品的话更新本商品
							insert.put("account_flag", one.get("account_flag"));
							insert.put("sku_num", String.valueOf(cart.getSku_num()));
							insert.put("update_time", DateUtil.getSysDateTimeString());
							DbUp.upTable("oc_shopCart").dataUpdate(insert, "", "buyer_code,sku_code");
						}else if(cart.getSku_num()==0&&one!=null){
							deleteSkuForShopCart(cart.getBuyer_code(), cart.getSku_code());//删除的商品
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			rootResult.setResultCode(916401102);
			rootResult.setResultMessage(bInfo(916401102));
		}
		return rootResult;
	}
	

	/**
	 *获取用户购物车中的所有商品
	 * 
	 */
	public Map<String, Object> getSkuShopCart(String sellerCode,String buyerCode){
		Map<String, Object> result = new HashMap<String, Object>();
		List<GoodsInfoForQuery> list = new ArrayList<GoodsInfoForQuery>();
		int AccountAll = 0;//商品总个数
		int disableAccount = 0;//无效商品总个数
		List<MDataMap> skuLi = DbUp.upTable("oc_shopCart").queryAll("", "create_time DESC", "", new MDataMap("buyer_code",buyerCode));
		if(skuLi!=null&&!skuLi.isEmpty()){
			for (int i = 0; i < skuLi.size(); i++) {
				MDataMap map = skuLi.get(i);
				GoodsInfoForQuery gifq = new GoodsInfoForQuery();
				AccountAll = AccountAll+Integer.valueOf(map.get("sku_num"));
				ProductService service = new ProductService();
				PcProductinfo product = service.getskuinfo(map.get("sku_code"), "");
				//判断是否下架
				String productStatus = service.getProduct(product.getProductCode()).getProductStatus();
				if(productStatus==null||!"4497153900060002".equals(productStatus)){//已下架
					disableAccount = disableAccount+Integer.valueOf(map.get("sku_num"));
					gifq.setFlag_product("0");
				}
				//判断库存是否足够
				int maxStock = (new StoreService()).getStockNumByStore(map.get("sku_code"));
				if(Integer.valueOf(map.get("sku_num"))>maxStock){
					gifq.setFlag_stock("0");
				}
				gifq.setSku_stock(maxStock);
				gifq.setProduct_code(product.getProductCode());
				gifq.setArea_code("");
				gifq.setPic_url(product.getMainPicUrl());
				gifq.setSku_code(map.get("sku_code"));
				gifq.setSku_name(product.getProductSkuInfoList().get(0).getSkuName());
				gifq.setSku_num(Integer.valueOf(map.get("sku_num")));
				//判断此商品是否参加闪购活动
				Map<String, Object> kc = service.getSkuActivity(map.get("sku_code"), sellerCode);
				if((kc.containsKey("flashsalesObj")&&kc.get("flashsalesObj")!=null)){
					FlashsalesSkuInfo flashsalesSkuInfo = (FlashsalesSkuInfo)kc.get("flashsalesObj");
					gifq.setLimit_order_num(flashsalesSkuInfo.getPurchase_limit_order_num().intValue());//闪购商品的限购数量
					gifq.setSku_price(flashsalesSkuInfo.getVipPrice().doubleValue());//闪购商品价格
					gifq.setSales_info(bConfig("familyhas.ActivitySgInfo"));
					gifq.setSales_type(bConfig("familyhas.ActivitySgName"));
				}else {
					gifq.setLimit_order_num(Integer.valueOf(bConfig("familyhas.pt_product_num")));//普通商品的限购数量
					gifq.setSku_price(product.getProductSkuInfoList().get(0).getSellPrice().doubleValue());//普通商品价格
					gifq.setSales_info(bConfig("familyhas.firstActivityRemark"));
					gifq.setSales_type(bConfig("familyhas.firstActivityName"));
				}
				gifq.setSku_property(reProperties(map.get("sku_code"), product.getProductSkuInfoList().get(0).getSkuValue()));
				list.add(gifq);
			}
		}
		result.put("list", list);
		result.put("AccountAll", AccountAll);
		result.put("disableAccount", disableAccount);
		return result;
	}
	
	/**
	 *获取用户购物车中的失效商品
	 * 
	 */
	public Map<String, Object> getDisableSkuShopCart(String sellerCode,String buyerCode){
		Map<String, Object> result = new HashMap<String, Object>();
		List<GoodsInfoForQueryDisable> goodsList = new ArrayList<GoodsInfoForQueryDisable>();
		StringBuffer error = new StringBuffer();
		List<MDataMap> li = DbUp.upTable("oc_shopCart").queryByWhere("buyer_code",buyerCode);
		if(li==null||li.isEmpty()){
			result.put("error", error.toString());
			result.put("list", goodsList);
			return result;
		}
		for (int i = 0; i < li.size(); i++) {
			String skuCode = li.get(i).get("sku_code");
//			String area_code = li.get(i).get("area_code");
			//判断是否已下架商品
			ProductService service = new ProductService();
			//判断此商品是否存在
			PcProductinfo product = service.getskuinfo(skuCode, "");
			if(product!=null&&product.getProductSkuInfoList()!=null&&product.getProductSkuInfoList().get(0)!=null){
				//判断是否下架
				String productStatus = product.getProductStatus();
				//判断库存是否不足
				Map<String, Object> kc = service.getSkuActivity(skuCode, sellerCode);
				if(productStatus==null||!"4497153900060002".equals(productStatus)||
						(kc.containsKey("flashsalesObj")&&kc.get("flashsalesObj")!=null)){//已下架或者是闪购商品
					GoodsInfoForQueryDisable gfd = new GoodsInfoForQueryDisable();
					gfd.setSku_code(skuCode);
					gfd.setSku_name(product.getProductSkuInfoList().get(0).getSkuName());
					gfd.setSku_num(Integer.valueOf(li.get(i).get("sku_num").toString()));
					gfd.setPic_url(product.getMainPicUrl());
//					gfd.setArea_code(area_code);
					gfd.setSku_property(reProperties(skuCode, product.getProductSkuInfoList().get(0).getSkuValue()));
					goodsList.add(gfd);
				}
			}
		}
		result.put("error", error.toString());
		result.put("list", goodsList);
		return result;
	}
	
	/**
	 *删除购物车中的商品 
	 * 
	 */
	public boolean deleteSkuForShopCart(String buyer_code,String skuCode){
		boolean flag = false;
		try {
			if(buyer_code!=null&&skuCode!=null&&!"".equals(skuCode)&&!"".equals(buyer_code)){
				DbUp.upTable("oc_shopCart").delete("buyer_code",buyer_code,"sku_code",skuCode);
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 *普通订单确认订单 
	 * 
	 */
	public Map<String, Object> orderConfirmForPt(String sellerCode,String buyerCode,List<GoodsInfoForAdd> goods) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<GoodsInfoForConfirm> confirms = new ArrayList<GoodsInfoForConfirm>();
		BigDecimal payMoney = new BigDecimal(0.00);//实付款
		BigDecimal cashBack = new BigDecimal(0.00);//返现
		BigDecimal costMoney = new BigDecimal(0.00);//商品总金额
		BigDecimal firstCheap = new BigDecimal(0.00);//首单优惠
		BigDecimal sentMoney = new BigDecimal(0.00);//运费
		BigDecimal subMoney = new BigDecimal(0.00);//满减金额
		BigDecimal phoneMoney = new BigDecimal(0.00);//手机下单减少金额
		String payType = "";//仓储地支持的支付方式
		//库存足够的商品进行结算
		String error = checkGoodsForConfirmForPt(sellerCode,buyerCode, goods);
		result.put("error", error);
		if(goods!=null&&!goods.isEmpty()){
			for (int i = 0; i < goods.size(); i++) {
				String skuCode = goods.get(i).getSku_code();
				int skuNum = goods.get(i).getSku_num();
				ProductService service = new ProductService();
				PcProductinfo productInfo = service.getskuinfo(skuCode, "");
				
				//判断是否支持在线支付或货到付款
//				StoreService storeService = new StoreService();
//				String payT = storeService.getExpress(areaCode);
//				if("10".equals(payT)){
					payType = "449716200002";//货到付款
//				}else if("30".equals(payT)){
//					payType = "449716200001";//在线支付
//				}
				
				
				GoodsInfoForConfirm confirm = new GoodsInfoForConfirm();
				confirm.setSku_code(skuCode);
				confirm.setSku_name(productInfo.getProductSkuInfoList().get(0).getSkuName());
				confirm.setPic_url(productInfo.getMainPicUrl());
				confirm.setSku_num(skuNum);
				confirm.setGiftList(new ArrayList<Gift>());
				confirm.setSku_property(reProperties(skuCode, productInfo.getProductSkuInfoList().get(0).getSkuValue()));
				confirm.setSales_info("");
				confirm.setSales_type("");
				confirm.setSku_price(productInfo.getProductSkuInfoList().get(0).getSellPrice().doubleValue());
				confirm.setProduct_code(productInfo.getProductCode());
				StoreService storeService = new StoreService();
				confirm.setNow_stock(storeService.getStockNumByMax(skuCode));
				payMoney = payMoney.add(productInfo.getProductSkuInfoList().get(0).getSellPrice().multiply(new BigDecimal(skuNum)));//实付款
				costMoney = costMoney.add(productInfo.getProductSkuInfoList().get(0).getSellPrice().multiply(new BigDecimal(skuNum)));//商品总金额
				confirms.add(confirm);
			}
		}
 		String sqlWhere = " delete_flag!='1' and buyer_code ='"+buyerCode+"'";
		int success = DbUp.upTable("oc_orderinfo").dataCount(sqlWhere, new MDataMap());
		if(success==0){
			firstCheap = payMoney.multiply(new BigDecimal(0.12));//首单优惠
			payMoney = payMoney.multiply(new BigDecimal(0.88));//首单88折
			payMoney = payMoney.setScale(2, BigDecimal.ROUND_HALF_UP);//保留两位小数
		}else{
			payMoney = costMoney;
		}
		Double fxbl = getDiscountForUserCode(buyerCode, sellerCode);
		cashBack = payMoney.multiply(new BigDecimal(fxbl));
		result.put("confirms", confirms);
		result.put("payMoney", payMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.put("cashBack", cashBack.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.put("costMoney", costMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.put("firstCheap", firstCheap.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.put("sentMoney", sentMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.put("subMoney", subMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.put("phoneMoney", phoneMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.put("payType", payType);
		return result;
	}
	
	/**
	 *统一确认订单 
	 * 
	 */
	
	public Map<String, Object> orderConfirm(String sellerCode,String buyerCode,List<GoodsInfoForAdd> goods){
		Map<String, Object> result = new HashMap<String, Object>();
		List<GoodsInfoForConfirm> confirms = new ArrayList<GoodsInfoForConfirm>();
		BigDecimal payMoney = new BigDecimal(0.00);//实付款
		BigDecimal cashBack = new BigDecimal(0.00);//返现
		BigDecimal costMoney = new BigDecimal(0.00);//商品总金额
		BigDecimal firstCheap = new BigDecimal(0.00);//首单优惠
		BigDecimal sentMoney = new BigDecimal(0.00);//运费
		BigDecimal subMoney = new BigDecimal(0.00);//满减金额
		BigDecimal phoneMoney = new BigDecimal(0.00);//手机下单减少金额
		String payType = "";//库存地支持的支付方式
		//库存足够的商品进行结算
		String sqlWhere = " delete_flag!='1' and order_status !='4497153900010006' and buyer_code ='"+buyerCode+"'";
		int success = DbUp.upTable("oc_orderinfo").dataCount(sqlWhere, new MDataMap());
		int xgNum = 0;
		if(goods!=null&&!goods.isEmpty()){
			for (int i = 0; i < goods.size(); i++) {
				String skuCode = goods.get(i).getSku_code();
				int skuNum = goods.get(i).getSku_num();
				ProductService service = new ProductService();
				PcProductinfo productInfo = service.getskuinfo(skuCode, "");
				
				//判断是否支持在线支付或货到付款
//				StoreService storeService = new StoreService();
//				String payT = storeService.getExpress(areaCode);
//				if("10".equals(payT)){
					payType = "449716200002";//货到付款
//				}else if("30".equals(payT)){
//					payType = "449716200001";//在线支付
//				}
				GoodsInfoForConfirm confirm = new GoodsInfoForConfirm();
				confirm.setSku_code(skuCode);
				confirm.setSku_name(productInfo.getProductSkuInfoList().get(0).getSkuName());
				confirm.setPic_url(productInfo.getMainPicUrl());
				confirm.setSku_num(skuNum);
				confirm.setProduct_code(productInfo.getProductCode());
				confirm.setGiftList(new ArrayList<Gift>());
				confirm.setSku_property(reProperties(skuCode, productInfo.getProductSkuInfoList().get(0).getSkuValue()));
				confirm.setSku_price(productInfo.getProductSkuInfoList().get(0).getSellPrice().doubleValue());
				StoreService storeService = new StoreService();
				confirm.setNow_stock(storeService.getStockNumByStore(skuCode));
				Map<String, Object> kc = service.getSkuActivity(skuCode, sellerCode);
				if(kc.containsKey("flashsalesObj")&&kc.get("flashsalesObj")!=null){
					xgNum = xgNum+1;
					FlashsalesSkuInfo fssi = (FlashsalesSkuInfo)kc.get("flashsalesObj");
					confirm.setSales_info(bConfig("familyhas.ActivitySgInfo"));
					confirm.setSales_type(bConfig("familyhas.ActivitySgName"));
					confirm.setSales_code(fssi.getActivityCode());
					confirm.setSku_price(fssi.getVipPrice().doubleValue());
					payMoney = payMoney.add(fssi.getVipPrice().multiply(new BigDecimal(skuNum)));//实付款
					costMoney = costMoney.add((new BigDecimal(fssi.getVipPrice().doubleValue())).multiply(new BigDecimal(skuNum)));//商品总金额
				}else {
					if(success==0){
						confirm.setSales_info(bConfig("familyhas.firstActivityRemark"));
						confirm.setSales_type(bConfig("familyhas.firstActivityName"));
						confirm.setSales_code(bConfig("familyhas.firstActivity"));
					}
					payMoney = payMoney.add(productInfo.getProductSkuInfoList().get(0).getSellPrice().multiply(new BigDecimal(skuNum)));
					costMoney = costMoney.add(productInfo.getProductSkuInfoList().get(0).getSellPrice().multiply(new BigDecimal(skuNum)));//商品总金额
				}
				
				confirms.add(confirm);
			}
		}
		if(success==0&&xgNum==0){
			firstCheap = payMoney.multiply(new BigDecimal(0.12));//首单优惠
			payMoney = payMoney.multiply(new BigDecimal(0.88));//首单88折
			payMoney = payMoney.setScale(2, BigDecimal.ROUND_HALF_UP);//保留两位小数
		}
		Double fxbl = getDiscountForUserCode(buyerCode, sellerCode);
		cashBack = payMoney.multiply(new BigDecimal(fxbl));
		result.put("confirms", confirms);
		result.put("payMoney", payMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.put("cashBack", cashBack.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.put("costMoney", costMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.put("firstCheap", firstCheap.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.put("sentMoney", sentMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.put("subMoney", subMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.put("phoneMoney", phoneMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.put("payType", payType);
		return result;
	}
	
	/**
	 *闪购订单确认订单 
	 * 
	 */
	
	public Map<String, Object> orderConfirmForSg(String sellerCode,String buyerCode,List<GoodsInfoForAdd> goods){
		Map<String, Object> result = new HashMap<String, Object>();
		List<GoodsInfoForConfirm> confirms = new ArrayList<GoodsInfoForConfirm>();
		BigDecimal payMoney = new BigDecimal(0.00);//实付款
		BigDecimal cashBack = new BigDecimal(0.00);//返现
		BigDecimal costMoney = new BigDecimal(0.00);//商品总金额
		BigDecimal firstCheap = new BigDecimal(0.00);//首单优惠
		BigDecimal sentMoney = new BigDecimal(0.00);//运费
		BigDecimal subMoney = new BigDecimal(0.00);//满减金额
		BigDecimal phoneMoney = new BigDecimal(0.00);//手机下单减少金额
		String payType = "";//库存地支持的支付方式
		//库存足够的商品进行结算
		String error = checkGoodsForConfirmForSg(sellerCode,buyerCode, goods);
		result.put("error", error);
		if(goods!=null&&!goods.isEmpty()){
			for (int i = 0; i < goods.size(); i++) {
				String skuCode = goods.get(i).getSku_code();
				int skuNum = goods.get(i).getSku_num();
//				String areaCode = goods.get(i).getArea_code();
				ProductService service = new ProductService();
				PcProductinfo productInfo = service.getskuinfo(skuCode, "");
				
				//判断是否支持在线支付或货到付款
//				StoreService storeService = new StoreService();
//				String payT = storeService.getExpress(areaCode);
//				if("10".equals(payT)){
					payType = "449716200002";//货到付款
//				}else if("30".equals(payT)){
//					payType = "449716200001";//在线支付
//				}
				GoodsInfoForConfirm confirm = new GoodsInfoForConfirm();
				confirm.setSku_code(skuCode);
				confirm.setSku_name(productInfo.getProductSkuInfoList().get(0).getSkuName());
				confirm.setPic_url(productInfo.getMainPicUrl());
				confirm.setSku_num(skuNum);
				confirm.setProduct_code(productInfo.getProductCode());
//				confirm.setArea_code(areaCode);
				confirm.setGiftList(new ArrayList<Gift>());
				confirm.setSku_property(reProperties(skuCode, productInfo.getProductSkuInfoList().get(0).getSkuValue()));
				StoreService storeService = new StoreService();
				confirm.setNow_stock(storeService.getStockNumByMax(skuCode));
				Map<String, Object> kc = service.getSkuActivity(skuCode, sellerCode);
				if(kc.containsKey("flashsalesObj")&&kc.get("flashsalesObj")!=null){
					FlashsalesSkuInfo fssi = (FlashsalesSkuInfo)kc.get("flashsalesObj");
					confirm.setSales_info(bConfig("familyhas.ActivitySgInfo"));
					confirm.setSales_type(bConfig("familyhas.ActivitySgName"));
					confirm.setSku_price(fssi.getVipPrice().doubleValue());
					payMoney = payMoney.add(fssi.getVipPrice().multiply(new BigDecimal(skuNum)));//实付款
				}else {
					payMoney = productInfo.getProductSkuInfoList().get(0).getSellPrice().multiply(new BigDecimal(skuNum));
				}
				costMoney = costMoney.add(productInfo.getProductSkuInfoList().get(0).getSellPrice().multiply(new BigDecimal(skuNum)));//商品总金额
				confirms.add(confirm);
			}
		}
		Double fxbl = getDiscountForUserCode(buyerCode, sellerCode);
		cashBack = payMoney.multiply(new BigDecimal(fxbl));
		result.put("confirms", confirms);
		result.put("payMoney", payMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.put("cashBack", cashBack.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.put("costMoney", costMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.put("firstCheap", firstCheap.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.put("sentMoney", sentMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.put("subMoney", subMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.put("phoneMoney", phoneMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.put("payType", payType);
		return result;
	}
	
	/**
	 *判断是否能确认普通订单 
	 */
	public String checkGoodsForConfirmForPt(String sellerCode,String buyerCode,List<GoodsInfoForAdd> goods){
		StringBuffer error = new StringBuffer();
		if(goods!=null&&!goods.isEmpty()){
			for (int i = 0; i < goods.size(); i++) {
				String skuCode = goods.get(i).getSku_code();
				int skuNum = goods.get(i).getSku_num();
//				String areaCode = goods.get(i).getArea_code();
				ProductService service = new ProductService();
				//判断此商品是否存在
				PcProductinfo product = service.getskuinfo(skuCode, "");
				if(product==null){
					deleteSkuForShopCart(buyerCode, skuCode);//删除已不存在的商品
					continue;
				}
				//判断是否下架
				String productStatus = product.getProductStatus();
				if(productStatus==null||!"4497153900060002".equals(productStatus)){//已下架
					error.append(bInfo(916401104, skuCode));
					continue;
				}
				//判断本地区所在库是否存在本商品的库存或者存在库存是否不足  如果是闪购商品提示删除
				StoreService storeService = new StoreService();
				Map<String, Object> kc = service.getSkuActivity(skuCode, sellerCode);
				if(kc.containsKey("flashsalesObj")&&kc.get("flashsalesObj")!=null){
					error.append(bInfo(916401111, skuCode));
				}else if (goods.get(i).getSku_num()>Integer.valueOf(bConfig("familyhas.pt_product_num"))) {//判断普通商品是否超过限购数量
					error.append(bInfo(916401116, goods.get(i).getSku_code(),Integer.valueOf(bConfig("familyhas.pt_product_num"))));
				}else if(storeService.getStockNumByMax(skuCode)<skuNum){
					error.append(bInfo(916401103, skuCode));
				}
			}
		}
		return error.toString();
	}
	
	/**
	 *判断是否能确认闪购订单 
	 */
	public String checkGoodsForConfirmForSg(String sellerCode,String buyerCode,List<GoodsInfoForAdd> goods){
		StringBuffer error = new StringBuffer();
		if(goods!=null&&!goods.isEmpty()){
			for (int i = 0; i < goods.size(); i++) {
				String skuCode = goods.get(i).getSku_code();
				int skuNum = goods.get(i).getSku_num();
//				String areaCode = goods.get(i).getArea_code();
				ProductService service = new ProductService();
				PcProductinfo product = service.getskuinfo(skuCode, "");
				//判断是否下架
				String productStatus = product.getProductStatus();
				if(productStatus==null||!"4497153900060002".equals(productStatus)){//已下架
					error.append(bInfo(916401104, skuCode));
					continue;
				}
				//判断是否超过每会员限购数
				String sqlWhere = "select sum(sku_num) as num from oc_orderdetail where order_code in ( select order_code from oc_orderinfo where order_status in ('4497153900010002','4497153900010003','4497153900010004','4497153900010005') and buyer_code ='"+buyerCode+"') and sku_code='"+skuCode+"'";
				Map<String, Object> alSkuNum = DbUp.upTable("oc_orderinfo").dataSqlOne(sqlWhere, new MDataMap());
				int number = 0;
				if(alSkuNum!=null&&!alSkuNum.isEmpty()&&alSkuNum.containsKey("num")&&!"".equals(alSkuNum.get("num").toString())){
					number = Integer.valueOf(alSkuNum.get("num").toString())+goods.get(i).getSku_num();
				}
				//判断是否闪购商品 库存是否不足
				StoreService storeService = new StoreService();
				Map<String, Object> kc = service.getSkuActivity(skuCode, sellerCode);
				if(!kc.containsKey("flashsalesObj")&&kc.get("flashsalesObj")==null){
					error.append(bInfo(916401112, skuCode));
				}else if (goods.get(i).getSku_num()>((FlashsalesSkuInfo)kc.get("flashsalesObj")).getPurchase_limit_order_num().intValue()) {//判断是否超过每单限购数量
					error.append(bInfo(916401116, goods.get(i).getSku_code(),((FlashsalesSkuInfo)kc.get("flashsalesObj")).getPurchase_limit_order_num().intValue()));
				}else if (number>((FlashsalesSkuInfo)kc.get("flashsalesObj")).getPurchase_limit_order_num().intValue()) {//判断每会员限购数量
					error.append(bInfo(916401117, goods.get(i).getSku_code(),((FlashsalesSkuInfo)kc.get("flashsalesObj")).getPurchase_limit_order_num().intValue()));
				}else if(storeService.getStockNumByMax(skuCode)<skuNum){
					error.append(bInfo(916401103, skuCode));
				}
			}
		}
		return error.toString();
	}
	/**
	 *确认订单判断库存 和是否下架
	 */
	public RootResult checkGoodsStock(String buyerCode,List<GoodsInfoForAdd> goods){
		RootResult rootResult = new RootResult();
		String error = "";
		if(goods!=null&&!goods.isEmpty()){
			for (int i = 0; i < goods.size(); i++) {
				String skuCode = goods.get(i).getSku_code();
				int skuNum = goods.get(i).getSku_num();
				ProductService service = new ProductService();
				//判断此商品是否存在
				PcProductinfo product = service.getskuinfo(skuCode, "");
				if(product==null){
					deleteSkuForShopCart(buyerCode, skuCode);//删除已不存在的商品
					continue;
				}
				//判断是否下架
				String productStatus = product.getProductStatus();
				if(productStatus==null||!"4497153900060002".equals(productStatus)){//已下架
					error = product.getProductSkuInfoList().get(0).getSkuName();
					break;
				}
				//判断本地区所在库是否存在本商品的库存或者存在库存是否不足  如果是闪购商品提示删除
				StoreService storeService = new StoreService();
				if(storeService.getStockNumByMax(skuCode)<skuNum){
					error = product.getProductSkuInfoList().get(0).getSkuName();
					break;
				}
			}
		}
		if(!"".equals(error.toString())){
			rootResult.setResultCode(916401131);
			rootResult.setResultMessage(bInfo(916401131, "["+error+"]"));
		}
		return rootResult;
	}
	/**
	 *确认订单判断限购数量 
	 */
	public RootResult checkGoodsLimit(String sellerCode,String buyerCode,List<GoodsInfoForAdd> goods){
		RootResult rootResult = new RootResult();
		String error = "";
		if(goods!=null&&!goods.isEmpty()){
			for (int i = 0; i < goods.size(); i++) {

				String skuCode = goods.get(i).getSku_code();
				int skuNum = goods.get(i).getSku_num();
				ProductService service = new ProductService();
				//判断是否超过每会员限购数
				String sqlWhere = "select sum(sku_num) as num from oc_orderdetail where order_code in ( select order_code from oc_orderinfo where order_status in ('4497153900010001','4497153900010002','4497153900010003','4497153900010004','4497153900010005') and buyer_code ='"+buyerCode+"') and sku_code='"+skuCode+"'";
				Map<String, Object> alSkuNum = DbUp.upTable("oc_orderinfo").dataSqlOne(sqlWhere, new MDataMap());
				int number = 0;
				if(alSkuNum!=null&&!alSkuNum.isEmpty()&&alSkuNum.containsKey("num")&&alSkuNum.get("num")!=null&&!"".equals(alSkuNum.get("num").toString())){
					number = Integer.valueOf(alSkuNum.get("num").toString())+goods.get(i).getSku_num();
				}else {
					number=goods.get(i).getSku_num();
				}
				//判断是否闪购商品 库存是否不足
				Map<String, Object> kc = service.getSkuActivity(skuCode, sellerCode);
				PcProductinfo product = service.getskuinfo(skuCode, "");
				String skuName = product.getProductSkuInfoList().get(0).getSkuName();
				//判断是否超过每日限购数
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  //设置日期格式
				Calendar t1 = Calendar.getInstance();
				String start = format.format(t1.getTime())+" 00:00:00";
				t1.add(Calendar.DATE, 1);
				String end = format.format(t1.getTime())+" 00:00:00";
				String swhere = "select sum(sku_num) as num from oc_orderdetail where order_code in ( select order_code from oc_orderinfo where order_status in ('4497153900010001','4497153900010002','4497153900010003','4497153900010004','4497153900010005') and buyer_code ='"+buyerCode+"' and create_time>='"+start+"' and create_time < '"+end+"') and sku_code='"+skuCode+"'";
				Map<String, Object> alTodaySkuNum = DbUp.upTable("oc_orderinfo").dataSqlOne(swhere, new MDataMap());
				if(alTodaySkuNum==null||alTodaySkuNum.isEmpty()||alTodaySkuNum.get("num")==null||"".equals(alTodaySkuNum.get("num"))){
					if(kc.containsKey("flashsalesObj")&&kc.get("flashsalesObj")!=null&&skuNum>((FlashsalesSkuInfo)kc.get("flashsalesObj")).getPurchase_limit_day_num().intValue()){
						error = bInfo(916401132,"“" + skuName + "”", ((FlashsalesSkuInfo)kc.get("flashsalesObj")).getPurchase_limit_day_num().intValue());
						break;
					}
				}else if (kc!=null&&kc.containsKey("flashsalesObj")&&kc.get("flashsalesObj")!=null&&(Integer.valueOf(alTodaySkuNum.get("num").toString())+skuNum)>((FlashsalesSkuInfo)kc.get("flashsalesObj")).getPurchase_limit_day_num().intValue()) {
					error = bInfo(916401132,"“" + skuName + "”", ((FlashsalesSkuInfo)kc.get("flashsalesObj")).getPurchase_limit_day_num().intValue());
					break;
				}
				if(!kc.containsKey("flashsalesObj")&&kc.get("flashsalesObj")==null){//普通商品
					if (skuNum>Integer.valueOf(bConfig("familyhas.pt_product_num"))) {//判断普通商品是否超过限购数量
						error = bInfo(916401132,"“" + skuName + "”", bConfig("familyhas.pt_product_num"));
						break;
					}
				}else if (skuNum>((FlashsalesSkuInfo)kc.get("flashsalesObj")).getPurchase_limit_order_num().intValue()) {//判断是否超过每单限购数量
					error = bInfo(916401132,"“" + skuName + "”", ((FlashsalesSkuInfo)kc.get("flashsalesObj")).getPurchase_limit_order_num().intValue());
					break;
				}else if (number>((FlashsalesSkuInfo)kc.get("flashsalesObj")).getPurchaseLimitVipNum().intValue()) {//判断每会员限购数量
					error = bInfo(916401132,"“" + skuName + "”", ((FlashsalesSkuInfo)kc.get("flashsalesObj")).getPurchaseLimitVipNum().intValue()-number+goods.get(i).getSku_num());
					break;
				}
			
			}
		}
		if(!"".equals(error.toString())){
			rootResult.setResultCode(916401132);
			rootResult.setResultMessage(error);
		}
		return rootResult;
	}
	
	/**
	 *解析skuValue 
	 */
	public List<PcPropertyinfoForFamily> reProperties(String skuCode,String skuValue){
		List<PcPropertyinfoForFamily> piffList = new ArrayList<PcPropertyinfoForFamily>();
		if(skuValue==null||"".equals(skuValue)){
			return piffList;
		}
		String [] pro =  skuValue.split("&");
		for (int j = 0; j < pro.length; j++) {
			PcPropertyinfoForFamily piff = new PcPropertyinfoForFamily();
			String [] va = pro[j].split("=");
			piff.setSku_code(skuCode);
			piff.setPropertyKey(va[0]);
			piff.setPropertyValue(va[1]);
			piffList.add(piff);
		}
		return piffList;
	}
	/**
	 *创建订单 
	 */
	public Map<String, Object> createOrder(Map<String, Object> inputMap){
		Map<String, Object> result = new HashMap<String, Object>();
		//判断是否能进行添加订单操作  先进行订单确认校验
		ShopCartService service = new ShopCartService();
		Map<String, Object> checkMe = service.orderConfirm(inputMap.get("seller_code").toString(), inputMap.get("buyer_code").toString(), (List<GoodsInfoForAdd>)inputMap.get("goods"));
		if(((BigDecimal)checkMe.get("payMoney")).doubleValue()!=Double.valueOf(inputMap.get("check_pay_money").toString())){
			result.put("check_pay_money_error", ((BigDecimal)checkMe.get("payMoney")).doubleValue());
			return result;
		}
		//创建订单	
		Order order = new Order();
		order.setOrderCode(WebHelper.upCode(OrderConst.OrderHead));
		//发货地址信息
		OrderAddress address = new OrderAddress();
		address.setAddress(inputMap.get("buyer_address").toString());
		address.setAreaCode(inputMap.get("buyer_address_code").toString());
		address.setFlagInvoice("1");
		address.setOrderCode(order.getOrderCode());
		address.setInvoiceType(((BillInfo)inputMap.get("billInfo")).getBill_Type());
		address.setInvoiceTitle(((BillInfo)inputMap.get("billInfo")).getBill_title());
		address.setInvoiceContent(((BillInfo)inputMap.get("billInfo")).getBill_detail());
		address.setMobilephone(inputMap.get("buyer_mobile").toString());
		address.setReceivePerson(inputMap.get("buyer_name").toString());
		order.setAddress(address);
		List<GoodsInfoForConfirm> confirms = (List<GoodsInfoForConfirm>)checkMe.get("confirms");
		List<OcOrderActivity> actiList = new ArrayList<OcOrderActivity>();
		order.setAppVersion(inputMap.get("app_vision").toString());
		order.setBuyerCode(inputMap.get("buyer_code").toString());
		order.setCreateTime(DateUtil.getSysDateTimeString());
		if("449716200002".equals(order.getPayType())){
			order.setOrderStatus("4497153900010002");
		}else {
			order.setOrderStatus("4497153900010001");
		}
		order.setOrderSource(inputMap.get("order_souce").toString());
		order.setOrderType(inputMap.get("order_type").toString());
		order.setSellerCode(inputMap.get("seller_code").toString());
		order.setPayType(inputMap.get("pay_type").toString());
		order.setOrderMoney((BigDecimal)checkMe.get("costMoney"));
		order.setDueMoney((BigDecimal)checkMe.get("payMoney"));
		order.setPromotionMoney((BigDecimal)checkMe.get("firstCheap"));
		List<OrderDetail> odList = new ArrayList<OrderDetail>();
		for (int i = 0; i < confirms.size(); i++) {
			GoodsInfoForConfirm firm = confirms.get(i);
			OrderDetail od = new OrderDetail();
			od.setSkuCode(firm.getSku_code());
			od.setOrderCode(order.getOrderCode());
			od.setProductPicUrl(firm.getPic_url());
			od.setSkuName(firm.getSku_name());
			od.setSkuNum(firm.getSku_num());
			od.setSkuPrice(new BigDecimal(firm.getSku_price()));
			od.setStoreCode(firm.getArea_code());
			od.setGiftFlag("1");
			odList.add(od);
			
			if(Double.valueOf(checkMe.get("firstCheap").toString())>0){//如果首单优惠大于0则为首单优惠活动
				//活动相关的
				OcOrderActivity acti = new OcOrderActivity();
				acti.setActivityCode(bConfig("familyhas.firstActivity"));//首单88折活动编号
				acti.setActivityName(bConfig("familyhas.firstActivityName"));
				acti.setPreferentialMoney(firm.getSku_price().floatValue());//优惠金额
				acti.setSkuCode(firm.getSku_code());
				acti.setOrderCode(order.getOrderCode());
				actiList.add(acti);
			}
			
			if(firm.getSales_code()!=null&&!"".equals(firm.getSales_code())&&firm.getSales_code().startsWith("SG")){
				OcOrderActivity sgOcOrderActivity = new OcOrderActivity();
				sgOcOrderActivity.setSkuCode(firm.getSku_code());
				sgOcOrderActivity.setActivityCode(firm.getSales_code());
				sgOcOrderActivity.setActivityName(firm.getSales_info());
				sgOcOrderActivity.setActivityType("449715400004");
				actiList.add(sgOcOrderActivity);
			}
			
			//获取赠品数据
			/*StoreService storeService = new StoreService();
			List<String> stores = storeService.getStores(firm.getArea_code());*/
			GetGoodGiftList ggft = new GetGoodGiftList();
			List<ModelGoodGiftInfo> gifts = ggft.doRsync(firm.getProduct_code(), address.getAreaCode());
			if(gifts!=null&&!gifts.isEmpty()){
				for(int j=0;j<gifts.size();j++){
					ModelGoodGiftInfo mlgi = gifts.get(j);
					OcOrderActivity giftActi = new OcOrderActivity();
					giftActi.setActivityCode(mlgi.getEvent_id());
					giftActi.setOrderCode(order.getOrderCode());
					giftActi.setSkuCode(firm.getSku_code());
					actiList.add(giftActi);
					
					OrderDetail odG = new OrderDetail();
					odG.setSkuCode(mlgi.getGood_id());
					odG.setOrderCode(order.getOrderCode());
					odG.setSkuName(mlgi.getGood_nm());
					odG.setSkuNum(1);
					odG.setSkuPrice(new BigDecimal(0));
//					odG.setStoreCode(stores.get(0));
					odG.setGiftFlag("0");
//					odG.setStoreCode(stores.get(0).toString());
					odList.add(odG);
				}
			}
		}
		order.setProductList(odList);
		order.setActivityList(actiList);
		
		OrderService ser = new OrderService();
		List<Order> orderLists= new ArrayList<Order>();
		orderLists.add(order);
		StringBuffer error = new StringBuffer();
		if(AppConst.MANAGE_CODE_CAPP.equals(inputMap.get("seller_code").toString())){
			ser.AddOrderListTx(orderLists, error, AppConst.CAPP_STORE_CODE);
		}else {
			ser.AddOrderListTx(orderLists, error, inputMap.get("buyer_address_code").toString());
		}
		if(error!=null&&!"".equals(order.getOrderCode())){
			result.put("error", error);
		}
		result.put("order_code", order.getOrderCode());
		return result;
	}
	public void updateAccountFlag(String buyerCode,String skuCode){
		try {
			if(buyerCode!=null&&skuCode!=null&&!"".equals(skuCode)&&!"".equals(buyerCode)){
				MDataMap map = new MDataMap();
				map.put("buyer_code", buyerCode);
				map.put("sku_code", skuCode);
				map.put("account_flag", "1");
				DbUp.upTable("oc_shopCart").dataUpdate(map, "account_flag", "buyer_code,sku_code");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 根据用户编号，获取折扣
	 * @param userCode		登录用户编号
	 * @param sellerCode	app编号
	 * @return
	 */
	public double getDiscountForUserCode(String userCode,String sellerCode){
		if (null == userCode || null == sellerCode || "".equals(userCode) || "".equals(sellerCode)) {
			return 0.05;
		}
		try {
			//根据member_code获取account_code
			MDataMap accountCodeMap = DbUp.upTable("mc_member_info").oneWhere("account_code","","","member_code",userCode,"manage_code",sellerCode);
			
			//根据account_code获取清分比例
			MDataMap scaleReckonMap = DbUp.upTable("gc_group_account").oneWhere("scale_reckon","","","account_code",accountCodeMap.get("account_code"));
			
			double scaleReckon = Double.parseDouble(scaleReckonMap.get("scale_reckon"));
			
			//清分比例不足5%的设置为5%
			return (scaleReckon > 0.05 ? scaleReckon : 0.05);					//返现金额
			
		} catch (Exception e) {
			//清分比例不足5%的设置为5%
			return 0.05;
		}
	}
	/**
	 * 解析skuValue 获取sku编号
	 * 
	 */
	public String getSkuCodeForValue(String productCode,String skuKeyValue){
		if(skuKeyValue!=null&&!"".equals(skuKeyValue)&&!skuKeyValue.contains("&")&&!skuKeyValue.contains("=")){
			return skuKeyValue;
		}
		List<String> skuCode = new ArrayList<String>();
		ProductService service = new ProductService();
		PcProductinfo pc = service.getProduct(productCode);
		if(skuKeyValue!=null&&!"".equals(skuKeyValue)&&productCode!=null&&!"".equals(productCode)){
			String skuKey [] = null;
			if(skuKeyValue.contains("&")){
				skuKey = skuKeyValue.split("&");
			}else {
				skuKey = new String[1];
				skuKey[0] = skuKeyValue;
			}
			if(pc.getProductSkuInfoList()!=null&&!pc.getProductSkuInfoList().isEmpty()){
				for (int i = 0; i < pc.getProductSkuInfoList().size(); i++) {
					String ss = pc.getProductSkuInfoList().get(i).getSkuKey();
					if(ss!=null&&!"".equals(ss)){
						String ssMap [] = ss.split("&");
						MDataMap mm = new MDataMap();
						for (int d = 0; d < ssMap.length; d++) {
							mm.put(ssMap[d], "");
						}
						int cc = 0;
						for (int j = 0; j < skuKey.length; j++) {
							if(mm.containsKey(skuKey[j])){
								cc=cc+1;
							}
						}
						if(cc==skuKey.length){
							skuCode.add(pc.getProductSkuInfoList().get(i).getSkuCode());
						}
					}
				}
			}
		}
		if(skuCode.size()==1){
			return skuCode.get(0);
		}else {
			return "";
		}
	}
	
}
