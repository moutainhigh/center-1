package com.cmall.groupcenter.service;

import java.util.ArrayList;
import java.util.List;

import com.cmall.ordercenter.service.OrderShoppingService;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class AlipayOrderShoppingService extends BaseClass{
	/**
	 *根据订单号删除本订单对应的购物车中的商品   并且同步支付数据
	 */
	public boolean deletefamilySkuToShopCart(String orderCode){
			AlipayMoveInformationService alipayMoveInformationService = new AlipayMoveInformationService();
			alipayMoveInformationService.synchronizationAlipayMove(orderCode);   //同步支付宝数据
			OrderShoppingService orderShoppingService = new OrderShoppingService();
			boolean trueAndFalse = orderShoppingService.deleteSkuToShopCart(orderCode);
			return trueAndFalse;

	}
	
	
	/**
	 *根据订单号删除本订单对应的购物车中的商品   并且同步支付数据(最新)
	 */
	public void deletefamilySkuToShopCartNew(String orderCode){

			AlipayMoveInformationService alipayMoveInformationService = new AlipayMoveInformationService();
			OrderShoppingService orderShoppingService = new OrderShoppingService();
			List<MDataMap> orderinfoList = new ArrayList<MDataMap>();
			
			//同步支付宝数据      
			alipayMoveInformationService.synchronizationAlipayMove(orderCode);   
			
			// 通过订单号查询订单金额
			orderinfoList = DbUp.upTable("oc_paydetail").queryAll("", "", "", new MDataMap("pay_code", orderCode));
			
			for(MDataMap map : orderinfoList){
				orderShoppingService.deleteSkuToShopCart(map.get("order_code"));    //更新订单状态
			}

	}
	
	

	/**
	 *根据订单号删除本订单对应的购物车中的商品 
	 */
	public boolean deletefamilySkuToShopCartNotSynchronization(String orderCode){

		OrderShoppingService orderShoppingService = new OrderShoppingService();
		boolean trueAndFalse = orderShoppingService.deleteSkuToShopCart(orderCode);
		return trueAndFalse;

	}
	

}
