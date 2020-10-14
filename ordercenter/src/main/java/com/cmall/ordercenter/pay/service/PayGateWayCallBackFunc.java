package com.cmall.ordercenter.pay.service;

import org.apache.commons.lang.StringUtils;
import com.cmall.membercenter.support.MemberLoginSupport;
import com.cmall.ordercenter.service.OrderShoppingService;
import com.cmall.systemcenter.common.CouponConst;
import com.cmall.systemcenter.enumer.JmsNameEnumer;
import com.cmall.systemcenter.jms.JmsNoticeSupport;
import com.srnpr.xmaspay.request.PayGateWayCallBackRequest;
import com.srnpr.xmaspay.service.IPayGateWayCallBackFunc;
import com.srnpr.xmaspay.util.PayServiceFactory;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 支付网关回调相关业务处理
 * @author pang_jhui
 *
 */
public class PayGateWayCallBackFunc implements IPayGateWayCallBackFunc {

	public RootResultWeb doAfter(PayGateWayCallBackRequest request) {
		
		RootResultWeb rootResultWeb = new RootResultWeb();
		
		String orderCode = request.getC_order();
		
		/*删除购物车信息*/
		new OrderShoppingService().deleteSkuToShopCart(orderCode);
		
		issueCoupon(orderCode);
		
		
		return rootResultWeb;
	}
	
	/**
	 * 优惠券发放
	 * @param orderCode
	 */
	public void issueCoupon(String orderCode){
		
		MDataMap mDataMap = PayServiceFactory.getInstance().getOrderPayService().getOrderInfoUpper(orderCode);
		
		if(mDataMap != null){
			
			String mobileNO = new MemberLoginSupport().getMoblie(mDataMap.get("buyer_code"));
			
			if(StringUtils.isNotEmpty(mobileNO)){
				
				JmsNoticeSupport.INSTANCE.sendQueue(
						JmsNameEnumer.OnDistributeCoupon,
						CouponConst.pay_coupon, new MDataMap(
								"mobile", mobileNO, "manage_code", mDataMap.get("seller_code")));
				
				
			}
			
			
		}
		
	}

}
