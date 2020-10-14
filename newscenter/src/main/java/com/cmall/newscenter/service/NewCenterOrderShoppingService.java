package com.cmall.newscenter.service;




import com.cmall.groupcenter.service.AlipayMoveInformationService;
import com.cmall.ordercenter.service.OrderShoppingService;
import com.srnpr.zapcom.baseclass.BaseClass;

public class NewCenterOrderShoppingService extends BaseClass {
	/**
	 *根据订单号删除本订单对应的购物车中的商品 
	 */
	public boolean deletefamilySkuToShopCart(String orderCode){

			AlipayMoveInformationService alipayMoveInformationService = new AlipayMoveInformationService();
			alipayMoveInformationService.synchronizationAlipayMove(orderCode);   //同步支付宝数据
			OrderShoppingService orderShoppingService = new OrderShoppingService();
			boolean trueAndFalse = orderShoppingService.deleteSkuToShopCart(orderCode);
			return trueAndFalse;

	}
	
}
