package com.cmall.groupcenter.mlg.api;



import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.mlg.model.ApiGetOrderInfoForMLGInput;
import com.cmall.groupcenter.mlg.model.ApiGetOrderInfoForMLGResult;
import com.cmall.groupcenter.mlg.model.OrderBaseInfo;
import com.cmall.groupcenter.mlg.model.OrderDetailInfo;
import com.cmall.groupcenter.model.MPageData;
import com.cmall.groupcenter.model.PageOption;
import com.cmall.groupcenter.util.DataPaging;
import com.cmall.productcenter.service.MyService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForMember;
/**
 * 根据时间段获取麦乐购订单信息
 * @author ligj
 *
 */
public class ApiGetOrderInfoForMLG extends RootApiForMember<ApiGetOrderInfoForMLGResult, ApiGetOrderInfoForMLGInput>{

	public ApiGetOrderInfoForMLGResult Process(ApiGetOrderInfoForMLGInput inputParam, MDataMap mRequestMap) {
		ApiGetOrderInfoForMLGResult result = new ApiGetOrderInfoForMLGResult();
		List<OrderBaseInfo> orderBaseInfoList = new ArrayList<OrderBaseInfo>();
		String startTime = inputParam.getStarttime();
		String endTime = inputParam.getEndtime();
		int num = inputParam.getNum();
		int page = inputParam.getPage();
		
		PageOption paging = new PageOption();
		if(page<=0){
			return result;
		}
		paging.setOffset(page-1);
		paging.setLimit(num);
		MPageData mPageData=new MPageData();
		
		String sWhere ="update_time>='"+startTime+"' AND update_time<='"+endTime+"' AND small_seller_code='SF03MLG' AND seller_code='SI2003' AND order_status='4497153900010002' AND delete_flag='0'";
		
		mPageData=DataPaging.upPageData("oc_orderinfo", "order_code,due_money,update_time,create_time", "update_time,zid",sWhere, new MDataMap(),paging);
		
		//查询数据库中喜欢的商品的总条数
		int totalNum = mPageData.getPageResults().getTotal();
		//总页数
		int pagination=0;
		if(totalNum%num==0){
			pagination=totalNum/num;
		}else {
			pagination=totalNum/num+1;
		}
		Map<String,OrderBaseInfo> orderInfoMap = new HashMap<String,OrderBaseInfo>();
		List<String> orderCodeArr = new ArrayList<String>();		//得到所有订单编号，查询订单其他关联信息时用得到
		if (totalNum > 0) {
			for(MDataMap orderMap:mPageData.getListData()){
				OrderBaseInfo orderBaseInfo = new OrderBaseInfo();
				String orderCode = orderMap.get("order_code");
				double dueMoney = Double.parseDouble(orderMap.get("due_money"));
				String payTime = orderMap.get("update_time");
				String createTime = orderMap.get("create_time");
				
				orderBaseInfo.setOrder_id(orderCode);
				orderBaseInfo.setOrder_amount(dueMoney);				//订单总金额应该用sku成本价格去算
				orderBaseInfo.setPay_time(payTime);
				orderBaseInfo.setCtime(createTime);
				
				orderInfoMap.put(orderCode, orderBaseInfo);
				
				orderCodeArr.add(orderCode);
			}
			//地址信息
			List<MDataMap> addressMapList = DbUp.upTable("oc_orderadress").queryAll("order_code,area_code,address,mobilephone,receive_person,auth_true_name,auth_idcard_number", "", "order_code in ('"+StringUtils.join(orderCodeArr,"','")+"')", new MDataMap());
			MDataMap areaMap = new MyService().getAreaNameMap("");
			for (MDataMap addressMap : addressMapList) {
				String orderCode = addressMap.get("order_code");
				OrderBaseInfo orderBaseInfo = orderInfoMap.get(orderCode);
				orderBaseInfo.setAddress(addressMap.get("address"));		//详细地址
				orderBaseInfo.setRegion(addressMap.get("area_code"));		//所在地区
				orderBaseInfo.setConsignee(addressMap.get("receive_person"));	//收货人姓名
				orderBaseInfo.setPhone(addressMap.get("mobilephone"));		//手机号码
				orderBaseInfo.setName(addressMap.get("auth_true_name"));	//姓名
				orderBaseInfo.setIDNumber(addressMap.get("auth_idcard_number"));//证件号
				String areaCode = addressMap.get("area_code");
				if (areaMap != null && !areaMap.isEmpty() && StringUtils.isNotBlank(areaCode) && areaCode.length()==6) {
					orderBaseInfo.setProvince(areaMap.get(areaCode.substring(0, 2)+"0000"));
					orderBaseInfo.setCity(areaMap.get(areaCode.substring(0, 4)+"00"));
					orderBaseInfo.setArea(areaMap.get(areaCode));
				}
				orderInfoMap.put(orderCode, orderBaseInfo);
			}
			Map<String,List<OrderDetailInfo>> orderDetailMap = new HashMap<String,List<OrderDetailInfo>>();
			//商品信息
			List<MDataMap> orderDetailMapList = DbUp.upTable("oc_orderdetail").queryAll("order_code,sku_code,product_code,cost_price,sku_num", "", "order_code in ('"+StringUtils.join(orderCodeArr,"','")+"')", new MDataMap());
			
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
				String orderCode = detailMap.get("order_code");
				OrderDetailInfo detailInfo = new OrderDetailInfo();
				detailInfo.setPrice(Double.parseDouble(detailMap.get("cost_price")));
				detailInfo.setQuantity(Integer.parseInt(detailMap.get("sku_num")));
				detailInfo.setGoods_id(sellProductCodeMap.get(detailMap.get("sku_code")));
				detailInfo.setSku(sellProductCodeMap.get(detailMap.get("sku_code")));
				
				List<OrderDetailInfo>  orderDetailList = orderDetailMap.get(orderCode);
				if (null == orderDetailList) {
					orderDetailList = new ArrayList<OrderDetailInfo>();
				}
				orderDetailList.add(detailInfo);
				
				orderDetailMap.put(orderCode, orderDetailList);
			}
			
			for (String orderCode : orderCodeArr) {
				OrderBaseInfo orderBaseInfo = orderInfoMap.get(orderCode);
				List<OrderDetailInfo> orderDetailList = orderDetailMap.get(orderCode);
				orderBaseInfo.setGoods(orderDetailList);
				BigDecimal orderAmount = BigDecimal.ZERO;		//计算出来的订单总金额
				for (OrderDetailInfo orderDetailInfo : orderDetailList) {
					BigDecimal skuNum = new BigDecimal(orderDetailInfo.getQuantity());			//购买数量
					BigDecimal price = new BigDecimal(orderDetailInfo.getPrice());			//购买单价
					orderAmount = orderAmount.add(skuNum.multiply(price));
				}
				orderBaseInfo.setOrder_amount(orderAmount.doubleValue());	//订单总金额应该用sku成本价格去算，切记
				
				orderInfoMap.put(orderCode, orderBaseInfo);
				
				orderBaseInfoList.add(orderBaseInfo);//添加到订单信息list里，作为结果返回
			}
		}
		result.setList(orderBaseInfoList);
		result.setPage(pagination);
		result.setTotal(totalNum);
		return result;
	}
}
