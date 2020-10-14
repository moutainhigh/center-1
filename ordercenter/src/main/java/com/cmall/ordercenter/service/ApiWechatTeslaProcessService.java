package com.cmall.ordercenter.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cmall.ordercenter.alipay.util.WXUtil;
import com.cmall.ordercenter.alipay.wechat.ClientRequestHandler;
import com.cmall.systemcenter.util.AnalysisXmlUtil;
import com.srnpr.xmassystem.support.PlusSupportPay;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 微信支付
 * 
 * @author shiyz
 * 
 */
public class ApiWechatTeslaProcessService extends BaseClass {
	public static final String OC_ORDER_PAY_PAYTYPE_WECHAT = "449746280005"; // oc_order_pay中记录的微信支付类型

	public static final String MOVE_TYPE = "100"; // 微信APP

	public static final String MOVE_TYPE_SHAPIGOU = "100ShaPiGouAPP"; // 沙皮狗微信APP

	public static final String WAP_TYPE = "101"; // 微信WAP

	public static final String WAP_NEY_TYPE = "102"; // .net微信WAP

	/**
	 * 微信支付APP支付(最新版本) 惠家有
	 * 
	 * @param orderCode
	 * @param ip
	 * @param rootResult
	 * @return
	 */
	public Map wechatMovePaymentVersionNew(String orderCode, String ip,
			RootResult rootResult, String notify_url, String appid,
			String mch_id, BigDecimal dueMoney) {

		String noncestr = WXUtil.getNonceStr();
		String timestamp = WXUtil.getTimeStamp();
		Map<String, Object> mapOrder = new HashMap<String, Object>();

		// 把此订单的支付状态 放入缓存中, 目的是为了在详情页中查询此订单的支付方式
		new PlusSupportPay().fixPayFrom(orderCode, "449746280005");
		// 判断此订单是否存在
		if (mapOrder != null ) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("appid", appid);
			map.put("body", "商品");
			map.put("mch_id", mch_id);
			map.put("nonce_str", noncestr);
			map.put("notify_url", notify_url);

			map.put("out_trade_no", orderCode);
			map.put("spbill_create_ip", ip);
			map.put("total_fee", String.valueOf((dueMoney
					.multiply(new BigDecimal(100))).setScale(2,
					BigDecimal.ROUND_HALF_UP)));
			map.put("trade_type", "APP");

			// 获取签名
			String signnew = signVersionNew(map, MOVE_TYPE);
			if (signnew != null && !"".equals(signnew)) {
				map.put("sign", signnew);

				ClientRequestHandler clientRequestHandler = new ClientRequestHandler();

				try {
					URL url = new URL(
							"https://api.mch.weixin.qq.com/pay/unifiedorder");
					URLConnection con = url.openConnection();
					con.setDoOutput(true);
					con.setRequestProperty("Pragma:", "no-cache");
					con.setRequestProperty("Cache-Control", "no-cache");
					con.setRequestProperty("Content-Type", "text/xml");

					OutputStreamWriter out = new OutputStreamWriter(
							con.getOutputStream());
					String xmlInfo = clientRequestHandler.getXmlBody(map);
					out.write(xmlInfo);
					out.flush();
					out.close();
					BufferedReader br = new BufferedReader(
							new InputStreamReader(con.getInputStream()));
					String linenew = "";
					String line = null;
					// 微信返回信息
					for (line = br.readLine(); line != null; line = br
							.readLine()) {
						linenew = linenew + line;
					}
					// 解析xml
					Map wechatMoveMap = AnalysisXmlUtil
							.readStringXmlOut(linenew);
					if (wechatMoveMap != null && !"".equals(wechatMoveMap)
							&& wechatMoveMap.size() > 0) {
						wechatMoveMap.put("timestamp", timestamp); // 时间戳

						Map mm = new HashMap();
						mm.put("appid",
								String.valueOf(wechatMoveMap.get("appid")));
						mm.put("noncestr",
								String.valueOf(wechatMoveMap.get("nonce_str")));
						mm.put("package", "Sign=WXPay");
						mm.put("partnerid",
								String.valueOf(wechatMoveMap.get("mch_id")));
						mm.put("prepayid",
								String.valueOf(wechatMoveMap.get("prepay_id")));
						mm.put("timestamp", timestamp);

						// 生成客户端唤起微信支付签名
						String sign = genAppSign(mm, "100");

						if (sign != null && !"".equals(sign)) {
							wechatMoveMap.put("sign", sign);
							return wechatMoveMap;
						} else {
							rootResult.setResultMessage("生成唤起微信支付的签名失败!");
						}
					} else {
						rootResult.setResultCode(939301401);
						rootResult.setResultMessage("解析微信支付返回的xml失败!");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				rootResult.setResultCode(939301400);
				rootResult.setResultMessage("签名不能为空");
			}

		} else {
			rootResult.setResultCode(939301200);
			rootResult.setResultMessage("订单编号不存在");
		}

		return null;
	}

	/**
	 * 微信支付生成验签(腾讯最新版本，以后一直使用)
	 * 
	 * @param map
	 *            : 验签参数
	 * @return
	 */
	public String signVersionNew(Map<String, String> map, String type) {

		StringBuffer str = new StringBuffer();
		String stringSignTemp = "";
		List<String> list = new ArrayList<String>();

		for (Object oKey : map.keySet()) {
			if (map.get(oKey.toString()) != null
					&& !"".equals(map.get(oKey.toString()))) {
				list.add(oKey.toString() + "=" + map.get(oKey.toString()));
			}
		}
		Collections.sort(list); // 对List内容进行排序

		for (String nameString : list) {
			str.append(nameString + "&");
		}

		if ("100".equals(type)) { // 微信移动支付 验签
			stringSignTemp = str.substring(0, str.toString().length()) + "key="
					+ bConfig("ordercenter.PARTNER_KEY_HUJIAYOU");
		} else if ("101".equals(type)) { // 微信JSAPI、NATIVE支付验签
			stringSignTemp = str.substring(0, str.toString().length()) + "key="
					+ bConfig("ordercenter.PARTNER_KEY_HUJIAYOU_WAP");
		} else if ("102".equals(type)) { // 微信WAP支付 .net 测试
			stringSignTemp = str.substring(0, str.toString().length()) + "key="
					+ bConfig("ordercenter.PARTNER_KEY_NET_WAP");
		} else if ("100ShaPiGouAPP".equals(type)) {
			stringSignTemp = str.substring(0, str.toString().length()) + "key="
					+ bConfig("ordercenter.PARTNER_KEY_SHAPIGOU");
		}
		String sign = com.cmall.ordercenter.alipay.sign.MD5.sign(
				stringSignTemp, "UTF8").toUpperCase();

		return sign;
	}

	public String genAppSign(Map map, String type) {
		StringBuilder sb = new StringBuilder();
		StringBuffer str = new StringBuffer();
		String stringSignTemp = "";

		List<String> list = new ArrayList<String>();

		for (Object oKey : map.keySet()) {
			if (map.get(oKey.toString()) != null
					&& !"".equals(map.get(oKey.toString()))) {
				list.add(oKey.toString() + "=" + map.get(oKey.toString()));
			}
		}
		Collections.sort(list); // 对List内容进行排序

		for (String nameString : list) {
			str.append(nameString + "&");
		}

		if ("100".equals(type)) { // APP微信支付
			stringSignTemp = str.substring(0, str.toString().length()) + "key="
					+ bConfig("ordercenter.PARTNER_KEY_HUJIAYOU");
		} else if ("101".equals(type)) { // WAP微信支付
			stringSignTemp = str.substring(0, str.toString().length()) + "key="
					+ bConfig("ordercenter.PARTNER_KEY_HUJIAYOU_WAP");
		} else if ("102".equals(type)) { // .net微信支付WAP
			stringSignTemp = str.substring(0, str.toString().length()) + "key="
					+ bConfig("ordercenter.PARTNER_KEY_NET_WAP");
		} else if ("100ShaPiGouAPP".equals(type)) { // APP微信支付(沙皮狗)
			stringSignTemp = str.substring(0, str.toString().length()) + "key="
					+ bConfig("ordercenter.PARTNER_KEY_SHAPIGOU");
		}

		String sign = com.cmall.ordercenter.alipay.sign.MD5.sign(
				stringSignTemp, "UTF8").toUpperCase();
		return sign;
	}

}
