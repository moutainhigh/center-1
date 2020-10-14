package com.cmall.groupcenter.baidupush.core.utility;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.active.ActiveConst;
import com.cmall.groupcenter.active.ActiveControllerForProduct;
import com.cmall.groupcenter.active.BaseActive;
import com.cmall.groupcenter.active.product.SkuInfoForRet;
import com.cmall.groupcenter.homehas.GetGoodGiftList;
import com.cmall.groupcenter.homehas.model.ModelGoodGiftInfo;
import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.common.OrderConst;
import com.cmall.ordercenter.familyhas.active.ActiveForproduct;
import com.cmall.ordercenter.familyhas.active.ActiveReq;
import com.cmall.ordercenter.familyhas.active.ActiveReturn;
import com.cmall.ordercenter.model.CouponInfoRootResult;
import com.cmall.ordercenter.model.GoodsInfoForAdd;
import com.cmall.ordercenter.model.OcOrderActivity;
import com.cmall.ordercenter.model.OcOrderPay;
import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.OrderAddress;
import com.cmall.ordercenter.model.OrderDetail;
import com.cmall.ordercenter.model.PtInfo;
import com.cmall.ordercenter.service.CouponsService;
import com.cmall.ordercenter.service.GetOrderFreightService;
import com.cmall.ordercenter.util.CouponUtil;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.service.CategoryService;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.xmasorder.service.TeslaCouponService;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.helper.PlusHelperEvent;
import com.srnpr.xmassystem.load.LoadGiftSkuInfo;
import com.srnpr.xmassystem.load.LoadProductInfo;
import com.srnpr.xmassystem.modelevent.PlusModelEventItemProduct;
import com.srnpr.xmassystem.modelproduct.PlusModelGiftSkuinfo;
import com.srnpr.xmassystem.modelproduct.PlusModelGitfSkuInfoList;
import com.srnpr.xmassystem.modelproduct.PlusModelMediMclassGift;
import com.srnpr.xmassystem.modelproduct.PlusModelProductInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelProductQuery;
import com.srnpr.xmassystem.modelproduct.PlusModelPropertyInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuQuery;
import com.srnpr.xmassystem.support.PlusSupportEvent;
import com.srnpr.xmassystem.support.PlusSupportProduct;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 家有汇和惠家有拆单util
 * 
 * @author xiegj
 * 
 * 
 */
public class DismantlOrderUtil extends BaseClass {

	private final static char[] seq = new char[]{'0','1','2','3','4','5','6','7','8','9',
		'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
		'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	public static String notPayStatus = "4497153900010001";//下单成功未付款（在线支付）
	public static String PayStatus = "4497153900010002";//下单成功未发货（货到付款）
	public static String OLP = "449716200001";//在线支付
	public static String COD = "449716200002";//货到付款
	// 虚拟商品：供应商 10或20 一地入库：入库地 00四地入库
	// 虚拟商品无运费，正常订单都有运费
	// 活动金额按照订单金额，由高到低扣减
	// 多单或者含有虚拟商品订单必须在线支付，一个普通商品订单时可进行货到付款
	/**
	 * 根据虚拟商品标识分别拆单（普通商品订单和虚拟商品订单）
	 * @param map<sku编号,sku数量> 
	 * @param other 创建订单所需参数(buyerCode,orderType,orderSouce,payType,sellerCode)
	 */
	public List<Order> DismantlOrder(Map<String, Integer> map,OrderAddress address,MDataMap other,RootResultWeb resultWeb, List<String> couponCodes) {
		List<Order> result = new ArrayList<Order>();
		try {
			int unCodNum = 0;//不支持货到付款的商品种类
			MDataMap cxMap = new MDataMap();//对接促销系统
			Iterator<String> icIte = map.keySet().iterator();
			String sellerCode = other.get("sellerCode");
			while (icIte.hasNext()) {//促销系统IC开头的skucode与系统skucode进行对接
				String iteamCode = (String) icIte.next();
				if(PlusHelperEvent.checkEventItem(iteamCode)){
					String yskuCode = new PlusSupportProduct().upSkuInfoBySkuCode(iteamCode,other.get("buyerCode").toString()).getSkuCode();
					cxMap.put(yskuCode,iteamCode);
					map.put(yskuCode,map.get(iteamCode));
					map.remove(iteamCode);
				}
			}
			String sql = "SELECT c.sku_code as skuCode,a.product_code as productCode,a.small_seller_code as smallSellerCode,a.mainpic_url as mainpicUrl,a.validate_flag as flag," +
					" b.prch_type as type,b.dlr_id as dlrId,b.oa_site_no as siteNo from pc_productinfo a," +
					"pc_productinfo_ext b,pc_skuinfo c where a.product_code=b.product_code and " +
					"c.product_code=a.product_code and c.sku_code in ('"+StringUtils.join(map.keySet().toArray(),"','")+"')";
			List<Map<String, Object>> all = DbUp.upTable("pc_productinfo").dataSqlList(sql, new MDataMap());//拆单所需
			//获取所有商品的活动信息(活动价及活动信息)
			List<SkuInfoForRet> skuActivityList = new ArrayList<SkuInfoForRet>();
			Map<String, ActiveReturn> skuActivityListHas = new HashMap<String, ActiveReturn>();
			if(MemberConst.MANAGE_CODE_HPOOL.equals(sellerCode)){
				skuActivityList = new ActiveControllerForProduct(sellerCode).activeGallery( map,new MDataMap(ActiveConst.ACTIVE_PARAMS_BUYERCODE,other.get("buyerCode"),ActiveConst.ACTIVE_PARAMS_BUYERADDRESS,other.get("vip_address_id")), resultWeb);
			}else if (MemberConst.MANAGE_CODE_HOMEHAS.equals(sellerCode)||MemberConst.MANAGE_CODE_SPDOG.equals(sellerCode)) {
				List<ActiveReq> reqList = new ArrayList<ActiveReq>();
				for (Map<String, Object> spro : all) {
					ActiveReq activeReq = new ActiveReq();
					activeReq.setBuyer_code(other.get("buyerCode").toString());
					activeReq.setProduct_code(spro.get("productCode").toString());
					if(cxMap.containsKey(spro.get("skuCode").toString())){//对接促销系统
						activeReq.setSku_code(cxMap.get(spro.get("skuCode").toString()));
					}else{
						activeReq.setSku_code(spro.get("skuCode").toString());
					}
					activeReq.setSku_num(map.get(spro.get("skuCode").toString()));
					reqList.add(activeReq);
				}
				skuActivityListHas = new ActiveForproduct().activeGallery(reqList, resultWeb);
			}
			Map<String, List<Map<String, Object>>> oneMap = new HashMap<String, List<Map<String,Object>>>();//一地入库商品
			List<Map<String, Object>> fourList = new ArrayList<Map<String,Object>>();//四地入库商品
			Map<String, List<Map<String, Object>>> xnMap = new HashMap<String, List<Map<String,Object>>>();//虚拟商品
			Double tranPri = 0.00;//运费
			other.put("tranPri", tranPri.toString());
			if(resultWeb.upFlagTrue()){
				for (int i = 0; i < all.size(); i++) {
					Map<String, Object> mm = all.get(i);
					String skuCode = mm.get("skuCode").toString();
					mm.put("skuNum", map.get(skuCode));
					if(MemberConst.MANAGE_CODE_HPOOL.equals(sellerCode)){
						for (int j = 0; j < skuActivityList.size(); j++) {
							SkuInfoForRet sf = skuActivityList.get(j);
							if(sf.getSkuCode().equals(skuCode)){
								mm.put("activity", sf);//活动信息存入商品Map中
							}
						}
					}else if(MemberConst.MANAGE_CODE_HOMEHAS.equals(sellerCode)||MemberConst.MANAGE_CODE_SPDOG.equals(sellerCode)){
						Iterator<String> sahIte = skuActivityListHas.keySet().iterator();
						while (sahIte.hasNext()) {
							String key = (String) sahIte.next();
							if(key.split("_")[0].equals(skuCode)){
								mm.put("activity", skuActivityListHas.get(key));//活动信息存入商品Map中
							}
						}
					}
					if("Y".equals(mm.get("flag").toString())){//虚拟商品根据   供应商拆单
						String dlrId = mm.get("dlrId").toString();
						if(map.containsKey(dlrId)){
							xnMap.get(dlrId).add(mm);
						}else {
							if(xnMap.containsKey(dlrId)){
								xnMap.get(dlrId).add(mm);
							}else {
								List<Map<String, Object>> newList  = new ArrayList<Map<String,Object>>();
								newList.add(mm);
								xnMap.put(dlrId, newList);
							}
						}
						
					}else {//普通商品根据    一地入库和四地入库拆单
						if("00".equals(mm.get("type").toString())){//四地入库
							fourList.add(mm);
						}else {//一地入库
							String siteNo = mm.get("siteNo").toString();
							if(oneMap.containsKey(siteNo)){//一地入库根据入库地进行拆单
								oneMap.get(siteNo).add(mm);
							}else {
								List<Map<String, Object>> newList  = new ArrayList<Map<String,Object>>();
								newList.add(mm);
								oneMap.put(siteNo, newList);
							}
						}
					}
				}
				int ss = 0;
				String orderSeq = String.valueOf(seq[ss]);
				Iterator<String> xnIte = xnMap.keySet().iterator();//虚拟商品订单
				while (xnIte.hasNext()) {
					orderSeq = String.valueOf(seq[ss]);
					String xn = (String) xnIte.next();
					result.addAll(virtualOrder(orderSeq,xnMap.get(xn), address, other));
					ss++;
				}
				Iterator<String> oneIte = oneMap.keySet().iterator();
				while (oneIte.hasNext()) {//一地入库 订单
					orderSeq = String.valueOf(seq[ss]);
					String sn = (String) oneIte.next();
					result.addAll(virtualOrder(orderSeq,oneMap.get(sn), address, other));
					ss++;
				}
				if(!fourList.isEmpty()){
					orderSeq = String.valueOf(seq[ss]);
					result.addAll(virtualOrder(orderSeq,fourList, address, other));//四地入库订单
					ss++;
				}
				if(result!=null&&!result.isEmpty()){//只有一个普通订单且订单内不包含闪购活动且不是虚拟商品订单    货到付款
					int viFlag = 0;//虚拟商品标识
					if(!result.isEmpty()){
						for (int g = 0; g < result.size(); g++) {
							Order order = result.get(g);
							for (int i = 0; i < order.getProductList().size(); i++) {
								OrderDetail detail = order.getProductList().get(i);
								if("1".equals(detail.getGiftFlag())){
									if("Y".equals(detail.getValidateFlag())){
										viFlag=viFlag+1;
									}
								}
							}
							for (int i = 0; i < order.getActivityList().size(); i++) {
								OcOrderActivity activity = order.getActivityList().get(i);
								if(StringUtils.isNotBlank(activity.getActivityCode())&&!StringUtils.startsWith(activity.getOutActiveCode(), "IC_SMG_")
										&&(activity.getActivityCode().startsWith("SG")
										||activity.getActivityCode().startsWith("NG")
										||PlusHelperEvent.checkEventItem(activity.getOutActiveCode()))){
										unCodNum=unCodNum+1;
								}
							}
						}
					}
					if(VersionHelper.checkServerVersion("9.5.41.31")&&sellerCode.equals(MemberConst.MANAGE_CODE_HPOOL)){
						result = recheckMoneyJyh(result,other);//家有汇储值金、暂存款、积分使用
					}
					if(sellerCode.equals(MemberConst.MANAGE_CODE_HOMEHAS)||sellerCode.equals(MemberConst.MANAGE_CODE_SPDOG)){
//						if(StringUtility.isNotNull(other.get("coupon_codes"))){
//							result = useCouponsForProduct(cxMap,result,other,resultWeb);//优惠券均等拆分到可使用优惠券的商品上
//						}
						//优惠券均等拆分到可使用优惠券的商品上
						if(couponCodes != null && couponCodes.size() > 0) {
							useCouponsForProduct(cxMap,result,other,resultWeb,couponCodes);//优惠券均等拆分到可使用优惠券的商品上
						}
					}
					if(sellerCode.equals(MemberConst.MANAGE_CODE_HOMEHAS)&&other.containsKey("wgsUseMoney")&&Double.valueOf(other.get("wgsUseMoney"))>0){
						useWgsForOrder(result, other, resultWeb);
					}
					if(sellerCode.equals(MemberConst.MANAGE_CODE_SPDOG)){
						result = freightFreeForDog(result);
					}
					if(other.containsKey("payType")&&other.get("payType")!=null
							&&("449716200001".equals(other.get("payType"))
									||"449746280003".equals(other.get("payType"))
									||"449746280005".equals(other.get("payType")))){
						for (int i = 0; i < result.size(); i++) {
							result.get(i).setPayType(OLP);//在线支付
							if(result.get(i).getDueMoney().compareTo(new BigDecimal(0))==0){//订单应付款为0时，自动设置订单状态为下单成功未发货
								result.get(i).setOrderStatus(PayStatus);//下单成功未发货
							}else {
								result.get(i).setOrderStatus(notPayStatus);//下单成功未付款
							}
						}
					}else {
						if(unCodNum==0&&viFlag==0){//无虚拟商品时可货到付款
							for (int i = 0; i < result.size(); i++) {
								if(result.get(i).getDueMoney().compareTo(new BigDecimal(0))==0){
									result.get(i).setPayType(OLP);//在线支付
								}else {
									result.get(i).setPayType(COD);//货到付款
								}
								result.get(i).setOrderStatus(PayStatus);//下单成功未发货
							}
						}else {
							for (int i = 0; i < result.size(); i++) {
								if(result.get(i).getDueMoney().compareTo(new BigDecimal(0))==0){//订单应付款为0时，自动设置订单状态为下单成功未发货
									if(sellerCode.equals(MemberConst.MANAGE_CODE_HOMEHAS)
											&&((other.containsKey("wgsUseMoney")&&Double.valueOf(other.get("wgsUseMoney"))>0)
											||(couponCodes != null && couponCodes.size() > 0))){
										result.get(i).setPayType(OLP);//在线支付
										result.get(i).setOrderStatus(PayStatus);//下单成功未发货
									}else{
										result.get(i).setPayType(COD);//货到付款
										result.get(i).setOrderStatus(PayStatus);//下单成功未发货
									}
								}else {
									result.get(i).setPayType(OLP);//在线支付
									result.get(i).setOrderStatus(notPayStatus);//下单成功未付款
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 创建订单
	 * @param skuList 商品List
	 * @param address 地址对象（地址及发票信息）
	 * @param other 创建订单所需参数(buyerCode,orderType,orderSouce,payType,sellerCode,tranPri,mainpicUrl)
	 */
	public List<Order> virtualOrder(String orderSeq,List<Map<String, Object>> skuMap,OrderAddress address,Map<String, String> other) {
		List<Order> result = new ArrayList<Order>();
		//开始创建订单
		Order order = new Order();
		order.setOrderCode(WebHelper.upCode(OrderConst.OrderHead));
		address.setOrderCode(order.getOrderCode());
		order.setAddress(address);
		order.setOrderSeq(orderSeq);
		order.setBuyerCode(other.get("buyerCode"));//买家编号
		order.setCreateTime(DateUtil.getSysDateTimeString());//订单创建时间
		order.setOrderSource(other.get("orderSouce"));
		order.setOrderType(other.get("orderType"));
		order.setSellerCode(other.get("sellerCode"));
		List<OrderDetail> odList = new ArrayList<OrderDetail>();
		List<OcOrderActivity> actiList = new ArrayList<OcOrderActivity>();
		Iterator<Map<String, Object>> skusIterator = skuMap.iterator();
		while (skusIterator.hasNext()) {
			Map<String, Object> skuInfo= skusIterator.next();//商品信息 skuCode productCode flag type dlrId siteNo smallSellerCode
			OrderDetail od = new OrderDetail();
			ProductService productService = new ProductService();
			PcProductinfo productInfo = productService.getskuinfo(skuInfo.get("skuCode").toString(), "");
			
			/*
			 * 如何是拼好货订单，则取拼好货活动价   fq++
			 
			if(!StringUtils.isEmpty(other.get("orderType")) && "449715200013".equals(other.get("orderType"))) {
				productInfo.getProductSkuInfoList().get(0).setSellPrice(new PlusSupportProduct().getPrice(skuInfo.get("skuCode").toString()));
			}
			*/
			od.setCostPrice(productInfo.getCostPrice());
			if(MemberConst.MANAGE_CODE_HPOOL.equals(other.get("sellerCode"))){//家有汇*********
				order.setSmallSellerCode(MemberConst.MANAGE_CODE_HPOOL);
				SkuInfoForRet skuActivity = (SkuInfoForRet)skuInfo.get("activity");
				od.setSkuKey(skuActivity.getSkuKey());
				od.setSkuValue(skuActivity.getSkuValue());
				od.setSkuCode(skuActivity.getSkuCode());
				od.setProductCode(skuActivity.getProductCode());
				
				/*
				 * 如何是拼好货订单，则取拼好货活动价   fq++
				
				if(!StringUtils.isEmpty(other.get("orderType")) && "449715200013".equals(other.get("orderType"))) {
					skuActivity.setTransactionPrice(new PlusSupportProduct().getPrice(skuInfo.get("skuCode").toString()));
				}
				 */
				od.setSkuPrice(skuActivity.getTransactionPrice());
				od.setSkuName(skuActivity.getSkuName());
				od.setSaveAmt(skuActivity.getSellPrice().subtract(skuActivity.getTransactionPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
				//商品参与的活动
				if(new CategoryService().productInCategory("SI2009", od.getProductCode(), TopUp.upConfig("homepool.jyh_king"))){
					order.setOrderType("449715200009");//尊享订单
				}
				if(skuActivity.getActiveList()!=null&&!skuActivity.getActiveList().isEmpty())
					for (int x = 0; x < skuActivity.getActiveList().size(); x++) {
						BaseActive ba = skuActivity.getActiveList().get(x);
						if("AT141224100001".equals(ba.getActivity_type_code())){
							order.setOrderType("449715200008");//折扣订单
						}
						if("AT140820100002".equals(ba.getActivity_type_code())){
							order.setOrderType("449715200004");//闪购订单
						}
						if("AT140820100003".equals(ba.getActivity_type_code())){
							order.setOrderType("449715200006");//会员订单
						}
						if("AT140820100004".equals(ba.getActivity_type_code())){
							order.setOrderType("449715200007");//内购订单
						}
						OcOrderActivity ga = new OcOrderActivity();
						ga.setActivityCode(ba.getActivity_code());
						ga.setActivityName(ba.getActivity_title());
						ga.setActivityType(ba.getActivity_type_code());
						ga.setOrderCode(order.getOrderCode());
						ga.setPreferentialMoney((skuActivity.getSellPrice().subtract(ba.getActivePrice())).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
						ga.setProductCode(skuActivity.getProductCode());
						ga.setSkuCode(skuActivity.getSkuCode());
						ga.setOutActiveCode(ba.getOuter_activity_code());//家有汇活动编号在新设计的活动上
						actiList.add(ga);
					}
					order.setPromotionMoney(order.getPromotionMoney().add((skuActivity.getSellPrice().subtract(skuActivity.getTransactionPrice())).setScale(2, BigDecimal.ROUND_HALF_UP)));
			}else if (MemberConst.MANAGE_CODE_HOMEHAS.equals(other.get("sellerCode"))||MemberConst.MANAGE_CODE_SPDOG.equals(other.get("sellerCode"))) {//惠家有**********
				order.setSmallSellerCode(skuInfo.get("smallSellerCode").toString());
				od.setSkuKey(productInfo.getProductSkuInfoList().get(0).getSkuKey());
				od.setSkuValue(productInfo.getProductSkuInfoList().get(0).getSkuValue());
				od.setSkuCode(skuInfo.get("skuCode").toString());
				od.setProductCode(productInfo.getProductCode());
				od.setSkuPrice(productInfo.getProductSkuInfoList().get(0).getSellPrice().setScale(2, BigDecimal.ROUND_DOWN));
				od.setSkuName(productInfo.getProductSkuInfoList().get(0).getSkuName());
				ActiveReturn acre = (ActiveReturn)skuInfo.get("activity");
				if(acre.isUse_activity()){
					OcOrderActivity ga = new OcOrderActivity();
					ga.setActivityCode(acre.getActivity_code());
					ga.setSkuCode(od.getSkuCode());
					if("449715400004".equals(acre.getActivity_type())){//闪购
						order.setOrderType("449715200004");//闪购订单
						ga.setActivityType("449715400004");//惠家有的活动编号在原设计的闪购活动上
						ga.setOutActiveCode(DbUp.upTable("oc_activity_flashsales").one("activity_code",acre.getActivity_code()).get("outer_activity_code"));
						ga.setActivityName(bConfig("familyhas.ActivitySgInfo"));
					}else if ("AT140820100004".equals(acre.getActivity_type())) {//内购
						order.setOrderType("449715200007");//内购订单
						ga.setActivityType("AT140820100004");
						ga.setActivityName(bConfig("familyhas.ActivityNgInfo"));
					}else if ("AT140820108888".equals(acre.getActivity_type())) {//对接促销系统
						ga.setActivityCode(acre.getActivity_code().split("&")[0]);
						ga.setOutActiveCode(acre.getActivity_code().split("&")[1]);
						ga.setSkuCode(acre.getSku_code());
					}
					order.setPayType(this.OLP);
					ga.setOrderCode(order.getOrderCode());
					ga.setPreferentialMoney((od.getSkuPrice().subtract(acre.getActivity_price())).floatValue());
					ga.setProductCode(od.getProductCode());
					od.setSaveAmt(new BigDecimal(ga.getPreferentialMoney()));
					od.setSkuPrice(acre.getActivity_price());
					actiList.add(ga);
				}
			}
			od.setOrderCode(order.getOrderCode());
			od.setProductPicUrl(skuInfo.get("mainpicUrl")==null ? "":skuInfo.get("mainpicUrl").toString());
			od.setSkuNum((Integer)skuInfo.get("skuNum"));
			od.setStoreCode(address.getAreaCode());
			od.setGiftFlag("1");
			od.setValidateFlag(skuInfo.get("flag").toString());//是否虚拟商品
			od.setDlrId(skuInfo.get("dlrId").toString());//供应商编号
			od.setOaSiteNo(skuInfo.get("siteNo").toString());//入库仓库编号
			od.setPrchType(skuInfo.get("type").toString());//入库类型
			order.setOrderMoney(order.getOrderMoney().add(od.getSkuPrice().multiply(new BigDecimal((Integer)skuInfo.get("skuNum")))));//订单金额
			odList.add(od);
			// 获取赠品数据
			List<ModelGoodGiftInfo> gifts = new ArrayList<ModelGoodGiftInfo>();
			if(other.get("sellerCode").equals(MemberConst.MANAGE_CODE_HOMEHAS)){
				gifts = this.getProductGiftsDetailList(od.getProductCode());
			}else {
				gifts = this.getProductGiftsDetailList(productInfo.getProductCodeOld());
			}
			if (gifts != null && !gifts.isEmpty()) {
				for (int j = 0; j < gifts.size(); j++) {
					ModelGoodGiftInfo mlgi = gifts.get(j);

					OrderDetail odG = new OrderDetail();
					odG.setSkuCode(mlgi.getGood_id());
					odG.setProductCode(od.getSkuCode());
					odG.setOrderCode(order.getOrderCode());
					odG.setSkuName(mlgi.getGood_nm());
					odG.setSkuNum(od.getSkuNum());
					odG.setSkuPrice(new BigDecimal(0));
					odG.setGiftFlag("0");
					odG.setStoreCode(od.getStoreCode());
					if(other.get("sellerCode").equals(MemberConst.MANAGE_CODE_HOMEHAS)||
							other.get("sellerCode").equals(MemberConst.MANAGE_CODE_HPOOL)||
							other.get("sellerCode").equals(MemberConst.MANAGE_CODE_SPDOG)){//后台逻辑代码版本控制
						OcOrderActivity giftActi = new OcOrderActivity();
						giftActi.setActivityCode(mlgi.getEvent_id());
						giftActi.setOutActiveCode(mlgi.getEvent_id());
						giftActi.setOrderCode(order.getOrderCode());
						giftActi.setSkuCode(mlgi.getGood_id());
						actiList.add(giftActi);
						odG.setGift_cd(mlgi.getGift_cd());
					}
					odList.add(odG);
				}
			}
		}
		order.setProductList(odList);
		order.setActivityList(actiList);
		order.setOrderMoney(order.getOrderMoney().setScale(2, BigDecimal.ROUND_HALF_UP));
		
		if(other.get("sellerCode").equals(MemberConst.MANAGE_CODE_HOMEHAS)||
				other.get("sellerCode").equals(MemberConst.MANAGE_CODE_HPOOL)||
				other.get("sellerCode").equals(MemberConst.MANAGE_CODE_SPDOG)){//惠家有、家有汇运费由快递人员进行收取
			order.setTransportMoney(this.getTranMoney(skuMap, address));
			
			/*
			 * 判断是否是一元购订单，如果是一元购订单，则取一元购活动价  fq++
			 */
			if("449715200014".equals(order.getOrderType())) {
				
				String yygPayMoney = other.get("yygPayMoney");
				MDataMap one = DbUp.upTable("lc_sync_yyg_orderInfo").one("order_no",other.get("yygOrderNo"));
				if(null != one) {
					String period_num = one.get("period_num");
					String product_id = one.get("product_id");
					
					MDataMap paramMap = new MDataMap();
					paramMap.put("period_num", period_num);
					paramMap.put("product_id", product_id);
					
					String sSql = " SELECT SUM(pay_money) as money FROM logcenter.lc_sync_yyg_orderInfo WHERE period_num = :period_num AND product_id = :product_id  GROUP BY period_key " ;
					Map<String, Object> dataSqlOne = DbUp.upTable("lc_sync_yyg_orderInfo").dataSqlOne(sSql, paramMap);
					if(null != dataSqlOne && dataSqlOne.size() > 0) {
						BigDecimal payMoney = BigDecimal.valueOf(Double.valueOf(String.valueOf(dataSqlOne.get("money"))));
						if(payMoney.compareTo(BigDecimal.valueOf(Double.valueOf(yygPayMoney))) == 0 ){//一元购支付的金额等于同步记录的总金额，则成功
							order.getOrderMoney();
							order.setOrderMoney(new BigDecimal(0.00));
							order.setDueMoney(new BigDecimal(0.00));
						} else {
							return result;
						}
					} else {
						return result;
					}
				} else {
					return result;
				}
				
			} else {
				order.setDueMoney(order.getOrderMoney().setScale(2, BigDecimal.ROUND_HALF_UP));//应付金额 = 订单金额
			}
		}else {
			order.setTransportMoney(new BigDecimal(other.get("tranPri")));
			order.setDueMoney((order.getOrderMoney().add(new BigDecimal(other.get("tranPri")))).setScale(2, BigDecimal.ROUND_HALF_UP));//应付金额 = 订单金额+运费-优惠金额
		}
		result.add(order);
		return result;
	}

	/**
	 *获取订单运费（规则为：多个商品时取运费最高的为订单运费） 
	 * 
	 */
	public BigDecimal getTranMoney(List<Map<String, Object>> list,OrderAddress address) {
		BigDecimal tran = new BigDecimal(0.00);
		if(address.getAreaCode()==null||"".equals(address.getAreaCode())){
			return tran;
		}
		if(list==null||list.isEmpty()||list.size()==0){
			return tran;
		}
		//未完待续(别忘记四舍五入)
		List<PtInfo> ptInfos = new ArrayList<PtInfo>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> skuinfo = list.get(i);
			PtInfo info = new PtInfo();
			info.setAccount(Integer.valueOf(skuinfo.get("skuNum").toString()));
			info.setProductCode(skuinfo.get("productCode").toString());
			ptInfos.add(info);
		}
		Map<String,BigDecimal> result = (new GetOrderFreightService()).getProductFreight(ptInfos,address.getAreaCode());
		Map<String, Double> rr = new HashMap<String, Double>();
		Iterator<String> it = result.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			Double value = result.get(key).doubleValue();
			rr.put(key, value);
		}
		 List<Map.Entry<String, Double>> list_Data = mapValueSort(rr);
	        tran=new BigDecimal(list_Data.get(0).getValue());//订单内还有多个商品时，取最大的商品运费作为订单运费
		return tran;
	}
	
	/**
	 *获取赠品 
	 *
	 */
	public List<ModelGoodGiftInfo> getGifts(Map<String, Object> skuInfo){
		List<ModelGoodGiftInfo> gifts = new ArrayList<ModelGoodGiftInfo>();
		try {
			GetGoodGiftList ggft = new GetGoodGiftList();
			String pc = DbUp.upTable("pc_productinfo").one("product_code",skuInfo.get("productCode").toString()).get("product_code_old").toString();
			
			String siteNo = (null == skuInfo.get("siteNo") ? "" : skuInfo.get("siteNo").toString());	//2016-08-04改为一地入库跟多地入库的仓库编号都储存在此字段
			
			if("Y".equals(skuInfo.get("flag").toString())){//虚拟商品
				gifts = ggft.doRsync(pc, "C18");
			}else {
				String[] ss = siteNo.split(",");
				for (int i = 0; i < ss.length; i++) {
					gifts = ggft.doRsync(pc, ss[i]);
					if(gifts!=null&&!"".equals(gifts)&&!gifts.isEmpty()){
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gifts;
	}
	
	/***
	 *同步LD系统出错时针对已下单成功小订单进行处理
	 * @param bigOrderCode 大订单号
	 */
	public boolean handleOrder(String bigOrderCode){
		boolean result = true;
		try {
			List<Map<String, Object>> list= DbUp.upTable("oc_orderinfo").dataSqlList("SELECT order_code,buyer_code,out_order_code from oc_orderinfo where big_order_code=:big_order_code ", new MDataMap("big_order_code",bigOrderCode));
			DbUp.upTable("oc_orderinfo_upper").dataUpdate(new MDataMap("big_order_code",bigOrderCode,"delete_flag","1"), "delete_flag", "big_order_code");
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				String order_code= (String)map.get("order_code");
				String buyer_code= (String)map.get("buyer_code");
				String out_order_code= (String)map.get("out_order_code");
				String now=DateUtil.getSysDateTimeString();
				DbUp.upTable("oc_order_cancel_h").insert("order_code",order_code,"buyer_code",buyer_code,"out_order_code",out_order_code,"call_flag","1","create_time",now,"update_time",now,"canceler","system");
				//置成逻辑删除状态
				MDataMap updateMap = new MDataMap();
				updateMap.put("order_code", order_code);
				updateMap.put("order_status", "4497153900010006");
				updateMap.put("delete_flag", "1");
				DbUp.upTable("oc_orderinfo").dataUpdate(updateMap, "order_status,delete_flag", "order_code");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result=false;
		}
		return result;
	}
	/**
	 *根据订单金额拍讯进行订单减30元，如不够，小的进行补，一直补齐30元或订单金额全为0 
	 * 
	 */
	public List<Order> subtractMoney(List<Order> orders){
		List<Order> result = new ArrayList<Order>();
		Map<String, Double> ms = new HashMap<String, Double>();
		Map<String, Order> orderMap = new HashMap<String, Order>();
		for (int i = 0; i < orders.size(); i++) {
			Order order = orders.get(i);
			ms.put(order.getOrderCode(), order.getOrderMoney().doubleValue());
			orderMap.put(order.getOrderCode(), order);
		}
		//订单金额排序
        List<Map.Entry<String, Double>> list_Data = mapValueSort(ms); //针对订单金额从大到小排序
         
        //根据排序减满30
        BigDecimal subMoney = new BigDecimal(bConfig("familyhas.eveActivityMoney"));//需要减去的金额
        for (int i = 0; i < list_Data.size(); i++) {
			Map.Entry<String, Double> entry = list_Data.get(i);
			String orderCode = entry.getKey();
			BigDecimal orderMoney = new BigDecimal(entry.getValue());//订单金额
			Order order = orderMap.get(orderCode);
			BigDecimal dueMoney = order.getDueMoney();//应付款
			if(orderMoney.compareTo(subMoney)>=0){//订单金额大于需要立减的金额时
				orderMap.get(orderCode).setDueMoney((dueMoney.subtract(subMoney)).setScale(2, BigDecimal.ROUND_HALF_UP));
				orderMap.get(orderCode).setPromotionMoney(subMoney);
				OcOrderActivity acti = new OcOrderActivity();
				acti.setActivityName(bConfig("familyhas.eveActivityName"));//活动名称
				acti.setActivityType("449715400006");
				acti.setPreferentialMoney(subMoney.floatValue());// 优惠金额
				String yhq = "familyhas.quan_"+String.valueOf(subMoney.intValue());
				String actc = "familyhas.eveActivity_"+String.valueOf(subMoney.intValue());
				if(bConfig(actc)==null||"".equals(bConfig(actc))){
					acti.setActivityCode(bConfig("familyhas.eveActivity"));// 每单立减30元活动编号
					acti.setOutActiveCode(bConfig("familyhas.eveActivity"));
				}else {
					acti.setActivityCode(bConfig(actc));// 每单立减30元活动编号
					acti.setOutActiveCode(bConfig(actc));
				}
				if(bConfig(yhq)==null||"".equals(bConfig(yhq))){
					acti.setTicketCode(bConfig("familyhas.eveActivityquan"));
				}else {
					acti.setTicketCode(bConfig(yhq));
				}
				acti.setOrderCode(orderCode);
				orderMap.get(orderCode).getActivityList().add(acti);
				subMoney = new BigDecimal(0);
			}else {//订单金额小于需要立减的金额时
				orderMap.get(orderCode).setDueMoney((dueMoney.subtract(orderMoney)).setScale(2, BigDecimal.ROUND_HALF_UP));
				orderMap.get(orderCode).setPromotionMoney(orderMoney);
				OcOrderActivity acti = new OcOrderActivity();
				acti.setActivityName(bConfig("familyhas.eveActivityName"));//活动名称
				acti.setActivityType("449715400006");
				acti.setPreferentialMoney(orderMoney.floatValue());// 优惠金额
				String yhq = "familyhas.quan_"+String.valueOf(orderMoney.intValue());
				String actc = "familyhas.eveActivity_"+String.valueOf(orderMoney.intValue());
				acti.setActivityCode(bConfig(actc));// 每单立减30元活动编号
				acti.setOutActiveCode(bConfig(actc));
				acti.setTicketCode(bConfig(yhq));
				acti.setOrderCode(orderCode);
				orderMap.get(orderCode).getActivityList().add(acti);
				subMoney = subMoney.subtract(orderMoney);
			}
			if(subMoney.compareTo(new BigDecimal(0))==0){
				break;
			}
		}
        for(String key : orderMap.keySet()){
        	result.add(orderMap.get(key));
        }
		return result;
	}
	
	/**
	 *惠家有使用优惠券处理方法 (优惠券金额均等绑定在拆分的订单商品上)
	 *均等拆分方案：按照单个商品数量金额占总额比例获取单个商品优惠金额向下取整，如优惠金额向下取整后等于0，此商品不参与优惠，下一个商品再进行均摊,
	 *到最后一个商品时优惠券总额减去之前优惠的金额再均摊到此商品单品向上取整（此处有可能存在误差）
	 *如果购买数量均一样，则单价最高的商品不参与计算(边界情况，此方法为减小误差)
	 */
	public List<Order> useCouponsForProduct(MDataMap skuM ,List<Order> orders,MDataMap other,RootResult rootResult,List<String> couponCodes) {
		List<Order> result = orders;
		BigDecimal skusCount = new BigDecimal(0.00);//可用优惠券的商品总金额
		Map<String, Double> skuMonMap = new HashMap<String, Double>();//<商品编号,商品金额>
		Map<String, Double> skuNumMap = new HashMap<String, Double>();//<商品编号,商品数量>
		Map<String, Double> skuCouMon = new HashMap<String, Double>();//<商品编号,单品优惠券额度>
		int num = 0;//随机获取单品的数量
		List<GoodsInfoForAdd> goodsList = new ArrayList<GoodsInfoForAdd>();//需要进行优惠券平摊的商品列表
		for (int i = 0; i < orders.size(); i++) {//各参数赋值
			Order order = orders.get(i);
			for (int j = 0; j < order.getProductList().size(); j++) {
				OrderDetail od = order.getProductList().get(j);
				if("1".equals(od.getGiftFlag())){//非赠品
					skuMonMap.put(od.getSkuCode(), od.getSkuPrice().doubleValue());
					skuNumMap.put(od.getSkuCode(),Double.valueOf(od.getSkuNum()));
					num = od.getSkuNum();
					GoodsInfoForAdd add = new GoodsInfoForAdd();
					add.setArea_code(order.getAddress().getAreaCode());
					add.setProduct_code(od.getProductCode());
					add.setSku_code(skuM.containsKey(od.getSkuCode())?skuM.get(od.getSkuCode()):od.getSkuCode());
					add.setSku_num(od.getSkuNum());
					goodsList.add(add);
				}
			}
		}
		//获取可用优惠券的商品编号，map值为不为空的时候表示可用,key为sku_code,value为sku_num
		skuNumMap = new CouponsService().getAvailableCouponProduct(other.get("buyerCode"), couponCodes, goodsList,other.get("channelId"));
		if(skuNumMap!=null&&!skuNumMap.isEmpty()){
			Iterator<String> skuNumIterator = skuNumMap.keySet().iterator();
			//计算可用优惠券的商品总金额  skusCount
			while (skuNumIterator.hasNext()) {
				String key = (String) skuNumIterator.next();
				skusCount=skusCount.add(new BigDecimal(skuMonMap.get(key)).multiply(new BigDecimal(skuNumMap.get(key))));
			}
			/**
			 * 需要叠加处理
			 */
			//我爱爸爸
			List<BigDecimal> couponMoneys = new ArrayList<BigDecimal>();
			for(String couponCode : couponCodes) {
				BigDecimal couponMoney = new CouponsService().getCouponMoney(couponCode);//优惠券金额
				couponMoneys.add(couponMoney);
			}
			//计算优惠券总金额
			BigDecimal couponMoney = BigDecimal.ZERO;
			for(BigDecimal money : couponMoneys) {
				couponMoney.add(money);
			}
			// 根据礼金券的金额倒排序
			List<Map<String,BigDecimal>> couponList = new ArrayList<Map<String,BigDecimal>>();
			for(int i=0;i<couponCodes.size();i++ ) {
				Map<String,BigDecimal> item = new HashMap<String,BigDecimal>();
				item.put(couponCodes.get(i), couponMoneys.get(i));
				couponList.add(item);
			}
			//将礼金券金额从大到小排序
			Collections.sort(couponList, new Comparator<Map<String,BigDecimal>>() {
				public int compare(Map<String,BigDecimal> moenyOne, Map<String,BigDecimal> moneyTwo) {
					BigDecimal one = (BigDecimal) moenyOne.get(0);
					BigDecimal two = (BigDecimal) moneyTwo.get(0);
					return two.compareTo(one);
				}
			});
			BigDecimal productMoney = null;
			for (int i = 0; i < orders.size(); i++) {
				Order or = orders.get(i);
				BigDecimal couponPay = new BigDecimal(0.00);//使用优惠券支付的金额
				for (int j = 0; j < or.getProductList().size(); j++) {
					OrderDetail od = or.getProductList().get(j);
					if(!"1".equals(od.getGiftFlag()) || !skuNumMap.containsKey(od.getSkuCode())){
						continue;
					}
					productMoney = od.getSkuPrice().multiply(new BigDecimal(od.getSkuNum()));
					BigDecimal useCouponMoney = productMoney.divide(skusCount,4,BigDecimal.ROUND_HALF_UP).multiply(couponMoney);
					BigDecimal eveCouMoney = useCouponMoney.divide(new BigDecimal(od.getSkuNum())).setScale(2, BigDecimal.ROUND_HALF_UP);		
					
					// 使用的金额不能超过商品金额
					if(eveCouMoney.compareTo(od.getSkuPrice()) > 0){
						eveCouMoney = od.getSkuPrice();
					}
					
					// 确认单件商品使用的优惠券金额
					useCouponMoney = eveCouMoney.multiply(new BigDecimal(od.getSkuNum()));
					
					// 循环礼金券拆分使用的商品
					Map<String,BigDecimal> couponItem = null;
					BigDecimal useC = BigDecimal.ZERO; // 已经拆分的金额
					BigDecimal preferentialMoney = BigDecimal.ZERO;  //优惠金额
					for (int k = 0; k < couponList.size(); k++) {
						couponItem = couponList.get(k);
						
						// 优惠券的剩余金额
						BigDecimal money = (BigDecimal)couponItem.get("money");
						BigDecimal useM = null;
						
						// 忽略已经拆分完的优惠券
						if(money.compareTo(BigDecimal.ZERO) == 0){
							continue;
						}
						
						// 不能超过优惠券的使用金额
						if(useC.add(money).compareTo(useCouponMoney) > 0){
							useM = useCouponMoney.subtract(useC);
						}else{
							useM = money;
						}
						
						// 更新优惠券的剩余金额
						couponItem.put("money", money.subtract(useM));
						
						// 记录下优惠券使用记录
						OcOrderActivity oa = new OcOrderActivity();
						oa.setProductCode(od.getProductCode());
						oa.setTicketCode(couponItem.get("code").toString());
						oa.setActivityName("优惠券");
						oa.setOrderCode(orders.get(i).getOrderCode());
						oa.setOutActiveCode(bConfig("familyhas.coupon_code"));//优惠券使用虚拟活动号传至LD
						oa.setSkuCode(od.getSkuCode());
						oa.setPreferentialMoney(useM.floatValue());
						orders.get(i).getActivityList().add(oa);
						preferentialMoney.add(useM);
					}
					BigDecimal sa = orders.get(i).getProductList().get(j).getCouponPrice();
					if(sa==null){
						sa = new BigDecimal(0.00);
					}
					orders.get(i).getProductList().get(j).setCouponPrice(sa.add(eveCouMoney));//订单商品上记优惠金额
					orders.get(i).getProductList().get(j).setSkuPrice(orders.get(i).getProductList().get(j).getSkuPrice().subtract(eveCouMoney));
					orders.get(i).setDueMoney(or.getDueMoney().subtract(preferentialMoney));//应付款金额-优惠金额
					couponPay = couponPay.add(preferentialMoney);
				}
				OcOrderPay op = new OcOrderPay();//插入一条优惠券支付信息
				op.setMerchantId(other.get("buyerCode"));
				op.setOrderCode(or.getOrderCode());
				op.setPaySequenceid(StringUtils.join(couponList, ","));
				op.setPayedMoney(couponPay.floatValue());
				op.setPayType("449746280002");
				if(orders.get(i).getOcOrderPayList()==null){
					List<OcOrderPay> li = new ArrayList<OcOrderPay>();
					li.add(op);
					orders.get(i).setOcOrderPayList(li);
				}else {
					orders.get(i).getOcOrderPayList().add(op);
				}
			}
			/**
			 * 
			BigDecimal couponMoney = new CouponsService().getCouponMoney(other.get("coupon_codes"));//优惠券金额
			BigDecimal overcouponMoney = new BigDecimal(0.00);//已使用优惠券金额
			String couponCode = other.get("coupon_codes");//优惠券编号
			//边界情况处理逻辑:商品种类大于两种时商品数量都相等的情况，默认为true
			boolean bjqk = true;
			if(skuNumMap.size()>1){
				Iterator<String> it = skuNumMap.keySet().iterator();
				while(it.hasNext()){
					if(skuNumMap.get(it.next())!=num){
						bjqk=false;
						break;
					}
				}
			}else {
				bjqk=false;
			}
			if(couponMoney.doubleValue()>0&&orders.size()>0){
				List<Map.Entry<String, Double>> list_Data = new ArrayList<Map.Entry<String,Double>>();
				if(bjqk){//边界情况
					Map<String, Double> skuSPMap = new HashMap<String, Double>();//<商品编号,商品金额>
					Iterator<String> ks = skuNumMap.keySet().iterator();
					while (ks.hasNext()) {
						String key = (String) ks.next();
						skuSPMap.put(key, skuMonMap.get(key));
					}
					list_Data = mapValueSortDesc(skuSPMap);//数量相等时，按金额倒序
				}else{
					//一般情况处理逻辑
					list_Data = mapValueSort(skuNumMap);//一般情况按数量倒序
				}
				for (int i = 0; i < list_Data.size(); i++) {
					Map.Entry<String, Double> entry = list_Data.get(i);
					String skuCode = entry.getKey();
					Double skuNum = skuNumMap.get(skuCode);
					BigDecimal skuPrice = new BigDecimal(skuMonMap.get(skuCode));
					if(i==list_Data.size()-1){//最后一个商品特殊处理
						Double eveSkuCouMon = Math.ceil(((couponMoney.subtract(overcouponMoney)).divide(new BigDecimal(skuNum),2,BigDecimal.ROUND_UP)).doubleValue());//向上取整 单品优惠金额
						if(eveSkuCouMon.compareTo(skuPrice.doubleValue())>=0){
							eveSkuCouMon=skuPrice.doubleValue();
						}
						skuCouMon.put(skuCode, eveSkuCouMon);
					}else {//一般商品处理
						Double eveSkuCouMon = Math.floor(((skuPrice.multiply(couponMoney).divide(skusCount,2,BigDecimal.ROUND_DOWN))).doubleValue());//向下取整 单品优惠金额
						if(eveSkuCouMon>0){
							if(eveSkuCouMon.compareTo(skuPrice.doubleValue())>=0){
								eveSkuCouMon=skuPrice.doubleValue();
							}
							skuCouMon.put(skuCode, eveSkuCouMon);
						}
						overcouponMoney = overcouponMoney.add(new BigDecimal(eveSkuCouMon).multiply(new BigDecimal(skuNum)));
					}
				}
				for (int i = 0; i < orders.size(); i++) {
					Order or = orders.get(i);
					BigDecimal couponPay = new BigDecimal(0.00);//使用优惠券支付的金额
					for (int j = 0; j < or.getProductList().size(); j++) {
						OrderDetail od = or.getProductList().get(j);
						if("1".equals(od.getGiftFlag())&&skuCouMon.containsKey(od.getSkuCode())){
							Double eveCouMoney = skuCouMon.get(od.getSkuCode());
							OcOrderActivity oa = new OcOrderActivity();
							oa.setProductCode(od.getProductCode());
							oa.setTicketCode(couponCode);
							oa.setActivityName("优惠券");
							oa.setOrderCode(orders.get(i).getOrderCode());
							oa.setOutActiveCode(bConfig("familyhas.coupon_code"));//优惠券使用虚拟活动号传至LD
							oa.setSkuCode(od.getSkuCode());
							oa.setPreferentialMoney((new BigDecimal(eveCouMoney).multiply(new BigDecimal(od.getSkuNum()))).floatValue());
							orders.get(i).getActivityList().add(oa);
							BigDecimal sa = orders.get(i).getProductList().get(j).getCouponPrice();
							if(sa==null){
								sa = new BigDecimal(0.00);
							}
							orders.get(i).getProductList().get(j).setCouponPrice(sa.add(new BigDecimal(eveCouMoney)));//订单商品上记优惠金额
							orders.get(i).getProductList().get(j).setSkuPrice(orders.get(i).getProductList().get(j).getSkuPrice().subtract(new BigDecimal(eveCouMoney)));
							orders.get(i).setDueMoney(or.getDueMoney().subtract(new BigDecimal(oa.getPreferentialMoney())));//应付款金额-优惠金额
							couponPay=couponPay.add(new BigDecimal(oa.getPreferentialMoney()));
						}
					}
					OcOrderPay op = new OcOrderPay();//插入一条优惠券支付信息
					op.setMerchantId(other.get("buyerCode"));
					op.setOrderCode(or.getOrderCode());
					op.setPaySequenceid(couponCode);
					op.setPayedMoney(couponPay.floatValue());
					op.setPayType("449746280002");
					if(orders.get(i).getOcOrderPayList()==null){
						List<OcOrderPay> li = new ArrayList<OcOrderPay>();
						li.add(op);
						orders.get(i).setOcOrderPayList(li);
					}else {
						orders.get(i).getOcOrderPayList().add(op);
					}
				}
			}
			*/
		}
		return result;
	}
	/**
	 *惠家有使用优惠券处理方法 (优惠券金额均等绑定在拆分的订单商品上)
	 *均等拆分方案：按照单个商品数量金额占总额比例获取单个商品优惠金额向下取整，如优惠金额向下取整后等于0，此商品不参与优惠，下一个商品再进行均摊,
	 *到最后一个商品时优惠券总额减去之前优惠的金额再均摊到此商品单品向上取整（此处有可能存在误差）
	 *如果购买数量均一样，则单价最高的商品不参与计算(边界情况，此方法为减小误差)
	 */
	public List<Order> useWgsForOrder(List<Order> orders,MDataMap other,RootResult rootResult) {
		List<Order> result = orders;
		BigDecimal skusCount = new BigDecimal(0.00);//可用微公社的商品总金额
		Map<String, Double> skuMonMap = new HashMap<String, Double>();//<商品编号,商品金额>
		Map<String, Double> skuNumMap = new HashMap<String, Double>();//<商品编号,商品数量>
		Map<String, Double> skuCouMon = new HashMap<String, Double>();//<商品编号,单品使用微公社额度>
		int num = 0;//随机获取单品的数量
		for (int i = 0; i < orders.size(); i++) {//各参数赋值
			Order order = orders.get(i);
			for (int j = 0; j < order.getProductList().size(); j++) {
				OrderDetail od = order.getProductList().get(j);
				if("1".equals(od.getGiftFlag())){
					skuMonMap.put(od.getSkuCode(), od.getSkuPrice().doubleValue());
					skuNumMap.put(od.getSkuCode(),Double.valueOf(od.getSkuNum()));
					num = od.getSkuNum();
				}
			}
		}
		if(skuNumMap!=null&&!skuNumMap.isEmpty()){
			Iterator<String> skuNumIterator = skuNumMap.keySet().iterator();
			while (skuNumIterator.hasNext()) {
				String key = (String) skuNumIterator.next();
				skusCount=skusCount.add(new BigDecimal(skuMonMap.get(key)).multiply(new BigDecimal(skuNumMap.get(key))));
			}
			BigDecimal couponMoney = new BigDecimal(other.get("wgsUseMoney"));//优惠券金额
			BigDecimal overcouponMoney = new BigDecimal(0.00);//已使用优惠券金额
			//边界情况处理逻辑:商品种类大于两种时商品数量都相等的情况，默认为true
			boolean bjqk = true;
			if(skuNumMap.size()>1){
				Iterator<String> it = skuNumMap.keySet().iterator();
				while(it.hasNext()){
					if(skuNumMap.get(it.next())!=num){
						bjqk=false;
						break;
					}
				}
			}else {
				bjqk=false;
			}
			if(couponMoney.doubleValue()>0&&orders.size()>0){
				List<Map.Entry<String, Double>> list_Data = new ArrayList<Map.Entry<String,Double>>();
				if(bjqk){//边界情况
					Map<String, Double> skuSPMap = new HashMap<String, Double>();//<商品编号,商品金额>
					Iterator<String> ks = skuNumMap.keySet().iterator();
					while (ks.hasNext()) {
						String key = (String) ks.next();
						skuSPMap.put(key, skuMonMap.get(key));
					}
					list_Data = mapValueSortDesc(skuSPMap);//数量相等时，按金额倒序
				}else{
					//一般情况处理逻辑
					list_Data = mapValueSort(skuNumMap);//一般情况按数量倒序
				}
				for (int i = 0; i < list_Data.size(); i++) {
					Map.Entry<String, Double> entry = list_Data.get(i);
					String skuCode = entry.getKey();
					Double skuNum = skuNumMap.get(skuCode);
					BigDecimal skuPrice = new BigDecimal(skuMonMap.get(skuCode));
					if(i==list_Data.size()-1){//最后一个商品特殊处理
						Double eveSkuCouMon = Math.ceil(((couponMoney.subtract(overcouponMoney)).divide(new BigDecimal(skuNum),2,BigDecimal.ROUND_UP)).doubleValue());//向上取整 单品优惠金额
						if(eveSkuCouMon.compareTo(skuPrice.doubleValue())>=0){
							eveSkuCouMon=skuPrice.doubleValue();
						}
						skuCouMon.put(skuCode, eveSkuCouMon);
					}else {//一般商品处理
						Double eveSkuCouMon = Math.floor(((skuPrice.multiply(couponMoney).divide(skusCount,2,BigDecimal.ROUND_DOWN))).doubleValue());//向下取整 单品优惠金额
						if(eveSkuCouMon>0){
							if(eveSkuCouMon.compareTo(skuPrice.doubleValue())>=0){
								eveSkuCouMon=skuPrice.doubleValue();
							}
							skuCouMon.put(skuCode, eveSkuCouMon);
						}
						overcouponMoney = overcouponMoney.add(new BigDecimal(eveSkuCouMon).multiply(new BigDecimal(skuNum)));
					}
				}
				BigDecimal restMoney = new BigDecimal(other.get("wgsUseMoney"));//使用金额金额
				for (int i = 0; i < orders.size(); i++) {
					Order or = orders.get(i);
					BigDecimal couponPay = new BigDecimal(0.00);//使用微公社支付的金额
					for (int j = 0; j < or.getProductList().size(); j++) {
						OrderDetail od = or.getProductList().get(j);
						if("1".equals(od.getGiftFlag())&&skuCouMon.containsKey(od.getSkuCode())){
							Double eveCouMoney = skuCouMon.get(od.getSkuCode());
							orders.get(i).getProductList().get(j).setGroupPrice(new BigDecimal(eveCouMoney));
							orders.get(i).getProductList().get(j).setSkuPrice(od.getSkuPrice().subtract(new BigDecimal(eveCouMoney)));
							orders.get(i).setDueMoney(or.getDueMoney().subtract(new BigDecimal(eveCouMoney).multiply(new BigDecimal(od.getSkuNum()))));//应付款金额-微公社金额
							couponPay=couponPay.add(new BigDecimal(eveCouMoney).multiply(new BigDecimal(od.getSkuNum())));
						}
					}
					OcOrderPay op = new OcOrderPay();//插入一条优惠券支付信息
					op.setMerchantId(other.get("buyerCode"));
					op.setOrderCode(or.getOrderCode());
					op.setPayedMoney(couponPay.floatValue());
					op.setPayType("449746280009");
					if(restMoney.doubleValue()>0&&restMoney.compareTo(couponPay)>=0){
						restMoney=restMoney.subtract(couponPay);
						op.setPayRemark(String.valueOf(couponPay.doubleValue()));
					}else if(restMoney.doubleValue()>0&&restMoney.compareTo(couponPay)<0) {
						op.setPayRemark(String.valueOf(restMoney.doubleValue()));
						restMoney=new BigDecimal(0.00);
					}else {
						op.setPayRemark("0");
					}
					if(orders.get(i).getOcOrderPayList()==null){
						List<OcOrderPay> li = new ArrayList<OcOrderPay>();
						li.add(op);
						orders.get(i).setOcOrderPayList(li);
					}else {
						orders.get(i).getOcOrderPayList().add(op);
					}
				}
			}
		}
		return result;
	}
	/**
	 *惠家有使用优惠券处理方法 (优惠券金额绑定在应付款最大的订单上)
	 */
	public List<Order> useCoupons(List<Order> orders,MDataMap other,RootResult rootResult) {
		List<Order> result = new ArrayList<Order>();
		Map<String, Order> orderMap = new HashMap<String, Order>();
		Map<String, Double> ms = new HashMap<String, Double>();
		BigDecimal am = new BigDecimal(0.00);//所有订单的总金额
		for (int i = 0; i < orders.size(); i++) {
			Order order = orders.get(i);
			am = am.add(order.getOrderMoney());
			ms.put(order.getOrderCode(), order.getOrderMoney().doubleValue());
			orderMap.put(order.getOrderCode(), order);
		}
		CouponInfoRootResult cf = (new CouponUtil()).getCouponInfo(other.get("buyerCode"), other.get("coupon_codes"), am);
		if(cf.getResultCode()>1){
			rootResult.setResultCode(cf.getResultCode());
			rootResult.setResultMessage(cf.getResultMessage());
			result = orders;
			return result;
		}
		BigDecimal subMoney = cf.getSurplusMoney();
		String couponCode = cf.getCouponCode();
		if(subMoney.doubleValue()>0&&orders.size()>0){
			//订单金额排序
	        List<Map.Entry<String, Double>> list_Data = mapValueSort(ms); //针对订单金额从大到小排序
			for (int i = 0; i < list_Data.size(); i++) {
				Map.Entry<String, Double> entry = list_Data.get(i);
				String orderCode = entry.getKey();//订单编号
				BigDecimal dueMoney =  orderMap.get(orderCode).getDueMoney();//应付款
				orderMap.get(orderCode).setPayType("449716200001");//使用优惠券必须在线支付(拆单和此处共同控制)
				if(dueMoney.doubleValue()>0&&subMoney.doubleValue()>0&&dueMoney.compareTo(subMoney)>=0){//订单金额大于需要立减的金额时
					orderMap.get(orderCode).setDueMoney((dueMoney.subtract(subMoney)).setScale(2, BigDecimal.ROUND_HALF_UP));
					OcOrderPay op = new OcOrderPay();
					op.setMerchantId(other.get("buyerCode"));
					op.setOrderCode(orderCode);
					op.setPaySequenceid(couponCode);
					op.setPayedMoney(subMoney.floatValue());
					op.setPayType("449746280002");
					if(orderMap.get(orderCode).getOcOrderPayList()==null){
						List<OcOrderPay> opList = new ArrayList<OcOrderPay>();
						opList.add(op);
						orderMap.get(orderCode).setOcOrderPayList(opList);
					}else{
						orderMap.get(orderCode).getOcOrderPayList().add(op);
					}
					subMoney = new BigDecimal(0);
				}else if(dueMoney.doubleValue()>0&&subMoney.doubleValue()>0&&dueMoney.compareTo(subMoney)<0) {//订单金额小于需要优惠的金额时
					orderMap.get(orderCode).setDueMoney(new BigDecimal(0.00));
					OcOrderPay op = new OcOrderPay();
					op.setMerchantId(other.get("buyerCode"));
					op.setOrderCode(orderCode);
					op.setPaySequenceid(couponCode);
					op.setPayedMoney(dueMoney.floatValue());
					op.setPayType("449746280002");
					if(orderMap.get(orderCode).getOcOrderPayList()==null){
						List<OcOrderPay> opList = new ArrayList<OcOrderPay>();
						opList.add(op);
						orderMap.get(orderCode).setOcOrderPayList(opList);
					}else{
						orderMap.get(orderCode).getOcOrderPayList().add(op);
					}
					subMoney = subMoney.subtract(dueMoney);
				}
			}
	        for(String key : orderMap.keySet()){
	        	result.add(orderMap.get(key));
	        }
		}else {
			result=orders;
		}
		
		return result;
	}
	
	/**
	 *惠家有使用优惠券处理方法 (优惠券金额均等绑定在拆分的订单商品上)
	 *均等拆分方案：按照单个商品数量金额占总额比例获取单个商品优惠金额向下取整，如优惠金额向下取整后等于0，此商品不参与优惠，下一个商品再进行均摊,
	 *到最后一个商品时优惠券总额减去之前优惠的金额再均摊到此商品单品向上取整（此处有可能存在误差）
	 *如果购买数量均一样，则单价最高的商品不参与计算(边界情况，此方法为减小误差)
	 */
	public List<Order> useCouponsForAve(List<Order> orders,MDataMap other,RootResult rootResult) {
		List<Order> result = orders;
		BigDecimal dueCount = new BigDecimal(0.00);//应付总金额
		BigDecimal skusCount = new BigDecimal(0.00);//商品总金额
		Map<String, Double> skuMonMap = new HashMap<String, Double>();//<商品编号,商品金额>
		Map<String, Double> skuNumMap = new HashMap<String, Double>();//<商品编号,商品数量>
		Map<String, Double> skuCouMon = new HashMap<String, Double>();//<商品编号,单品优惠券额度>
		int num = 0;//随机获取单品的数量
		for (int i = 0; i < orders.size(); i++) {//各参数赋值
			Order order = orders.get(i);
			dueCount = dueCount.add(order.getDueMoney());
			for (int j = 0; j < order.getProductList().size(); j++) {
				OrderDetail od = order.getProductList().get(j);
				if("1".equals(od.getGiftFlag())){
					skusCount = skusCount.add(od.getSkuPrice().multiply(new BigDecimal(od.getSkuNum())));
					skuMonMap.put(od.getSkuCode(), od.getSkuPrice().doubleValue());
					skuNumMap.put(od.getSkuCode(), Double.valueOf(od.getSkuNum()));
					num = od.getSkuNum();
				}
			}
		}
		CouponInfoRootResult cf = (new CouponUtil()).getCouponInfo(other.get("buyerCode"), other.get("coupon_codes"), dueCount);
		if(cf.getResultCode()>1){//判断用户输入的优惠券是否可用
			rootResult.setResultCode(cf.getResultCode());
			rootResult.setResultMessage(cf.getResultMessage());
			result = orders;
			return result;
		}
		BigDecimal couponMoney = cf.getSurplusMoney();//优惠券金额
		BigDecimal overcouponMoney = new BigDecimal(0.00);//已使用优惠券金额
		String couponCode = cf.getCouponCode();//优惠券编号
		//边界情况处理逻辑:商品种类大于两种时商品数量都相等的情况，默认为true
		boolean bjqk = true;
		if(skuNumMap.size()>1){
			Iterator<String> it = skuNumMap.keySet().iterator();
			while(it.hasNext()){
				if(skuNumMap.get(it.next())!=num){
					bjqk=false;
					break;
				}
			}
		}else {
			bjqk=false;
		}
		if(couponMoney.doubleValue()>0&&orders.size()>0){
			List<Map.Entry<String, Double>> list_Data = new ArrayList<Map.Entry<String,Double>>();
			if(bjqk){//边界情况
				list_Data = mapValueSortDesc(skuMonMap);//数量相等时，按金额倒序
			}else{
				//一般情况处理逻辑
				list_Data = mapValueSort(skuNumMap);//一般情况按数量倒序
			}
			for (int i = 0; i < list_Data.size(); i++) {
				Map.Entry<String, Double> entry = list_Data.get(i);
				String skuCode = entry.getKey();
				Double skuNum = skuNumMap.get(skuCode);
				BigDecimal skuPrice = new BigDecimal(skuMonMap.get(skuCode));
				if(i==list_Data.size()-1){//最后一个商品特殊处理
					Double eveSkuCouMon = Math.ceil(((couponMoney.subtract(overcouponMoney)).divide(new BigDecimal(skuNum),2,BigDecimal.ROUND_UP)).doubleValue());//向上取整 单品优惠金额
					skuCouMon.put(skuCode, eveSkuCouMon);
				}else {//一般商品处理
					Double eveSkuCouMon = Math.floor(((skuPrice.multiply(couponMoney).divide(dueCount,2,BigDecimal.ROUND_DOWN))).doubleValue());//向下取整 单品优惠金额
					if(eveSkuCouMon>0){
						skuCouMon.put(skuCode, eveSkuCouMon);
					}
					overcouponMoney = overcouponMoney.add(new BigDecimal(eveSkuCouMon).multiply(new BigDecimal(skuNum)));
				}
			}
			for (int i = 0; i < orders.size(); i++) {
				Order or = orders.get(i);
				BigDecimal couponPay = new BigDecimal(0.00);//使用优惠券支付的金额
				for (int j = 0; j < or.getProductList().size(); j++) {
					OrderDetail od = or.getProductList().get(j);
					if("1".equals(od.getGiftFlag())&&skuCouMon.containsKey(od.getSkuCode())){
						Double eveCouMoney = skuCouMon.get(od.getSkuCode());
						OcOrderActivity oa = new OcOrderActivity();
						oa.setTicketCode(couponCode);
						oa.setActivityName("优惠券");
						oa.setOrderCode(orders.get(i).getOrderCode());
						oa.setOutActiveCode(bConfig("familyhas.coupon_code"));//优惠券使用虚拟活动号传至LD
						oa.setSkuCode(od.getSkuCode());
						oa.setPreferentialMoney((new BigDecimal(eveCouMoney).multiply(new BigDecimal(od.getSkuNum()))).floatValue());
						orders.get(i).getActivityList().add(oa);
						BigDecimal sa = orders.get(i).getProductList().get(j).getSaveAmt();
						if(sa==null){
							sa = new BigDecimal(0.00);
						}
						orders.get(i).getProductList().get(j).setCouponPrice(sa.add(new BigDecimal(eveCouMoney)));//订单商品上记优惠金额
						orders.get(i).setDueMoney(or.getDueMoney().subtract(new BigDecimal(oa.getPreferentialMoney())));//应付款金额-优惠金额
						couponPay=couponPay.add(new BigDecimal(oa.getPreferentialMoney()));
					}
				}
				OcOrderPay op = new OcOrderPay();//插入一条优惠券支付信息
				op.setMerchantId(other.get("buyerCode"));
				op.setOrderCode(or.getOrderCode());
				op.setPaySequenceid(couponCode);
				op.setPayedMoney(couponPay.floatValue());
				op.setPayType("449746280002");
				if(orders.get(i).getOcOrderPayList()==null){
					List<OcOrderPay> li = new ArrayList<OcOrderPay>();
					li.add(op);
					orders.get(i).setOcOrderPayList(li);
				}else {
					orders.get(i).getOcOrderPayList().add(op);
				}
			}
		}
		return result;
	}
	
	/**
	 *家有汇储值金、暂存款、积分支付处理方法 
	 * 注：内购员工不能使用积分
	 */
	public List<Order> recheckMoneyJyh(List<Order> orders,MDataMap other) {
		List<Order> result = new ArrayList<Order>();
		Double czj = Double.valueOf(other.get("czj"));//储值金
		Double zck = Double.valueOf(other.get("zck"));//暂存款
		Double jf = Double.valueOf(other.get("jf"));//积分
		TreeMap<String, BigDecimal> zf = new TreeMap<String, BigDecimal>();
		if(czj>0){
			zf.put("449746280006", new BigDecimal(czj));
		}
		if(zck>0){
			zf.put("449746280007", new BigDecimal(zck));
		}
		if(jf>0){
			zf.put("449746280008", new BigDecimal(jf));
		}
		if(zf!=null&&!zf.isEmpty()){
			Map<String, Double> ms = new HashMap<String, Double>();
			Map<String, Order> orderMap = new HashMap<String, Order>();
			BigDecimal am = new BigDecimal(0.00);//所有订单的总金额
			for (int i = 0; i < orders.size(); i++) {
				Order order = orders.get(i);
				am = am.add(order.getOrderMoney());
				ms.put(order.getOrderCode(), order.getOrderMoney().doubleValue());
				orderMap.put(order.getOrderCode(), order);
			}
			//订单金额排序
	        List<Map.Entry<String, Double>> list_Data = mapValueSort(ms); //针对订单金额从大到小排序
	        Iterator<String> iterator = zf.keySet().iterator();
	        while (iterator.hasNext()) {
				String payType = (String) iterator.next();//支付方式
				BigDecimal subMoney = zf.get(payType);//优惠的money
				for (int i = 0; i < list_Data.size(); i++) {
					Map.Entry<String, Double> entry = list_Data.get(i);
					String orderCode = entry.getKey();//订单编号
					BigDecimal dueMoney =  orderMap.get(orderCode).getDueMoney();//应付款
					if(dueMoney.doubleValue()>0&&subMoney.doubleValue()>0&&dueMoney.compareTo(subMoney)>=0){//订单金额大于需要立减的金额时
						orderMap.get(orderCode).setDueMoney((dueMoney.subtract(subMoney)).setScale(2, BigDecimal.ROUND_HALF_UP));
						orderMap.get(orderCode).setPromotionMoney(subMoney.add(orderMap.get(orderCode).getPromotionMoney()));
						OcOrderPay op = new OcOrderPay();
						op.setMerchantId(other.get("buyerCode"));
						op.setOrderCode(orderCode);
						op.setPayedMoney(subMoney.floatValue());
						op.setPayType(payType);
						if(orderMap.get(orderCode).getOcOrderPayList()==null){
							List<OcOrderPay> opList = new ArrayList<OcOrderPay>();
							opList.add(op);
							orderMap.get(orderCode).setOcOrderPayList(opList);
						}else{
							orderMap.get(orderCode).getOcOrderPayList().add(op);
						}
						subMoney = new BigDecimal(0);
					}else if(dueMoney.doubleValue()>0&&subMoney.doubleValue()>0&&dueMoney.compareTo(subMoney)<0) {//订单金额小于需要优惠的金额时
						orderMap.get(orderCode).setDueMoney(new BigDecimal(0.00));
						orderMap.get(orderCode).setPromotionMoney(dueMoney.add(orderMap.get(orderCode).getPromotionMoney()));
						OcOrderPay op = new OcOrderPay();
						op.setMerchantId(other.get("buyerCode"));
						op.setOrderCode(orderCode);
						op.setPayedMoney(dueMoney.floatValue());
						op.setPayType(payType);
						if(orderMap.get(orderCode).getOcOrderPayList()==null){
							List<OcOrderPay> opList = new ArrayList<OcOrderPay>();
							opList.add(op);
							orderMap.get(orderCode).setOcOrderPayList(opList);
						}else{
							orderMap.get(orderCode).getOcOrderPayList().add(op);
						}
						subMoney = subMoney.subtract(dueMoney);
					}
				}
			}
	        for(String key : orderMap.keySet()){
	        	result.add(orderMap.get(key));
	        }
		}else {
			result=orders;
		}
		
		return result;
	}
	/**
	 *满399减50
	 * 
	 */
	public List<Order> reduMoney(List<Order> orders){
		List<Order> result = new ArrayList<Order>();
		Map<String, Double> ms = new HashMap<String, Double>();
		Map<String, Order> orderMap = new HashMap<String, Order>();
		BigDecimal am = new BigDecimal(0.00);//所有订单的总金额
		for (int i = 0; i < orders.size(); i++) {
			Order order = orders.get(i);
			am = am.add(order.getOrderMoney());
			ms.put(order.getOrderCode(), order.getOrderMoney().doubleValue());
			orderMap.put(order.getOrderCode(), order);
		}
		//订单金额排序
        List<Map.Entry<String, Double>> list_Data = mapValueSort(ms); //针对订单金额从大到小排序
        //根据排序减满50
        BigDecimal fullMoney = new BigDecimal(bConfig("familyhas.fullSubActivityFullMoney"));//需要满金额
        BigDecimal subMoney = new BigDecimal(bConfig("familyhas.fullSubActivityMoney"));//减去的金额
		Map.Entry<String, Double> entry = list_Data.get(0);
		String orderCode = entry.getKey();
		Order order = orderMap.get(orderCode);
		BigDecimal dueMoney = order.getDueMoney();//应付款
		//针对订单内的商品进行从大到小排序，满399减50放在单品金额大的商品上
		Map<String, Double> ps = new HashMap<String, Double>();
		for(int i = 0; i < order.getProductList().size(); i++){
			if("1".equals(order.getProductList().get(i).getGiftFlag())){
				ps.put(order.getProductList().get(i).getSkuCode(), order.getProductList().get(i).getSkuPrice().doubleValue());
			}
		}
		String maxskuCode = mapValueSort(ps).get(0).getKey();
		if(am.compareTo(fullMoney)>=0){
			orderMap.get(orderCode).setDueMoney((dueMoney.subtract(subMoney)).setScale(2, BigDecimal.ROUND_HALF_UP));
			orderMap.get(orderCode).setPromotionMoney(subMoney);
			OcOrderActivity acti = new OcOrderActivity();
			acti.setSkuCode(maxskuCode);
			acti.setActivityName(bConfig("familyhas.fullSubActivityName"));//活动名称
			acti.setActivityType("449715400006");
			acti.setPreferentialMoney(subMoney.floatValue());// 优惠金额
			String yhq = "familyhas.fullSubActivityquan";
			String actc = "familyhas.fullSubActivityCode";
			acti.setActivityCode(bConfig(actc));
			acti.setOutActiveCode(bConfig(actc));
			acti.setTicketCode(bConfig(yhq));
			acti.setOrderCode(orderCode);
			orderMap.get(orderCode).getActivityList().add(acti);
	        for(String key : orderMap.keySet()){
	        	result.add(orderMap.get(key));
	        }
		}else{
			return orders;
		}
		return result;
	}
	
	/**
	 * 根据productCode查询商品的赠品（仅支持惠家有调用）
	 * @param productCodes 格式商品编号中间用逗号隔开：code,code,code,...
	 * @param channelId 渠道编号，（为空默认）惠家有app：449747430001；wap商城：449747430002；微信商城：449747430003，惠家有PC订单:449747430004
	 * @return
	 * @author ligj
	 */
	public Map<String, String> getProductGifts(String productCodes,String channelId){
		if (StringUtils.isBlank(channelId)) {
			channelId = "449747430001";
		}
		
		//key为productCode,value为赠品
		Map<String,String> resultMap = new HashMap<String, String>();
		if (StringUtils.isEmpty(productCodes)) {
			return resultMap;
		}
		//内联赠品
		Map<String,String> innerGiftMap = new HashMap<String, String>();
		//外联赠品
		Map<String,String> outerGiftMap = new HashMap<String, String>();
		
		String[] productCodeArr = productCodes.split(",");
		
		String gift = "内联赠品";
		
		LoadProductInfo loadProductInfo = new LoadProductInfo();
		LoadGiftSkuInfo loadGiftSkuInfo = new LoadGiftSkuInfo();
		String qrccodeMark = bConfig("productcenter.qrcode_product_mark");
		qrccodeMark = (null == qrccodeMark ? "" : qrccodeMark);
		PlusSupportEvent plusSupportEvent = new PlusSupportEvent();
		for (String productCode : productCodeArr) {
			//判断扫码购渠道
			boolean flagSMG = false;
			
			for (String smgMark : qrccodeMark.split(",")) {
				if (productCode.contains(smgMark)) {
					flagSMG = true;
					break;
				}
			}
			
			if (PlusHelperEvent.checkEventItem(productCode)) {
				PlusModelEventItemProduct eventItemtInfo = plusSupportEvent.upItemProductByIcCode(productCode);
				if (null != eventItemtInfo) {
					productCode = eventItemtInfo.getProductCode();
				}
			}
			
			//获取内联赠品
			PlusModelProductInfo plusModelProductinfo =loadProductInfo.upInfoByCode(new PlusModelProductQuery(productCode));
			if (null != plusModelProductinfo) {
				for (PlusModelPropertyInfo properties : plusModelProductinfo.getPropertyInfoList()) {
					if (gift.equals(properties.getPropertykey())) {
						String startDate = properties.getStartDate();  
						String endDate = properties.getEndDate(); 
						if(StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)){
							SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String currDate=sdf.format(new Date());
							if(startDate.compareTo(currDate) < 0 && currDate.compareTo(endDate) < 0){
								innerGiftMap.put(productCode , properties.getPropertyValue());
							}else{
								innerGiftMap.put(productCode , "");
							}
						}else{   // 如果是历史数据，没有被编辑过的商品则开始时间和结束时间都是空，则不做处理 - Yangcl
							innerGiftMap.put(productCode , properties.getPropertyValue());
						}
					}
				}
			}
			
			//获取缓存中的外联赠品
			 PlusModelSkuQuery query = new PlusModelSkuQuery();
			 query.setCode(productCode);
			 PlusModelGitfSkuInfoList gitList = loadGiftSkuInfo.upInfoByCode(query);
			 
			 if (null != gitList && null != gitList.getGiftSkuinfos()) {
				 List<PlusModelGiftSkuinfo> giftSkuinfos = gitList.getGiftSkuinfos();
				
				 for (PlusModelGiftSkuinfo plusModelGiftSkuinfo : giftSkuinfos) {
					 String sysTime = DateUtil.getSysDateTimeString();
					 //判断在有效期内
					 if (plusModelGiftSkuinfo.getFr_date().compareTo(sysTime) <= 0
							 && plusModelGiftSkuinfo.getEnd_date().compareTo(sysTime) >= 0) {
						
						 List<PlusModelMediMclassGift> medi_mclssList = plusModelGiftSkuinfo.getMedi_mclss_nm();
						 
						 boolean flag = false;
						 // 2:网站；34:APP通路；39:扫码购；42:微信商城
						 for (PlusModelMediMclassGift plusModelMediMclassGift : medi_mclssList) {
							 if (flagSMG) {
								//扫码购渠道，如果是扫码购，则其他通路不需要判断
								if ("39".equals(plusModelMediMclassGift.getMEDI_MCLSS_ID())) {
									flag = true;
									break;
								}
								continue;
							}
							 //APP通路
							 if ("449747430001".equals(channelId) && "34".equals(plusModelMediMclassGift.getMEDI_MCLSS_ID())) {
								 flag = true;
									break;
							 }
							//网站渠道
							 if (("449747430002".equals(channelId) || "449747430004".equals(channelId)) 
									 && "2".equals(plusModelMediMclassGift.getMEDI_MCLSS_ID())) {
								 flag = true;
									break;
							}
							 //微信商城
							 if ("449747430003".equals(channelId) && "42".equals(plusModelMediMclassGift.getMEDI_MCLSS_ID())) {
								 flag = true;
									break;
								}
						}
						 if (flag) {
								String giftName = plusModelGiftSkuinfo.getGood_nm();
								if (StringUtils.isNotBlank(outerGiftMap.get(productCode))) {
									giftName = outerGiftMap.get(productCode) + giftName;
								}
								outerGiftMap.put(productCode,giftName+";");
						}
					}
				}
			}
		}
		for (String productCode : productCodeArr) {
			if (PlusHelperEvent.checkEventItem(productCode)) {
				PlusModelEventItemProduct eventItemtInfo = plusSupportEvent.upItemProductByIcCode(productCode);
				if (null != eventItemtInfo) {
					productCode = eventItemtInfo.getProductCode();
				}
			}
			String innerGiftName = innerGiftMap.get(productCode);
			String outerGiftName = outerGiftMap.get(productCode);
			String giftName = "";
			if (StringUtils.isNotEmpty(innerGiftName)) {
				giftName += innerGiftName+";";
				
			}
			
//			XmasKv.upFactory(EKvSchema.Product).del(productCode);
//			String aa = XmasKv.upFactory(EKvSchema.Product).get(productCode);
//			JSONObject pobj = JSONObject.parseObject(XmasKv.upFactory(EKvSchema.Product).get(productCode));
//			System.out.println(aa); 
			
			// 根据product code 在缓存中取商品信息  
			PlusModelProductInfo plusModelProductinfo = new PlusModelProductInfo();
			//加上tryCatch可以防止首页接口因为商品缓存报错
			try {
				plusModelProductinfo = new LoadProductInfo().upInfoByCode(new PlusModelProductQuery(productCode));
			} catch (Exception e) {
				XmasKv.upFactory(EKvSchema.Product).del(productCode);
				plusModelProductinfo = new LoadProductInfo().upInfoByCode(new PlusModelProductQuery(productCode));
			}
			Boolean lcflag = true;
			 // 抄底价商品 449747110001:否，449747110002:是  
			if(StringUtils.isNotBlank(plusModelProductinfo.getLowGood()) && plusModelProductinfo.getLowGood().equals("449747110002")){
				lcflag = false;
			} 
			
			if (lcflag && StringUtils.isNotEmpty(outerGiftName)) {   // 抄底价商品，不展示赠品信息 - Yangcl 
				giftName += outerGiftName;
			}
			resultMap.put(productCode, giftName);
		}
		return resultMap;
	}
	/**
	 * 根据skuCode查询商品的赠品（仅支持惠家有调用）
	 * @param skuCodes 格式商品编号中间用逗号隔开：code,code,code,...
	 * @return
	 * @author ligj
	 */
	public Map<String,String> getSkuGifts(String skuCodes){
		//key为skuCode,value为赠品
		Map<String,String> resultMap = new HashMap<String, String>();
		if (StringUtils.isEmpty(skuCodes)) {
			return resultMap;
		}
		//内联赠品
		Map<String,String> innerGiftMap = new HashMap<String, String>();
		//外联赠品
		Map<String,String> outerGiftMap = new HashMap<String, String>();
		String gift = "内联赠品";
		
		String productWhere = "sku_code in ('"+skuCodes.replace(",", "','")+"')";
		List<MDataMap> skuInfoMap = DbUp.upTable("pc_skuinfo").queryAll("sku_code,product_code","",productWhere,null);
		
		Map<String,String> skuProductMap = new HashMap<String, String>();		//key:skuCode,value:productCode
		Map<String,Integer> productCodeMap = new HashMap<String, Integer>();	//productCode 去重用
		for (MDataMap mDataMap : skuInfoMap) {
			skuProductMap.put(mDataMap.get("sku_code"), mDataMap.get("product_code"));
			productCodeMap.put(mDataMap.get("product_code"), 1);
		}
		String productCodes = "";
		for (String productCode : productCodeMap.keySet()) {
			productCodes += (productCode+",");
		}
		String[] productCodeArr = productCodes.substring(0, productCodes.length()-1).split(",");
		
		//自定义属性列表
		String propertiesWhere = "product_code in ('"+productCodes.substring(0, productCodes.length()-1).replace(",", "','")+"') and property_type='449736200004' ";
		List<MDataMap> productPropertiesMap = DbUp.upTable("pc_productproperty").queryAll("product_code,property_key,property_value,start_date,end_date","",propertiesWhere,null);
		for (MDataMap mDataMap : productPropertiesMap) {
			if (gift.equals(mDataMap.get("property_key"))) {
				String startDate = mDataMap.get("start_date");
				String endDate = mDataMap.get("end_date");
				if(StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)){
					SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String currDate=sdf.format(new Date());
					if(startDate.compareTo(currDate) < 0 && currDate.compareTo(endDate) < 0){
						innerGiftMap.put(mDataMap.get("product_code"), mDataMap.get("property_value"));
					}else{
						innerGiftMap.put(mDataMap.get("product_code"), "");
					}
				}
			}
		}
		//获取外联赠品的输入参数
		List<MDataMap> giftsMapList =  DbUp.upTable("pc_product_gifts_new").queryAll("product_code,gift_name", "", 
				"seller_code='SI2003' and product_code in ('"+productCodes.replace(",", "','")+"')", null);
		if (null!=giftsMapList) {
			for (MDataMap giftMap : giftsMapList) {
				String productCode = giftMap.get("product_code");
				String giftName = giftMap.get("gift_name");
				if (StringUtils.isNotBlank(outerGiftMap.get(productCode))) {
					giftName = outerGiftMap.get(productCode) + giftName;
				}
				outerGiftMap.put(productCode,giftName+";");
			}
		}
		//product与赠品
		Map<String,String> productGiftNametMap = new HashMap<String, String>();
		for (String productCode : productCodeArr) {
			String innerGiftName = innerGiftMap.get(productCode);
			String outerGiftName = outerGiftMap.get(productCode);
			String giftName = "";
			if (StringUtils.isNotEmpty(innerGiftName)) {
				giftName += innerGiftName+";";
				
			}
			if (StringUtils.isNotEmpty(outerGiftName)) {
				giftName += outerGiftName;
			}
			productGiftNametMap.put(productCode, giftName);
		}
		for (String skuCode : skuProductMap.keySet()) {
			String productCode = skuProductMap.get(skuCode);
			String giftName = productGiftNametMap.get(productCode);
			resultMap.put(skuCode, giftName);
		}
		return resultMap;
	}
	/**
	 * 根据productCode查询商品的赠品详细信息（仅支持惠家有调用）
	 * @param productCode 商品编码
	 * @return
	 * @author ligj
	 */
	public List<ModelGoodGiftInfo> getProductGiftsDetailList(String productCode){
		
		List<ModelGoodGiftInfo> giftList = new ArrayList<ModelGoodGiftInfo>();
		if (StringUtils.isBlank(productCode)) {
			return giftList;
		}
		//获取外联赠品的输入参数
		List<MDataMap> giftsMapList =  DbUp.upTable("pc_product_gifts_new").queryAll("", "", 
				"seller_code='SI2003' and product_code = '"+productCode+"'", null);
		if (null!=giftsMapList) {
			for (MDataMap giftMap : giftsMapList) {
				ModelGoodGiftInfo giftModel = new ModelGoodGiftInfo();
				giftModel.setGood_id(giftMap.get("gift_id"));
				giftModel.setGood_nm(giftMap.get("gift_name"));
				giftModel.setStyle_id(giftMap.get("style_id"));
				giftModel.setColor_id(giftMap.get("color_id"));
				giftModel.setEvent_id(giftMap.get("event_id"));
				giftModel.setGift_cd(giftMap.get("gift_cd"));
				giftList.add(giftModel);
			}
		}
		return giftList;
	}
	/**
	 *根据hashmap的value进行从大到小排序 
	 * 
	 */
	public List<Map.Entry<String, Double>> mapValueSort(Map<String, Double> ms) {
        List<Map.Entry<String, Double>> list_Data = new ArrayList<Map.Entry<String, Double>>(ms.entrySet()); 
        Collections.sort(list_Data, new Comparator<Map.Entry<String, Double>>()  
          {   
              public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2)  
              {  
               if(o2.getValue()!=null&&o1.getValue()!=null&&o2.getValue().compareTo(o1.getValue())>0){  
                return 1;  
               }else{  
                return -1;  
               }  
                  
              }  
          });
        
        return list_Data;
	}
	
	/**
	 *根据hashmap的value进行从小到大排序 
	 * 
	 */
	public List<Map.Entry<String, Double>> mapValueSortDesc(Map<String, Double> ms) {
        List<Map.Entry<String, Double>> list_Data = new ArrayList<Map.Entry<String, Double>>(ms.entrySet()); 
        Collections.sort(list_Data, new Comparator<Map.Entry<String, Double>>()  
          {   
              public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2)  
              {  
               if(o2.getValue()!=null&&o1.getValue()!=null&&o2.getValue().compareTo(o1.getValue())<0){  
                return 1;  
               }else{  
                return -1;  
               }  
                  
              }  
          });
        
        return list_Data;
	}
	
	private List<Order> freightFreeForDog(List<Order> orders){
		if(orders!=null&&!orders.isEmpty()){
			BigDecimal duemoney = new BigDecimal(0.00);
			for (Order order : orders) {
				duemoney = duemoney.add(order.getDueMoney());
			}
			String orderCode = "";
			if(duemoney.compareTo(BigDecimal.valueOf(Double.valueOf(bConfig("familyhas.full_amount"))))<0){
				
				for (int i = 0; i < orders.size(); i++) {
					Order or = orders.get(i);
					if(or.getTransportMoney()!=null&&or.getTransportMoney().compareTo(new BigDecimal(0.00))>0){
						orders.get(i).setDueMoney(or.getDueMoney().add(or.getTransportMoney()));
						orderCode = or.getOrderCode();
						break;
					}
				}
				for (int j = 0; j < orders.size(); j++) {
					Order or = orders.get(j);
					if(StringUtils.isNotBlank(orderCode)&&!orderCode.equals(or.getOrderCode())){
						orders.get(j).setTransportMoney(new BigDecimal(0.00));
					}
				}
			}else {
				for (int j = 0; j < orders.size(); j++) {
					
					orders.get(j).setTransportMoney(new BigDecimal(0.00));
				}
			}
		}
		return orders;
	}
}
