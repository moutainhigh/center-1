package com.cmall.newscenter.beauty.api;

import java.util.ArrayList;
import java.util.List;
import com.cmall.newscenter.beauty.model.AddOrderInput;
import com.cmall.newscenter.beauty.model.AddOrderResult;
import com.cmall.newscenter.beauty.model.OrderDetail;
import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 惠美丽—提交订单Api
 * @author houwen
 * date: 2014-10-08
 * @version1.0
 */
public class AddOrder  extends RootApiForToken<AddOrderResult, AddOrderInput> {

	public AddOrderResult Process(AddOrderInput inputParam,
			MDataMap mRequestMap) {
		
		AddOrderResult result = new AddOrderResult();
		
		// 设置相关信息
		if (result.upFlagTrue()) {
			
			List<Order> list = new ArrayList<Order>();
			OrderService orderService = new OrderService();
			
			String Order_code = WebHelper.upCode("DD");
			list.get(0).setOrderCode(Order_code);
			list.get(0).setBuyerCode(getUserCode());
			list.get(0).setPayType(inputParam.getPay_type());  //支付方式
			list.get(0).setSendType(inputParam.getSend_type());  //配送方式
			list.get(0).setTransportMoney(inputParam.getTransportMoney());  //实际运费
			list.get(0).setPayedMoney(inputParam.getPayedMoney());//微账户支付金额
			list.get(0).setDueMoney(inputParam.getDueMoney());//应付金额
			list.get(0).setOrderMoney(inputParam.getOrderMoney());//订单金额
			list.get(0).setOrderType(inputParam.getOrder_type());//订单类型
			list.get(0).setOrderSource(inputParam.getOrder_souce());//订单来源
			list.get(0).setAppVersion(inputParam.getApp_vision());//app版本
			if(inputParam.getGoods()!=null&&!inputParam.getGoods().isEmpty()){
				for (int i = 0; i < inputParam.getGoods().size(); i++) {
					
					OrderDetail add = inputParam.getGoods().get(i);
					list.get(0).getProductList().get(i).setProductCode(add.getSku_code());
					list.get(0).getProductList().get(i).setSkuNum(add.getSku_num());
					
				}
		
			}
			
			list.get(0).getAddress().setReceivePerson(inputParam.getOrderAddress().getBuyer_name());
			list.get(0).getAddress().setAreaCode(inputParam.getOrderAddress().getArea_code());
			list.get(0).getAddress().setAddress(inputParam.getOrderAddress().getBuyer_address());
			list.get(0).getAddress().setMobilephone(inputParam.getOrderAddress().getBuyer_mobile());
			list.get(0).getAddress().setPostCode(inputParam.getOrderAddress().getPostCode());
			StringBuffer error = new StringBuffer();
			orderService.AddOrderListTx(list, error,"HDS1");
			
			result.setOrder_code(Order_code);
			result.setOrder_money(inputParam.getOrderMoney().toString());
			result.setPay_type(inputParam.getPay_type());
				
		}
		return result;
    }
}
