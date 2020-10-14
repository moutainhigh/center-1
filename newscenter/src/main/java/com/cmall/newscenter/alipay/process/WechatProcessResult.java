package com.cmall.newscenter.alipay.process;

import javax.servlet.http.HttpServletRequest;

import com.cmall.groupcenter.service.AlipayOrderShoppingService;
import com.cmall.ordercenter.model.PayResult;
import com.cmall.ordercenter.service.ApiWechatProcessService;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.helper.WebSessionHelper;

/**
 * 微信支付(回调内容)
 * 
 * @author wz
 * 
 */
public class WechatProcessResult extends BaseClass {
	/**
	 * 微信支付接口
	 * 
	 * @param orderCode
	 * @param ip
	 * @param rootResult
	 * @return
	 */
//	public MDataMap wechatMove(String orderCode, String ip,
//			RootResult rootResult) {
//		MDataMap mDataMap = new MDataMap();
//		try {
//			ApiWechatProcessService apiWechatProcessService = new ApiWechatProcessService();
//			mDataMap = apiWechatProcessService.wechatMovePayment(orderCode, ip,
//					rootResult);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return mDataMap;
//	}

	/**
	 * 微信支付回调方法
	 * 
	 * @return
	 */
	public PayResult responseWechatMove() {
		
		AlipayOrderShoppingService orderShoppingService = new AlipayOrderShoppingService();
		PayResult payResult = new PayResult();
		HttpServletRequest request = WebSessionHelper.create().upHttpRequest();
		ApiWechatProcessService apiWechatProcessService = new ApiWechatProcessService();
		payResult = apiWechatProcessService.responseWechatMoveService(request);
		
		if (payResult.upFlagTrue()) {
			orderShoppingService.deletefamilySkuToShopCart(payResult
					.getOrderCode()); // 根据订单号删除本订单对应的购物车中的商品 并且 同步支付数据
			
		}
		return payResult;
	}
	
	
	/**
	 * 微信支付回调方法(最新)
	 * 
	 * @return
	 */
	public PayResult responseWechatMoveNew() {
		
		AlipayOrderShoppingService orderShoppingService = new AlipayOrderShoppingService();
		PayResult payResult = new PayResult();
		HttpServletRequest request = WebSessionHelper.create().upHttpRequest();
		ApiWechatProcessService apiWechatProcessService = new ApiWechatProcessService();
		payResult = apiWechatProcessService.responseWechatMoveServiceNew(request);
		
		if (payResult.upFlagTrue()) {
			orderShoppingService.deletefamilySkuToShopCartNew(payResult
					.getOrderCode()); // 根据订单号删除本订单对应的购物车中的商品 并且 同步支付数据
			
		}
		return payResult;
	}
}

