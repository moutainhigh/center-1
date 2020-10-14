package com.cmall.groupcenter.service;



import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.active.ActiveConst;
import com.cmall.groupcenter.active.ActiveControllerForProduct;
import com.cmall.groupcenter.active.BaseActive;
import com.cmall.groupcenter.active.product.ConditionCheck;
import com.cmall.groupcenter.active.product.SkuInfoForRet;
import com.cmall.groupcenter.baidupush.core.utility.DismantlOrderUtil;
import com.cmall.groupcenter.homehas.GetGoodGiftList;
import com.cmall.groupcenter.homehas.model.ModelGoodGiftInfo;
import com.cmall.groupcenter.model.BillInfoForJYH;
import com.cmall.groupcenter.model.SkuInfoForShopCart;
import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.common.OrderConst;
import com.cmall.ordercenter.model.AddressInformation;
import com.cmall.ordercenter.model.OcOrderActivity;
import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.OrderAddress;
import com.cmall.ordercenter.model.OrderDetail;
import com.cmall.ordercenter.service.AddressService;
import com.cmall.ordercenter.service.NcStaffAddressService;
import com.cmall.ordercenter.service.OrderService;
import com.cmall.productcenter.model.PcProductinfo;
import com.cmall.productcenter.service.ProductService;
import com.cmall.productcenter.service.ProductStoreService;
import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.service.StoreService;
import com.cmall.systemcenter.util.StringUtility;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class OrderShoppingService extends BaseClass {
	/**
	 *根据订单号删除本订单对应的购物车中的商品 
	 */
	public boolean deleteSkuToShopCart(String orderCode){
		boolean flag = true;
		try {
			MDataMap updateMap = new MDataMap();
			updateMap.put("order_code", orderCode);
			updateMap.put("order_status", "4497153900010002");
			updateMap.put("delete_flag", "0");
			DbUp.upTable("oc_orderinfo").dataUpdate(updateMap, "order_status,delete_flag", "order_code");
			
			com.cmall.ordercenter.service.OrderService service = new com.cmall.ordercenter.service.OrderService();
			Order order = service.getOrder(orderCode);
			List<OrderDetail> productList = order.getProductList();
			String buyerCode = order.getBuyerCode();
			if(productList!=null&&!productList.isEmpty()){
				for (int i = 0; i < productList.size(); i++) {
					OrderDetail de = productList.get(i);
					String skuCode = de.getSkuCode();
					DbUp.upTable("oc_shopCart").delete("buyer_code",buyerCode,"sku_code",skuCode);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}
	
	/**
	 *根据订单号删除本订单对应的购物车中的商品 
	 */
	public boolean deleteSkuToShopCartForNotPay(String orderCode){
		boolean flag = true;
		try {
			MDataMap updateMap = new MDataMap();
			updateMap.put("order_code", orderCode);
			updateMap.put("order_status", "4497153900010001");
			updateMap.put("delete_flag", "0");
			DbUp.upTable("oc_orderinfo").dataUpdate(updateMap, "order_status,delete_flag", "order_code");
			
			com.cmall.ordercenter.service.OrderService service = new com.cmall.ordercenter.service.OrderService();
			Order order = service.getOrder(orderCode);
			List<OrderDetail> productList = order.getProductList();
			String buyerCode = order.getBuyerCode();
			if(productList!=null&&!productList.isEmpty()){
				for (int i = 0; i < productList.size(); i++) {
					OrderDetail de = productList.get(i);
					String skuCode = de.getSkuCode();
					DbUp.upTable("oc_shopCart").delete("buyer_code",buyerCode,"sku_code",skuCode);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}
	
	/**
	 * 添加商品进入购物车
	 * 
	 */
	public RootResultWeb addSkuToShopCart(Map<String, Integer> map,String buyCode) {
		RootResultWeb rootResult = new RootResultWeb();
		try {
			if (map != null && !map.isEmpty()) {
				//取出购物车所有商品
				List<MDataMap> ll = DbUp.upTable("oc_shopCart").queryByWhere("buyer_code", buyCode);
				Map<String, MDataMap> mm = new HashMap<String, MDataMap>();
				if(ll!=null&&!ll.isEmpty()){
					Iterator<MDataMap> iterator = ll.iterator();
					while (iterator.hasNext()) {
						MDataMap mDataMap = (MDataMap) iterator.next();
						mm.put(mDataMap.get("sku_code"), mDataMap);
					}
				}
				Iterator<String> iterator = map.keySet().iterator();
				while (iterator.hasNext()) {
					String skuCode = iterator.next().toString();
					Integer skuNum = map.get(skuCode);
					// 判断购物车中是否存在本商品
					MDataMap insert = new MDataMap();
					insert.put("sku_num", String.valueOf(skuNum));
					insert.put("buyer_code", buyCode);
					insert.put("sku_code", skuCode);
					MDataMap one = mm.get(skuCode);
					int skuMax = Integer.valueOf(bConfig("homepool.skuMaxNum"));
					if (one == null && skuNum > 0) {// 不存在本商品的话新增
						skuNum= skuNum>skuMax?skuMax:skuNum;
						insert.put("sku_num", String.valueOf(skuNum));
						insert.put("account_flag",String.valueOf(0));
						insert.put("add_time",DateUtil.getSysDateTimeString());
						insert.put("create_time",DateUtil.getSysDateTimeString());
						insert.put("update_time",DateUtil.getSysDateTimeString());
						DbUp.upTable("oc_shopCart").dataInsert(insert);
					} else if (one != null && (skuNum > 0||skuNum<0)) {// 已存在本商品的话更新本商品
						insert.put("account_flag", one.get("account_flag"));
						skuNum= (skuNum+Integer.valueOf(one.get("sku_num")))>skuMax?skuMax:(skuNum+Integer.valueOf(one.get("sku_num")));
						insert.put("sku_num",String.valueOf(skuNum));
						insert.put("update_time",DateUtil.getSysDateTimeString());
						DbUp.upTable("oc_shopCart").dataUpdate(insert, "","buyer_code,sku_code");
					} else if (skuNum == 0 && one != null) {
						deleteSkuForShopCart(buyCode,skuCode);// 删除的商品
					}
				}
			}
			//商品种类超量和单品超量的按逻辑给与删除(超过限定数量之和删除最早加入购物车的商品)
			int skuCountMax = Integer.valueOf(bConfig("homepool.skuMaxCount"));
			MDataMap countMap = DbUp.upTable("oc_shopCart").oneWhere("count(sku_code) as sCount","","","buyer_code",buyCode);
			if(Integer.valueOf(countMap.get("sCount"))>skuCountMax){
				int ds = Integer.valueOf(countMap.get("sCount"))-skuCountMax;
				String sql = "delete from oc_shopCart where buyer_code='"+
						buyCode+"' and sku_code in ( SELECT sku_code from oc_shopCart where buyer_code='"+
						buyCode+"' ORDER BY update_time LIMIT 0,"+String.valueOf(ds)+")";
				DbUp.upTable("oc_shopCart").dataExec(sql, new MDataMap());
			}
		} catch (Exception e) {
			e.printStackTrace();
			rootResult.setResultCode(916401102);
			rootResult.setResultMessage(bInfo(916401102));
		}
		return rootResult;
	}
	/**
	 * 删除购物车中的商品
	 * 
	 */
	public boolean deleteSkuForShopCart(String buyer_code, String skuCode) {
		boolean flag = false;
		try {
			if (buyer_code != null && skuCode != null && !"".equals(skuCode)
					&& !"".equals(buyer_code)) {
				DbUp.upTable("oc_shopCart").delete("buyer_code", buyer_code,
						"sku_code", skuCode);
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 获取用户购物车中的所有商品
	 * 
	 */
	public Map<String, Object> getSkuShopCart(String sellerCode,String buyerCode) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<SkuInfoForShopCart> list = new ArrayList<SkuInfoForShopCart>();
		BigDecimal accountMoney = new BigDecimal(0.00);
		List<MDataMap> querys = DbUp.upTable("oc_shopCart").queryAll("","create_time DESC", "", new MDataMap("buyer_code", buyerCode));
		if (querys != null && !querys.isEmpty()) {
			Map<String, Integer> skuNums = new HashMap<String, Integer>();
			for (int i = 0; i < querys.size(); i++) {
				skuNums.put(querys.get(i).get("sku_code"), Integer.valueOf(querys.get(i).get("sku_num")));
			}
			List<SkuInfoForRet> activitys = new ActiveControllerForProduct(sellerCode).activeGallery(skuNums,new MDataMap(ActiveConst.ACTIVE_PARAMS_BUYERCODE,buyerCode,ActiveConst.ACTIVE_PARAMS_ORDER_SHOPCAR,"true"), new RootResultWeb(),false);
			for (int i = 0; i < activitys.size(); i++) {
				SkuInfoForRet fr = activitys.get(i);
				String skuCode = fr.getSkuCode();
				Integer skuNum = skuNums.get(skuCode);
				SkuInfoForShopCart gifq = new SkuInfoForShopCart();
				ProductService service = new ProductService();
				PcProductinfo product = service.getskuinfo(skuCode,"");
				if (product == null) {
					deleteSkuForShopCart(buyerCode,skuCode);// 删除已不存在的商品
					continue;
				}
				// 判断是否下架
				String productStatus = service.getProduct(product.getProductCode()).getProductStatus();
				if (productStatus == null|| !"4497153900060002".equals(productStatus)) {// 已下架
					gifq.setFlagSell("0");
					deleteSkuForShopCart(buyerCode,skuCode);// 删除 下架的商品
					continue;
				}else {
					gifq.setFlagSell("1");
				}
				//判断数量
				if(skuNum>fr.getPurchase_limit_order_num()){
					skuNum=fr.getPurchase_limit_order_num();
					DbUp.upTable("oc_shopCart").dataUpdate(new MDataMap("buyer_code",buyerCode,"sku_code",skuCode,"sku_num",String.valueOf(skuNum)), "sku_num","buyer_code,sku_code");
				}
				// 判断库存是否足够
				int maxStock = (new StoreService()).getStockNumByStore(skuCode);
				
				if(VersionHelper.checkServerVersion("9.5.11.31")&&(sellerCode.equals(MemberConst.MANAGE_CODE_HOMEHAS)||sellerCode.equals(MemberConst.MANAGE_CODE_HPOOL))){//后台逻辑代码版本控制
					maxStock = (new ProductStoreService()).getStockNumBySku(skuCode);
				}
				
				if (Integer.valueOf(skuNum) > maxStock) {
					gifq.setFlagStock("0");
				}else {
					gifq.setFlagStock("1");
				}
				gifq.setProductCode(product.getProductCode());
				gifq.setSkuPic(product.getMainPicUrl());
				gifq.setSkuCode(skuCode);
				gifq.setSkuName(product.getProductSkuInfoList().get(0).getSkuName());
				gifq.setSkuNum(skuNum);
				gifq.setSkuKey(product.getProductSkuInfoList().get(0).getSkuKey());
				gifq.setSkuValue(product.getProductSkuInfoList().get(0).getSkuValue());
				
				Map<String, Integer> skuMap=new HashMap<String, Integer>();
				skuMap.put(gifq.getSkuCode(), gifq.getSkuNum());
				
				gifq.setActivityForShopCarts(fr.getActiveList());
				gifq.setSkuPrice(fr.getTransactionPrice().doubleValue());
				accountMoney = accountMoney.add(fr.getTransactionPrice().multiply(new BigDecimal(gifq.getSkuNum())));
				list.add(gifq);
			}
		}
		result.put("account", accountMoney.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		result.put("list", list);
		return result;
	}
	
	/**
	 * 获取购物车内所有商品
	 */
	public Map<String,Integer> getShopCart(String buyerCode){
		Map<String,Integer> map = new HashMap<String, Integer>();
		try {
			List<MDataMap> skuLi = DbUp.upTable("oc_shopCart").queryAll("","create_time DESC", "", new MDataMap("buyer_code", buyerCode));
			if(skuLi!=null&&!skuLi.isEmpty()){
				for (int i = 0; i < skuLi.size(); i++) {
					MDataMap mDataMap = skuLi.get(i);
					map.put(mDataMap.get("sku_code").toString(), Integer.valueOf(mDataMap.get("sku_num").toString()));
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 确认订单判断库存 和是否下架
	 */
	public RootResult checkGoodsStock(String sellerCode,String buyerCode,
			Map<String, Integer> map) {
		RootResult rootResult = new RootResult();
		String error = "";
		if (map != null && !map.isEmpty()) {
			Iterator<String> iterator = map.keySet().iterator();
			while(iterator.hasNext()){
				String skuCode = iterator.next();
				int skuNum = map.get(skuCode);
				ProductService service = new ProductService();
				// 判断此商品是否存在
				PcProductinfo product = service.getskuinfo(skuCode, "");
				if (product == null) {
					deleteSkuForShopCart(buyerCode, skuCode);// 删除已不存在的商品
					continue;
				}
				// 判断是否下架
				String productStatus = product.getProductStatus();
				if (productStatus == null|| !"4497153900060002".equals(productStatus)) {// 已下架
					error = product.getProductSkuInfoList().get(0).getSkuName();
					break;
				}
				// 判断本地区所在库是否存在本商品的库存或者存在库存是否不足 如果是闪购商品提示删除
				int maxStock = (new StoreService()).getStockNumByMax(skuCode);
				
				if(VersionHelper.checkServerVersion("9.5.11.31")&&(sellerCode.equals(MemberConst.MANAGE_CODE_HOMEHAS)||sellerCode.equals(MemberConst.MANAGE_CODE_HPOOL))){//后台逻辑代码版本控制
					maxStock = (new ProductStoreService()).getStockNumBySku(skuCode);
				}
				
				if (maxStock < skuNum) {
					error = product.getProductSkuInfoList().get(0).getSkuName();
					break;
				}
			}
		}
		if (!"".equals(error.toString())) {
			rootResult.setResultCode(922401119);
			rootResult.setResultMessage(bInfo(922401119, "[" + error + "]"));
		}
		return rootResult;
	}

	public Map<String, Object> confirmOrder(Map<String, Integer> map,MDataMap other,OrderAddress address, List<String> couponCodes){
		Map<String, Object> result = new HashMap<String, Object>();
		List<SkuInfoForShopCart> confirms = new ArrayList<SkuInfoForShopCart>();
		BigDecimal payMoney = new BigDecimal(0.00);// 应付款
		BigDecimal costMoney = new BigDecimal(0.00);// 商品总金额
		BigDecimal sentMoney = new BigDecimal(0.00);// 运费
		String payType = "";// 库存地支持的支付方式
		//拆单****************************拆单
		if(VersionHelper.checkServerVersion("9.5.11.31")&&other.get("sellerCode").equals(MemberConst.MANAGE_CODE_HPOOL)){//后台逻辑代码版本控制
			other.put("orderSouce", "449715190005");
			RootResultWeb resultWeb = new RootResultWeb();
			List<Order> orders = (new DismantlOrderUtil()).DismantlOrder(map, address, other, resultWeb, couponCodes);
			Double czj = 0.00;
			Double zck = 0.00;
			Double jf = 0.00;
			for (int i = 0; i < orders.size(); i++) {
				Order order = orders.get(i);
				sentMoney =sentMoney.add(order.getTransportMoney());
				payMoney=payMoney.add(order.getDueMoney());
				costMoney = costMoney.add(order.getOrderMoney());
				payType=order.getPayType();
				for (int j = 0; j < order.getProductList().size(); j++) {//商品
					OrderDetail detail = order.getProductList().get(j);
					if("1".equals(detail.getGiftFlag())){//暂时不返回赠品
						SkuInfoForShopCart confirm = new SkuInfoForShopCart();
						confirm.setSkuCode(detail.getSkuCode());
						confirm.setSkuName(detail.getSkuName());
						confirm.setSkuPic(detail.getProductPicUrl());
						confirm.setSkuNum(detail.getSkuNum());
						confirm.setProductCode(detail.getProductCode());
						confirm.setSkuKey(detail.getSkuKey());
						confirm.setSkuValue(detail.getSkuValue());
						confirm.setSkuPrice(detail.getSkuPrice().doubleValue());
						for (int k = 0; k < order.getActivityList().size(); k++) {
							OcOrderActivity ocOa = order.getActivityList().get(k);
							if(detail.getSkuCode().equals(ocOa.getSkuCode())){
								BaseActive active = new BaseActive();
								active.setActivePrice(new BigDecimal(ocOa.getPreferentialMoney()));
								active.setActivity_code(ocOa.getActivityCode());
								active.setActivity_title(ocOa.getActivityName());
								active.setActivity_type_code(ocOa.getActivityType());
								active.setApp_code(other.get("sellerCode"));
								active.setCreate_time(order.getCreateTime());
								active.setCreate_user(other.get("buyerCode"));
								active.setOuter_activity_code(ocOa.getOutActiveCode());
								confirm.getActivityForShopCarts().add(active);
							}
						}
						confirms.add(confirm);
					}
				}
				if(order.getOcOrderPayList()!=null&&!order.getOcOrderPayList().isEmpty()){
					for (int j = 0; j < order.getOcOrderPayList().size(); j++) {
						String pt = order.getOcOrderPayList().get(j).getPayType();
						Double ptm = (double)order.getOcOrderPayList().get(j).getPayedMoney();
						if("449746280006".equals(pt)){
							czj+=ptm;
						}else if ("449746280007".equals(pt)) {
							zck+=ptm;
						}else if ("449746280008".equals(pt)) {
							jf+=ptm;
						}
					}
				}
			}
			result.put("czj_m", czj);
			result.put("zck_m", zck);
			result.put("jf_m", jf);
			result.put("orders", orders);
			if(!resultWeb.upFlagTrue()){
				result.put("resultCode", resultWeb.getResultCode());
				result.put("resultMessage", resultWeb.getResultMessage());
			}
		//拆单****************************拆单
		}else {
			if (map != null && !map.isEmpty()) {
				RootResultWeb resultWeb = new RootResultWeb();
				resultWeb = this.checkLimitBuyEmplorer(other.get("buyerCode"), map);
				if(!resultWeb.upFlagTrue()){
					result.put("resultCode", resultWeb.getResultCode());
					result.put("resultMessage", resultWeb.getResultMessage());
					return result;
				}
				List<SkuInfoForRet> skuActivityList = new ActiveControllerForProduct(other.get("sellerCode")).activeGallery(map,new MDataMap(ActiveConst.ACTIVE_PARAMS_BUYERADDRESS,other.get("vip_address_id"),ActiveConst.ACTIVE_PARAMS_BUYERCODE,other.get("buyerCode")), resultWeb);
				if(!resultWeb.upFlagTrue()){
					result.put("resultCode", resultWeb.getResultCode());
					result.put("resultMessage", resultWeb.getResultMessage());
					return result;
				}else {
					for (int i = 0; i < skuActivityList.size(); i++) {
						SkuInfoForRet forRet = skuActivityList.get(i);
						String skuCode = forRet.getSkuCode();
						int skuNum = map.get(skuCode);
						ProductService service = new ProductService();
						PcProductinfo productInfo = service.getskuinfo(skuCode, "");
						for (int j = 0; j < forRet.getActiveList().size(); j++) {
							if("AT140820100004".equals(forRet.getActiveList().get(j).getActivity_type_code())){
								payType = "449716200001";// 在线支付
								break;
							}
						}
						if("".equals(payType)){
							payType = "449716200002";// 货到付款
						}
						SkuInfoForShopCart confirm = new SkuInfoForShopCart();
						confirm.setSkuCode(skuCode);
						confirm.setSkuName(productInfo.getProductSkuInfoList().get(0).getSkuName());
						confirm.setSkuPic(productInfo.getMainPicUrl());
						confirm.setSkuNum(skuNum);
						confirm.setProductCode(productInfo.getProductCode());
						confirm.setSkuKey(productInfo.getProductSkuInfoList().get(0).getSkuKey());
						confirm.setSkuValue(productInfo.getProductSkuInfoList().get(0).getSkuValue());
						confirm.setSkuPrice(forRet.getTransactionPrice().doubleValue());
						confirm.setActivityForShopCarts(forRet.getActiveList());
						payMoney = payMoney.add(forRet.getTransactionPrice().multiply(new BigDecimal(skuNum)));
						costMoney = costMoney.add(new BigDecimal(confirm.getSkuPrice()).multiply(new BigDecimal(skuNum)));//商品总金额
						confirms.add(confirm);
					}
				}
			}
		}
		result.put("confirms", confirms);
		result.put("payMoney", payMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.put("costMoney", costMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.put("sentMoney", sentMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.put("payType", payType);
		return result;
	}
	
	public void updateAccountFlag(String buyerCode, String skuCode) {
		try {
			if (buyerCode != null && skuCode != null && !"".equals(skuCode)
					&& !"".equals(buyerCode)) {
				MDataMap map = new MDataMap();
				map.put("buyer_code", buyerCode);
				map.put("sku_code", skuCode);
				map.put("account_flag", "1");
				DbUp.upTable("oc_shopCart").dataUpdate(map, "account_flag",
						"buyer_code,sku_code");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 创建订单
	 */
	public Map<String, Object> createOrderForJYh(Map<String, Object> inputMap) {
		Map<String, Object> result = new HashMap<String, Object>();
		// 发货地址信息
		AddressInformation ad = new AddressInformation();
		if(inputMap.get("buyer_address_id")!=null&&!"".equals(inputMap.get("buyer_address_id").toString())){
			ad = (new AddressService()).getAddressOne(inputMap.get("buyer_address_id").toString(),inputMap.get("buyer_code").toString());
		}else {
			ad = (new NcStaffAddressService()).nvStaffAddressValue(inputMap.get("vip_address_id").toString());
			ad.setAddress_mobile(inputMap.get("vip_buyer_mobile").toString());
			ad.setAddress_name(inputMap.get("vip_buyer_name").toString());
		}
		OrderAddress address = new OrderAddress();
		address.setAddress(ad.getAddress_street());
		address.setPostCode(ad.getAddress_postalcode());
		address.setMobilephone(ad.getAddress_mobile());
		address.setAreaCode(ad.getArea_code());
		if(StringUtility.isNull(address.getAddress())||StringUtility.isNull(address.getAreaCode())
				||StringUtility.isNull(address.getMobilephone())){//地址三级编号、手机号、详细地址都不能为空
			result.put("resultCode", 915805220);
			result.put("resultMessage", bInfo(915805220));
			return result;
		}
		address.setInvoiceType(((BillInfoForJYH) inputMap.get("billInfo")).getBill_Type());
		address.setInvoiceTitle(((BillInfoForJYH) inputMap.get("billInfo")).getBill_title());
		address.setInvoiceContent(((BillInfoForJYH) inputMap.get("billInfo")).getBill_detail());
		if((address.getInvoiceContent()==null||"".equals(address.getInvoiceContent()))
				&&(address.getInvoiceTitle()==null||"".equals(address.getInvoiceTitle()))
				&&(address.getInvoiceType()==null||"".equals(address.getInvoiceType()))){
			address.setFlagInvoice("0");
		}else {
			address.setFlagInvoice("1");
		}
		address.setReceivePerson(ad.getAddress_name());
		// 判断是否能进行添加订单操作 先进行订单确认校验
		MDataMap othMap = new MDataMap();
		othMap.put("sellerCode",inputMap.get("seller_code").toString());
		othMap.put("payType",inputMap.get("pay_type").toString());
		othMap.put("buyerCode",inputMap.get("buyer_code").toString());
		othMap.put("orderType",inputMap.get("order_type").toString());
		othMap.put("vip_address_id", inputMap.get("vip_address_id").toString());
		othMap.put("czj", inputMap.get("czj").toString());//储值金
		othMap.put("zck", inputMap.get("zck").toString());//暂存款
		othMap.put("jf", inputMap.get("jf").toString());//积分
		Map<String, Object> checkMe = confirmOrder((Map<String, Integer>)inputMap.get("goods"),othMap,address,null);
		if (((BigDecimal) checkMe.get("payMoney")).doubleValue() != Double.valueOf(inputMap.get("check_pay_money").toString())) {
			result.put("check_pay_money_error","");
			return result;
		}
		if(checkMe.containsKey("resultCode")){
			result.put("resultCode", checkMe.get("resultCode"));
			result.put("resultMessage", checkMe.get("resultMessage"));
			return result;
		}
		String sellerCode = inputMap.get("seller_code").toString();
		if(VersionHelper.checkServerVersion("9.5.11.31")&&(sellerCode.equals(MemberConst.MANAGE_CODE_HOMEHAS)||sellerCode.equals(MemberConst.MANAGE_CODE_HPOOL))){//后台逻辑代码版本控制
			//拆单*************************************start****************************************拆单
			OrderService orderService = new OrderService();
			RootResultWeb rootResult = new RootResultWeb();
			Map<String, String> other = new HashMap<String, String>();
			other.put("buyerCode", inputMap.get("buyer_code").toString());
			other.put("orderType", inputMap.get("order_type").toString());
			other.put("orderSouce", inputMap.get("order_souce").toString());
			other.put("payType", inputMap.get("pay_type").toString());
			other.put("sellerCode", inputMap.get("seller_code").toString());
			List<Order> orders = (List<Order>)checkMe.get("orders");
			if(rootResult.upFlagTrue()){
				String bigOrderCode = orderService.AddOrderListForSupper(orders, rootResult);
				if(rootResult.upFlagTrue()){
					result.put("order_code", bigOrderCode);
				}
			}else {
				result.put("error", rootResult.getResultMessage());
			}
		//拆单*************************************end****************************************拆单
		}else {
			// 创建订单
			Order order = new Order();
			order.setOrderCode(WebHelper.upCode(OrderConst.OrderHead));
			address.setOrderCode(order.getOrderCode());
			order.setAddress(address);
			List<SkuInfoForShopCart> confirms = (List<SkuInfoForShopCart>) checkMe.get("confirms");
			List<OcOrderActivity> actiList = new ArrayList<OcOrderActivity>();
			order.setBuyerCode(inputMap.get("buyer_code").toString());
			order.setCreateTime(DateUtil.getSysDateTimeString());
			if ("449716200002".equals(order.getPayType())) {
				order.setOrderStatus("4497153900010002");
			} else {
				order.setOrderStatus("4497153900010001");
			}
			order.setOrderSource(inputMap.get("order_souce").toString());
			order.setOrderType(inputMap.get("order_type").toString());
			order.setSellerCode(inputMap.get("seller_code").toString());
			order.setPayType(inputMap.get("pay_type").toString());
			order.setOrderMoney((BigDecimal) checkMe.get("costMoney"));
			order.setDueMoney((BigDecimal) checkMe.get("payMoney"));
			List<OrderDetail> odList = new ArrayList<OrderDetail>();
			for (int i = 0; i < confirms.size(); i++) {
				SkuInfoForShopCart firm = confirms.get(i);
				OrderDetail od = new OrderDetail();
				od.setSkuCode(firm.getSkuCode());
				od.setProductCode(firm.getProductCode());
				od.setOrderCode(order.getOrderCode());
				od.setProductPicUrl(firm.getSkuPic());
				od.setSkuName(firm.getSkuName());
				od.setSkuNum(firm.getSkuNum());
				od.setSkuPrice(new BigDecimal(firm.getSkuPrice()));
				od.setStoreCode(address.getAreaCode());
				od.setGiftFlag("1");
				odList.add(od);
	
				//商品参与的活动
				if(firm.getActivityForShopCarts()!=null&&!firm.getActivityForShopCarts().isEmpty()){
					for (int z = 0; z < firm.getActivityForShopCarts().size(); z++) {
						BaseActive ba = firm.getActivityForShopCarts().get(z);
						OcOrderActivity ga = new OcOrderActivity();
						ga.setActivityCode(ba.getActivity_code());
						ga.setActivityName(ba.getActivity_title());
						ga.setActivityType(ba.getActivity_type_code());
						ga.setOrderCode(order.getOrderCode());
						ga.setPreferentialMoney(new BigDecimal(DbUp.upTable("pc_skuinfo").one("sku_code",firm.getSkuCode()).get("sell_price")).subtract(ba.getActivePrice()).floatValue());
						ga.setProductCode(firm.getProductCode());
						ga.setSkuCode(firm.getSkuCode());
						ga.setOutActiveCode(ba.getOuter_activity_code());
						actiList.add(ga);
					}
				}
				// 获取赠品数据
				/*StoreService storeService = new StoreService();
				List<String> stores = storeService.getStores(address.getAreaCode());*/
				GetGoodGiftList ggft = new GetGoodGiftList();
				List<ModelGoodGiftInfo> gifts = ggft.doRsync(firm.getProductCode(), address.getAreaCode());
				if (gifts != null && !gifts.isEmpty()) {
					for (int j = 0; j < gifts.size(); j++) {
						ModelGoodGiftInfo mlgi = gifts.get(j);
	
						OrderDetail odG = new OrderDetail();
						odG.setSkuCode(mlgi.getGood_id());
						odG.setOrderCode(order.getOrderCode());
						odG.setSkuName(mlgi.getGood_nm());
						odG.setSkuNum(1);
						odG.setSkuPrice(new BigDecimal(0));
//						odG.setStoreCode(stores.get(0));
						odG.setGiftFlag("0");
//						odG.setStoreCode(stores.get(0).toString());
						odList.add(odG);
					}
				}
			}
			order.setProductList(odList);
			order.setActivityList(actiList);
	
			OrderService ser = new OrderService();
			List<Order> orderLists = new ArrayList<Order>();
			orderLists.add(order);
			StringBuffer error = new StringBuffer();
			if (AppConst.MANAGE_CODE_CAPP.equals(inputMap.get("seller_code")
					.toString())) {
				ser.AddOrderListTx(orderLists, error, AppConst.CAPP_STORE_CODE);
			} else {
				ser.AddOrderListTx(orderLists, error,ad.getArea_code());
			}
			if (error != null && !"".equals(order.getOrderCode())) {
				result.put("error", error);
			}
			result.put("order_code", order.getOrderCode());
		}
		return result;
	}
	/**
	 * 确认订单判断库存 和是否下架
	 */
	public Map<String, Object> checkGoodsStockAndStatus(String sellerCode,String buyerCode,Map<String, Integer> map) {
		Map<String, Object> result  = new HashMap<String, Object>();
		List<MDataMap> list = new ArrayList<MDataMap>();
		RootResultWeb resultWeb = new RootResultWeb();
		List<SkuInfoForRet> activitys = new ActiveControllerForProduct(sellerCode).activeGallery(map,new MDataMap(ActiveConst.ACTIVE_PARAMS_BUYERCODE,buyerCode==null?"":buyerCode), resultWeb);
		if(!resultWeb.upFlagTrue()){
			result.put("error", resultWeb);
			return result;
		}
		Map<String,Map<String,Integer>> ann = new HashMap<String, Map<String,Integer>>();//每个种类的活动对用的sku
		for (int i = 0; i < activitys.size(); i++) {
			List<BaseActive> lbs = activitys.get(i).getActiveList();
			String sc = activitys.get(i).getSkuCode();
			int sn = map.get(sc);
			for (int j = 0; j < lbs.size(); j++) {
				String tc = lbs.get(j).getActivity_type_code();
				if(tc!=null&&!"".equals(tc)){
					if(ann.containsKey(tc)){
						ann.get(tc).put(sc, sn);
					}else {
						Map<String,Integer> li = new HashMap<String,Integer>();
						li.put(sc,sn);
						ann.put(tc, li);
					}
				}
			}
		}
		if(ann.size()>1){
			resultWeb.setResultCode(918512006);
			resultWeb.setResultMessage(bInfo(918512006));
			result.put("error", resultWeb);
			return result;
		}
		if(ann.size()==1&&ann.containsKey("AT140820100004")){
			resultWeb = this.checkLimitBuyEmplorer(buyerCode, ann.get("AT140820100004"));
			if(!resultWeb.upFlagTrue()){
				result.put("error", resultWeb);
				return result;
			}
			result.put("flag", "1");
		}
		if (map != null && !map.isEmpty()) {
			
			for (int i = 0; i < activitys.size(); i++) {
				String skuCode = activitys.get(i).getSkuCode();
				MDataMap mDataMap = new MDataMap();
				mDataMap.put("skuCode", skuCode);
				int skuNum = map.get(skuCode);
				ProductService service = new ProductService();
				// 判断此商品是否存在
				PcProductinfo product = service.getskuinfo(skuCode, "");
				// 判断是否下架
				String productStatus = product.getProductStatus();
				if (productStatus == null|| !"4497153900060002".equals(productStatus)) {// 已下架
					mDataMap.put("flagSell", "0");
				}else {
					mDataMap.put("flagSell", "1");
				}
				// 判断本地区所在库是否存在本商品的库存或者存在库存是否不足 如果是闪购商品提示删除
				int maxStock = (new ProductStoreService()).getStockNumByMax(skuCode);
				
				if(VersionHelper.checkServerVersion("9.5.11.31")&&(sellerCode.equals(MemberConst.MANAGE_CODE_HOMEHAS)||sellerCode.equals(MemberConst.MANAGE_CODE_HPOOL))){//后台逻辑代码版本控制
					maxStock = (new ProductStoreService()).getStockNumBySku(skuCode);
				}
				
				if (maxStock < skuNum) {
					mDataMap.put("flagStock", "0");
				}else {
					mDataMap.put("flagStock", "1");
				}
				mDataMap.put("limitNum", String.valueOf(activitys.get(i).getPurchase_limit_order_num()));
				list.add(mDataMap);
			}
			result.put("list",list);
		}
		return result;
	}
	
	/**
	 *             请注意此方法已废弃，请联系ligj
	 * 内购会员的限购数,如果不是内购会员或传入的用户编号或商品编号为空时返回空字符串
	 * @param userCode			用户编号
	 * @param productCode		商品编号
	 * @return
	 */
	public RootResultWeb checkLimitBuyEmplorer(String userCode,Map<String,Integer> skuMap){
		RootResultWeb rootResult = new RootResultWeb();
		if (StringUtils.isEmpty(userCode) || null == skuMap || skuMap.isEmpty()
				|| !ConditionCheck.checkIsVipSpecialDay()
				|| !ConditionCheck.checkIsVipSpecial(userCode) ) {
			return rootResult;
		}
		int sameSkuForMonthLimit = Integer.parseInt(bConfig("groupcenter.sameSkuForMonthLimit"));		//用户每月同件sku内购限购数
		int allSkuForMonthLimit = Integer.parseInt(bConfig("groupcenter.allSkuForMonthLimit"));		//用户每月内购限购数
		
		//第一遍循环不查询订单表，找到不合规则的输入参数返回。
		int orderAllNum = 0;
		for (String skuCode : skuMap.keySet()) {
			if (skuMap.get(skuCode) > sameSkuForMonthLimit && skuMap.get(skuCode) < allSkuForMonthLimit) {
				//大于同件内购商品每会员每月限购数
				MDataMap skuInfo = DbUp.upTable("pc_skuinfo").oneWhere("sku_name", "", "", "sku_code",skuCode);
				rootResult.setResultCode(918512004);
				rootResult.setResultMessage(bInfo(918512004, (null == skuInfo ? "" : skuInfo.get("sku_name")),sameSkuForMonthLimit));
				return rootResult;
			}else if(skuMap.get(skuCode) > allSkuForMonthLimit){
				//大于内购商品每会员每月限购数
				rootResult.setResultCode(918512005);
				rootResult.setResultMessage(bInfo(918512005,allSkuForMonthLimit));
				return rootResult;
			}
			orderAllNum += skuMap.get(skuCode);
		}
		
		//第二遍循环前要先实时统计订单，此步比较耗费资源
//		String skuCodes = "";
//		for (String skuCode : skuMap.keySet()) {
//			skuCodes += (skuCode+",");
//		}
		StringBuffer censusSql = new StringBuffer();  
		censusSql.append(" select SUM(od.sku_num) as num,od.sku_code from oc_orderdetail od,oc_orderinfo oi,oc_order_activity oa ");
		censusSql.append(" where od.order_code = oi.order_code and oa.order_code=od.order_code and oi.buyer_code='"+userCode+"' ");
//		censusSql.append(" and od.sku_code = oa.sku_code and od.sku_code in ('"+skuCodes.substring(0, skuCodes.length()-1).replace(",", "','")+"') ");
		censusSql.append(" and od.sku_code = oa.sku_code and oi.order_status!='4497153900010006' and oi.delete_flag='0' ");
		censusSql.append(" and oa.activity_type='AT140820100004' and date_format(oi.create_time,'%Y-%m')=date_format(curdate(),'%Y-%m') "); 
		censusSql.append(" group by od.sku_code ");
		 List<Map<String, Object>> saleList=DbUp.upTable("oc_orderinfo").dataSqlList(censusSql.toString(),null);
		 //统计的内购商品本月本会员购买数
		 Map<String, Integer> saleMap = new  HashMap<String, Integer>();
		 int totalSalesNum = 0;		//此内购员工本月购买总商品数
		 for (Map<String, Object> map : saleList) {
			saleMap.put(String.valueOf(map.get("sku_code")),
					Integer.parseInt(String.valueOf((null == map.get("num"))?"0":map.get("num"))));
			totalSalesNum += Integer.parseInt(String.valueOf((null == map.get("num"))?"0":map.get("num")));
		}
		
		if ((orderAllNum + totalSalesNum) > allSkuForMonthLimit) {
			//大于内购商品每会员每月限购数
			rootResult.setResultCode(918512005);
			rootResult.setResultMessage(bInfo(918512005,allSkuForMonthLimit));
			return rootResult;
		}
		 
		for (String skuCode : skuMap.keySet()) {
			int buySameSkuCount = 0;
			if(!saleMap.isEmpty()&&saleMap.containsKey(skuCode)){
				buySameSkuCount = (skuMap.get(skuCode)+saleMap.get(skuCode));
			}else {
				buySameSkuCount = skuMap.get(skuCode);
			}
			if (buySameSkuCount > sameSkuForMonthLimit) {
				//大于同件内购商品每会员每月限购数
				MDataMap skuInfo = DbUp.upTable("pc_skuinfo").oneWhere("sku_name", "", "", "sku_code",skuCode);
				rootResult.setResultCode(918512004);
				rootResult.setResultMessage(bInfo(918512004, (null == skuInfo ? "" : skuInfo.get("sku_name")),sameSkuForMonthLimit));
				return rootResult;
			}
		}
		return rootResult;
	}
	/**
	 * 家有汇内购会员的每周限购数,如果不是内购会员或传入的用户编号或商品编号为空时返回空字符串
	 * @param userCode			用户编号
	 * @param productCode		商品编号
	 * @return
	 */
	public RootResultWeb checkLimitBuyEmplorerByWeek(String userCode,Map<String,Integer> skuMap){
		RootResultWeb rootResult = new RootResultWeb();
		if (StringUtils.isEmpty(userCode) || null == skuMap || skuMap.isEmpty()
				|| !ConditionCheck.checkIsVipSpecial(userCode) ) {
			return rootResult;
		}
		int allSkuForWeekLimit = Integer.parseInt(bConfig("groupcenter.allSkuForWeekLimit"));		//用户每周内购限购数
		
		//第一遍循环不查询订单表，找到不合规则的输入参数返回。
		int orderAllNum = 0;
		for (String skuCode : skuMap.keySet()) {
			orderAllNum += skuMap.get(skuCode);
			if(orderAllNum > allSkuForWeekLimit){
				//大于内购商品每会员每周限购数，提示语：每个内购商品每周最多购买{0}个
				rootResult.setResultCode(915805222);
				rootResult.setResultMessage(bInfo(915805222,allSkuForWeekLimit));
				return rootResult;
			}
		}
		
		//第二遍循环前要先实时统计订单，此步比较耗费资源
		//本周内购会员购买数，（周日--周六）
		StringBuffer censusSql = new StringBuffer();  
		censusSql.append(" select SUM(od.sku_num) as num from oc_orderdetail od,oc_orderinfo oi,oc_order_activity oa ");
		censusSql.append(" where od.order_code = oi.order_code and oa.order_code=od.order_code and oi.buyer_code='"+userCode+"' ");
		censusSql.append(" and od.sku_code = oa.sku_code and oi.order_status!='4497153900010006' and oi.delete_flag='0' ");
		censusSql.append(" and oa.activity_type='AT140820100004' and YEARWEEK(date_format(oi.create_time,'%Y-%m-%d'))=YEARWEEK(CURDATE()) "); 
		 Map<String, Object> saleMap=DbUp.upTable("oc_orderinfo").dataSqlOne(censusSql.toString(),null);
		 
		//此内购员工本周购买总商品数
		 int totalSalesNum = 0;
		 if (null != saleMap) {
			 totalSalesNum = Integer.parseInt(String.valueOf((null == saleMap.get("num"))?"0":saleMap.get("num")));
		}
		
		if ((orderAllNum + totalSalesNum) > allSkuForWeekLimit) {
			//大于内购商品每会员每月限购数
			rootResult.setResultCode(915805222);
			rootResult.setResultMessage(bInfo(915805222,allSkuForWeekLimit));
			return rootResult;
		}
		return rootResult;
	}
}
