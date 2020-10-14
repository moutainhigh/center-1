package com.cmall.newscenter.alipay.process;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.cmall.newscenter.service.NewCenterOrderShoppingService;
import com.cmall.ordercenter.model.PayResult;
import com.cmall.ordercenter.service.AlipayProcessService;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.helper.WebSessionHelper;
/**
 * 支付宝
 * @author wz
 *
 */
public class AlipayProcess extends BaseClass {

	public static final String OC_ORDERINFO = "oc_orderinfo";//主表
	public static final String OC_PAYMENT = "oc_payment";//支付宝接口表名
	
	/**
	 * 支付宝回调
	 * @param out_trade_no
	 * @param trade_no
	 * @param trade_status
	 * @param notify_time
	 * @param notify_type
	 * @param notify_id
	 * @param buyer_email
	 * @param seller_email
	 * @param sign_type
	 * @param sign
	 * @param total_fee
	 * @param mark
	 * @param gmt_payment
	 * @param out_channel_inst
	 */
	
	public void resultForm(String mark){
//		WebSessionHelper webSession=new WebSessionHelper();
//		webSession.upRequest("");
		NewCenterOrderShoppingService orderShoppingService = new NewCenterOrderShoppingService();
		
		HttpServletRequest request = WebSessionHelper.create().upHttpRequest();
		
		Map mMaps=request.getParameterMap();
		
		
		StringBuffer str = new StringBuffer();
		String endStr = "";
		MDataMap insertDatamap = new MDataMap();
		List<String> list = new ArrayList<String>();
		
		for(Object oKey: mMaps.keySet())
		{
			insertDatamap.put(oKey.toString(), request.getParameter(oKey.toString()));
			insertDatamap.put("mark", mark);
			
			if(!"sign_type".equals(oKey.toString()) && !"sign".equals(oKey.toString())){
				list.add(oKey.toString()+"="+request.getParameter(oKey.toString()));
			}
			
		}
		 Collections.sort(list);   //对List内容进行排序
		 
		 for(String nameString : list){
				str.append(nameString+"&");
		 }
		
		endStr = str.substring(0, str.toString().length()-1);
		
		AlipayProcessService alipayProcessService = new AlipayProcessService();
		
		PayResult payResult = alipayProcessService.resultValue(insertDatamap,
				endStr); // 将数据插入到oc_payment 验签
		if (payResult.upFlagTrue()) {
			orderShoppingService.deletefamilySkuToShopCart(payResult
					.getOrderCode());
		}
	}
	
}
