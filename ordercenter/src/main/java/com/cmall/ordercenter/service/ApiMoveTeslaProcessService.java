package com.cmall.ordercenter.service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cmall.ordercenter.alipay.config.AlipayMoveConfig;
import com.cmall.ordercenter.alipay.sign.RSA;
import com.cmall.ordercenter.alipay.util.AlipaySubmit;
import com.srnpr.xmassystem.support.PlusSupportPay;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 支付宝移动支付
 * 
 * @author shiyz
 * 
 */
public class ApiMoveTeslaProcessService extends BaseClass {
	public static final String OC_ORDERINFO = "oc_orderinfo";
	public static final String OC_PAYMENT = "oc_payment";// 支付宝接口表名
	public static final String HC_ORDER = "hc_order"; // 约TA 订单单表
	public static final String OC_UNIONPAY = "oc_Unionpay";// 和包回调信息日志表
	public static final String moveMark = "001"; // 移动支付标示
	public static final String webMark = "002"; // 移动支付标示

	/**
	 * 支付宝移动支付链接
	 * 
	 * @param orderCode
	 * @param flag
	 *            true: 以把支付类型放入缓存中
	 * @return
	 */
	public String alipayMoveParameter(String orderCode, boolean flag,
			String sellerCode, String dueMoney) {
		Map<String, String> map = new HashMap<String, String>();
		String strParam = null;
		String value = null;
		String sOut = "";

		// 商品名称
		String productNameAll = bConfig("ordercenter.PRODUCT_NAME");

		if (orderCode != null && !"".equals(orderCode)) {
			Map<String, String> alipayMoveConfigMap = new HashMap<String, String>();
			AlipayMoveConfig alipayMoveConfig = new AlipayMoveConfig();
			// 大订单
			if ("OS".equals(orderCode.substring(0, 2))) {
				/*
				 * 目前只有惠家有 有订单拆分
				 */
				if ("SI2003".equals(sellerCode) || "SI3003".equals(sellerCode)) { // 判断是哪个项目
					alipayMoveConfigMap = alipayMoveConfig
							.alipayConfig(sellerCode); // 获取支付相关配置信息

					if (alipayMoveConfigMap != null
							&& !"".equals(alipayMoveConfigMap)
							&& alipayMoveConfigMap.size() > 0) {
						// 获取所有小订单的商品名称

						map = objectToMapValues(dueMoney, orderCode, "",
								productNameAll, alipayMoveConfigMap, moveMark);

					}
				}

				if (!flag) {
					// 把此订单的支付状态 放入缓存中, 目的是为了在详情页中查询此订单的支付方式
					new PlusSupportPay().fixPayFrom(orderCode, "449746280003");
				}

			} else if ("DD".equals(orderCode.substring(0, 2))) {
				// 小订单
				alipayMoveConfigMap = alipayMoveConfig.alipayConfig(sellerCode); // 获取支付相关配置信息

				if (alipayMoveConfigMap != null
						&& !"".equals(alipayMoveConfigMap)
						&& alipayMoveConfigMap.size() > 0) {

					map = objectToMapValues(dueMoney, orderCode, "",
							productNameAll, alipayMoveConfigMap, moveMark);
				}
			}

			if (map != null && !"".equals(map) && map.size() > 0) {

				/*
				 * 以下计算往支付传的失效时间 ，都是基于order_Info_upper表的create_time进行计算的
				 */
				strParam = "partner=\""
						+ map.get("partner")
						+ "\"&seller_id=\""
						+ map.get("seller_id")
						+ "\"&out_trade_no=\""
						+ map.get("out_trade_no")
						+ "\"&subject=\""
						+ map.get("subject")
						+

						" \"&total_fee=\""
						+ map.get("total_fee")
						+ "\"&notify_url=\""
						+ map.get("notify_url")
						+ "\"&service=\"mobile.securitypay.pay\"&payment_type=\""
						+ map.get("payment_type")
						+ "\"&_input_charset=\"utf-8\"&it_b_pay=\"2m\"&show_url=\"m.alipay.com\"&success=\"true\"";
			}

			value = RSA.sign(strParam, alipayMoveConfigMap.get("key"), "UTF-8");
			sOut = strParam + "&sign=\"" + URLEncoder.encode(value)
					+ "\"&sign_type=\"RSA\"";

		}
		return sOut;
	}

	private Map<String, String> objectToMapValues(String dueMoney,
			String out_trade_no, String body, String subject,
			Map<String, String> alipayMoveConfigMap, String mark) {
		MDataMap map = new MDataMap();
		if ("001".equals(mark)) { // 移动支付
			map.put("service", bConfig("ordercenter.serviceMove"));
			map.put("notify_url",
					bConfig("ordercenter.alipay_serviceMove_response"));
		} else if ("002".equals(mark)) { // 网页支付
			map.put("service", bConfig("ordercenter.serviceWeb"));
			map.put("notify_url",
					bConfig("ordercenter.alipay_serviceWeb_response"));
		}
		map.put("partner", alipayMoveConfigMap.get("partner"));
		map.put("_input_charset", "UTF-8");

		map.put("out_trade_no", out_trade_no);
		map.put("subject", subject);
		map.put("payment_type", "1");
		map.put("seller_id", alipayMoveConfigMap.get("seller_email"));
		map.put("total_fee", dueMoney); // 库里查的
		map.put("body", body);
		/*
		 * 生成要请求给支付宝的参数数组
		 */
		Map<String, String> sPara = AlipaySubmit.buildRequestPara(map);
		return sPara;
	}
	
	
	/**
	 * 获取签名后的sign   
	 * @param orderCode
	 * @return
	 */
	public Map<String, String> alipaySign(String orderCode,String sellerCode,String dueMoney) {
		Map<String, String> map = new HashMap<String, String>();
		
		Map<String, String>  alipayMoveConfigMap = new HashMap<String,String>();
		AlipayMoveConfig alipayMoveConfig = new AlipayMoveConfig();   
		
		// 商品名称
		String productNameAll = bConfig("ordercenter.PRODUCT_NAME");
		
		if (orderCode != null && !"".equals(orderCode)) {
			//区分大订单小订单
			if("OS".equals(orderCode.substring(0, 2))){
				
						alipayMoveConfigMap = alipayMoveConfig.alipayConfig(sellerCode);  //获取支付相关配置信息
						
						map = objectToMapValues(dueMoney, orderCode,
								"", productNameAll,alipayMoveConfigMap,moveMark);
						return map;
					}
			}else if("DD".equals(orderCode.substring(0, 2))){
				
						alipayMoveConfigMap = alipayMoveConfig.alipayConfig(sellerCode);  //获取支付相关配置信息
						map = objectToMapValues(dueMoney, orderCode,
								"", productNameAll,alipayMoveConfigMap,moveMark);
						return map;
					}
			
			
		return null;
	}
}
