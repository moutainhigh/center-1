package com.cmall.ordercenter.alipay.config;

import java.util.HashMap;
import java.util.Map;

import com.srnpr.zapcom.baseclass.BaseClass;

/* *
 * 支付宝
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.3
 *日期：2012-08-10
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。

 *提示：如何获取安全校验码和合作身份者ID
 *1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *2.点击“商家服务”(https://b.alipay.com/order/myOrder.htm)
 *3.点击“查询合作者身份(PID)”、“查询安全校验码(Key)”

 *安全校验码查看时，输入支付密码后，页面呈灰色的现象，怎么办？
 *解决方法：
 *1、检查浏览器配置，不让浏览器做弹框屏蔽设置
 *2、更换浏览器或电脑，重新登录查询。
 */

public class AlipayConfig extends BaseClass {
	// ↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	// 合作身份者ID，以2088开头由16位纯数字组成的字符串
	// public static String partner = "";
	// // 商户的私钥
	// public static String key = "";
	// //乾和企业级账号
	// public static String seller_email = "";
	/**
	 * 乾和
	 */
	// public static String partner = "2088511816887140";
	// //合作身份者ID，以2088开头由16位纯数字组成的字符串
	// public static String seller_email = "weigongshe@52yungo.com"; ////乾和企业级账号
	// public static String key = "wq8apvhmzcpbt3l7yqvqn4hz07pq9oi4"; //商户的私钥
	// (乾和新)
	// public static String key = "3vofszh1fz31kq8qllgx6wv73r1a5lhf"; //商户的私钥
	// (乾和旧)
	// public static String key =
	// "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMCKIVvh6FMB50R1mUBeKR05nFTW/bHTnaAaRfR2eQ0+C31YHuGG61estWB6XzFersHYKb18BdZLb/Ra+s4vQauRGFZiFfsqc7HKrRVCfYDaPs9u5LEDRr2975sAYZiKOffgcUgwcR9Oj0JaqWI91xL8Pnl3gyVtIsLf7iD7rhlXAgMBAAECgYAqyMb+5uU8RMkCQmuKjSHvt5SQmbGIKXD2WcA/wW/GzIm7EbDTBqsXMW6ggLDUhKiqtIEZ9QxLATpgfzMKTB/4QB0A3OJ6Uw9qpKdR8x7XMPRMehZnbw6JQ7ZwbGPjqkMN0U7MlqUM0e+ufekSjUBWmSPH63yJlZGMgl3LQvdE2QJBAOkv17uzcuoJ6pmGEzUjlbWhg0LYNisAa8iOUnUkrqzn/y7mtEx4p9zqTjRAu2yy0/ck2LpNY9GuMlg+NL5vS1sCQQDTYEbtWDE1pFaZPLCMkprLQLhT3eeLQzP7d60mfQW96W3QjprlipvHj4kDssX1/RnQleivNY/JfepFcelOzBa1AkAZ44HkCOw9J5SwLr57K9Q3MhNMnIyHAaj1vzdQYh4yfB9MqbhitRKN6EV+b6FfVAtMaP7W0DjA0sIsIdvhOKH5AkBft5BGuBIIlXN1jqrv7Q9VjOgraigIwxTOAcKR1Dl+Zy8IKxtvaFXkh1XnK9RC8Sr4bnngpWOIPZGRguTAfuClAkEAoMteToYMktB3/Skb+mkL0OZrU6GPdJ2HO9XHnYlXQNdpB3v28DTXJLDYEVAhUVZ5JScuoJWpBW2H4BCoG2mBXw==";

	/**
	 * 惠家有
	 */
	 public static String partner = "2088911718925902"; //惠家有
	 public static String key = "x90awn8oi6g3jnj4s972p8s4b8lf72z2"; //商户的私钥
	 public static String seller_email = "hjy@ichsy.com";  //(惠家有)
	/**
	 * 家有惠
	 */
//	public static String partner = "2088501700700194"; //家有
//	 public static String key = "8qt1cg5ncj1i471lm6i3gb7zrxq33zmw"; //商户的私钥
//	 public static String seller_email = "webpay2011@jiayougo.com";  //(家有)

	/**
	 * 刘嘉玲
	 */
//	public static String partner = "2088611819393368"; // 嘉玲
//	public static String key = "bl068keirvz07wvnv3mu2d7a6ppsnfpg"; // 商户的私钥
//																	// (刘嘉玲)
//	public static String seller_email = "zhifubao@carinalau.cn";

	// ↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

	// 调试用，创建TXT日志文件夹路径
	public static String log_path = "H:\\";

	// 字符编码格式 目前支持 gbk 或 utf-8

	public static String input_charset = "UTF-8";     //"    UTF-8";

	// 签名方式 不需修改
	public static String sign_type = "MD5";

	// 网页支付即时到账接口名称
	public static String serviceWeb = "create_direct_pay_by_user";
	// 移动支付即时到帐接口名称
	public static String serviceMove = "mobile.securitypay.pay";

	public static final String HUIMEILI_SELLORCODE = "SI2007"; // 惠美梨商品编号(用乾和的支付账号)
	public static final String HUIJIAYOU_SELLORCODE = "SI2003"; // 惠家有商品编号(家有支付宝账号)
	public static final String LIUJIALING_SELLORCODE = "SI2001"; // 刘嘉玲商品编号(目前在用家有)
	public static final String JIAYOUHUI_SELLORCODE = "SI2009"; // 家有商品编号(目前在用家有)
	public static final String SHAPIGOU_SELLORCODE = "SI3003"; //沙皮狗(和惠家有项目所使用的账号一样)

	/**
	 * 网页支付参数
	 * 
	 * @param sellerCode
	 * @return
	 */
	public Map<String, String> alipayWebConfig(String sellerCode) {
		Map<String, String> map = new HashMap<String, String>();

		if (sellerCode != null && !"".equals(sellerCode)) {
			map.put("sign_type", "MD5");
			map.put("input_charset", "UTF-8");
			
			if(HUIJIAYOU_SELLORCODE.equals(sellerCode) || SHAPIGOU_SELLORCODE.equals(sellerCode)){
				map.put("notify_url",
						bConfig("ordercenter.alipay_serviceFamilyWeb_response")); // 惠家有回调地址
			}else if(JIAYOUHUI_SELLORCODE.equals(sellerCode)){
				map.put("notify_url",
						bConfig("ordercenter.alipay_serviceWeb_response")); // 家有惠回调地址
			}else{
				map.put("notify_url",
						bConfig("ordercenter.alipay_serviceWeb_response")); // 家有惠回调地址
			}
			
			map.put("return_url", bConfig("ordercenter.return_url_web")); // 支付成功后跳转页
			map.put("return_url_pinhaohuo", bConfig("ordercenter.return_url_web_pinhaohuo")); // 拼好货支付成功跳转页

			
			if (HUIMEILI_SELLORCODE.equals(sellerCode)) {
				map.put("partner", bConfig("ordercenter.partner_QianHe"));
				map.put("seller_email",
						bConfig("ordercenter.seller_email_QianHe"));
				map.put("key", bConfig("ordercenter.key_QianHe"));
				map.put("public_key", bConfig("ordercenter.public_key"));
				
				//即时到账
				map.put("service", bConfig("ordercenter.serviceWeb"));
			} else if (HUIJIAYOU_SELLORCODE.equals(sellerCode)) {
//				map.put("partner", bConfig("ordercenter.partner_jiayou"));
//				map.put("seller_email",
//						bConfig("ordercenter.seller_email_jiayou"));
//				map.put("key", bConfig("ordercenter.key_jiayou"));
//				map.put("public_key", bConfig("ordercenter.public_key"));
				
				
				map.put("partner", bConfig("ordercenter.partner_Huijiayou"));
				map.put("seller_email",
						bConfig("ordercenter.seller_email_Huijiayou"));
				map.put("key", bConfig("ordercenter.key_Huijiayou"));
				map.put("public_key", bConfig("ordercenter.public_key"));
				
				//手机网页版   wap(此属性加上，为支付宝WAP支付)
				map.put("service", bConfig("ordercenter.serviceMoveWeb"));
				
			} else if (LIUJIALING_SELLORCODE.equals(sellerCode)) {
				map.put("partner", bConfig("ordercenter.partner_jiayou"));
				map.put("seller_email",
						bConfig("ordercenter.seller_email_jiayou"));
				map.put("key", bConfig("ordercenter.key_jiayou"));
				map.put("public_key", bConfig("ordercenter.public_key"));
				
				//即时到账
				map.put("service", bConfig("ordercenter.serviceWeb"));
				
//				map.put("partner", bConfig("ordercenter.partner_jialing"));
//				map.put("seller_email",
//						bConfig("ordercenter.seller_email_jialing"));
//				map.put("key", bConfig("ordercenter.key_jialing"));
			}else if(JIAYOUHUI_SELLORCODE.equals(sellerCode)){
//				map.put("partner", bConfig("ordercenter.partner_jiayou"));
//				map.put("seller_email",
//						bConfig("ordercenter.seller_email_jiayou"));
//				map.put("key", bConfig("ordercenter.key_jiayou"));
//				map.put("public_key", bConfig("ordercenter.public_key"));
				
				map.put("partner", bConfig("ordercenter.partner_Huijiayou"));
				map.put("seller_email",
						bConfig("ordercenter.seller_email_Huijiayou"));
				map.put("key", bConfig("ordercenter.key_Huijiayou"));
				map.put("public_key", bConfig("ordercenter.public_key"));
				
				//即时到账
				map.put("service", bConfig("ordercenter.serviceWeb"));
			}else if(SHAPIGOU_SELLORCODE.equals(sellerCode)){
				map.put("partner", bConfig("ordercenter.partner_Huijiayou"));
				map.put("seller_email",
						bConfig("ordercenter.seller_email_Huijiayou"));
				map.put("key", bConfig("ordercenter.key_Huijiayou"));
				map.put("public_key", bConfig("ordercenter.public_key"));
				
				//手机网页版   wap(此属性加上，为支付宝WAP支付)
				map.put("service", bConfig("ordercenter.serviceMoveWeb"));
			}
		}
		return map;
	}
	
	
	

}
