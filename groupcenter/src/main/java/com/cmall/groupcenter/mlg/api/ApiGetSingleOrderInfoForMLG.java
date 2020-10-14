package com.cmall.groupcenter.mlg.api;



import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.mlg.model.ApiGetSingleOrderInfoForMLGInput;
import com.cmall.groupcenter.mlg.model.ApiGetSingleOrderInfoForMLGResult;
import com.cmall.groupcenter.mlg.model.OrderBaseInfo;
import com.cmall.groupcenter.mlg.model.OrderDetailInfo;
import com.cmall.productcenter.service.MyService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForMember;
/**
 * 根据订单号获取麦乐购订单信息
 * @author ligj
 *
 */
public class ApiGetSingleOrderInfoForMLG extends RootApiForMember<ApiGetSingleOrderInfoForMLGResult, ApiGetSingleOrderInfoForMLGInput>{

	public ApiGetSingleOrderInfoForMLGResult Process(ApiGetSingleOrderInfoForMLGInput inputParam, MDataMap mRequestMap) {
		ApiGetSingleOrderInfoForMLGResult result = new ApiGetSingleOrderInfoForMLGResult();
		String orderCode = inputParam.getOrder_id();
		
		MDataMap orderMap = DbUp.upTable("oc_orderinfo").oneWhere("order_code,due_money,update_time,create_time", "", "order_code=:order_code AND small_seller_code='SF03MLG' AND seller_code='SI2003' ", "order_code",orderCode);
		if (null == orderMap || orderMap.isEmpty()) {
			return result;
		}
		OrderBaseInfo orderBaseInfo = new OrderBaseInfo();
		double dueMoney = Double.parseDouble(orderMap.get("due_money"));
		String payTime = orderMap.get("update_time");
		String createTime = orderMap.get("create_time");
		
		orderBaseInfo.setOrder_id(orderCode);
		orderBaseInfo.setOrder_amount(dueMoney);				//订单总金额应该用sku成本价格去算
		orderBaseInfo.setPay_time(payTime);
		orderBaseInfo.setCtime(createTime);
		
		//地址信息
		MDataMap addressMap = DbUp.upTable("oc_orderadress").oneWhere("order_code,area_code,address,mobilephone,receive_person,auth_true_name,auth_idcard_number", "", "order_code=:order_code", "order_code",orderCode);
			
		orderBaseInfo.setAddress(addressMap.get("address"));		//详细地址
		orderBaseInfo.setRegion(addressMap.get("area_code"));		//所在地区
		orderBaseInfo.setConsignee(addressMap.get("receive_person"));	//收货人姓名
		orderBaseInfo.setPhone(addressMap.get("mobilephone"));		//手机号码
		orderBaseInfo.setName(addressMap.get("auth_true_name"));	//姓名
		orderBaseInfo.setIDNumber(addressMap.get("auth_idcard_number"));//证件号
		
		String areaCode = addressMap.get("area_code");
		if (StringUtils.isNotBlank(areaCode) && areaCode.length()==6) {
			MDataMap areaMap = new MyService().getAreaNameMap(areaCode);
			if (areaMap != null && !areaMap.isEmpty()) {
				orderBaseInfo.setProvince(areaMap.get(areaCode.substring(0, 2)+"0000"));
				orderBaseInfo.setCity(areaMap.get(areaCode.substring(0, 4)+"00"));
				orderBaseInfo.setArea(areaMap.get(areaCode));
			}
		}
		//商品信息
		BigDecimal orderAmount = BigDecimal.ZERO;		//计算出来的订单总金额
		List<OrderDetailInfo>  orderDetailList = new ArrayList<OrderDetailInfo>();
		List<MDataMap> orderDetailMapList = DbUp.upTable("oc_orderdetail").queryAll("order_code,sku_code,product_code,cost_price,sku_num", "", "order_code=:order_code", new MDataMap("order_code",orderCode));
		//获取商品编号，用来取得商品货号
		List<String> skuCodeArr = new ArrayList<String>();
		MDataMap sellProductCodeMap = new MDataMap();
		for (MDataMap mDataMap : orderDetailMapList) {
			skuCodeArr.add(mDataMap.get("sku_code"));
		}
		List<MDataMap> sellProductCodeMapList = DbUp.upTable("pc_skuinfo").queryAll("sku_code,product_code,sell_productcode", "", "sku_code in ('"+StringUtils.join(skuCodeArr,"','")+"')", new MDataMap());
		
		for (MDataMap mDataMap : sellProductCodeMapList) {
			sellProductCodeMap.put(mDataMap.get("sku_code"), mDataMap.get("sell_productcode"));
		}
		
		for (MDataMap detailMap : orderDetailMapList) {
			OrderDetailInfo detailInfo = new OrderDetailInfo();
			detailInfo.setPrice(Double.parseDouble(detailMap.get("cost_price")));
			detailInfo.setQuantity(Integer.parseInt(detailMap.get("sku_num")));
			detailInfo.setGoods_id(sellProductCodeMap.get(detailMap.get("sku_code")));
			detailInfo.setSku(sellProductCodeMap.get(detailMap.get("sku_code")));
			
			orderDetailList.add(detailInfo);
			
			BigDecimal skuNum = new BigDecimal(detailInfo.getQuantity());			//购买数量
			BigDecimal price = new BigDecimal(detailInfo.getPrice());			//购买单价
			orderAmount = orderAmount.add(skuNum.multiply(price));
		}
		orderBaseInfo.setOrder_amount(orderAmount.doubleValue());	//订单总金额应该用sku成本价格去算，切记
		orderBaseInfo.setGoods(orderDetailList);
		
		result.setData(orderBaseInfo);
		return result;
	}
}
