package com.cmall.ordercenter.alipay.sign;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.crypto.Cipher;

import com.cmall.ordercenter.alipay.config.AlipayMoveConfig;

public class RSA {

	public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	/**
	 * RSA签名
	 * 
	 * @param content
	 *            待签名数据
	 * @param privateKey
	 *            商户私钥
	 * @param input_charset
	 *            编码格式
	 * @return 签名值
	 */
	public static String sign(String content, String privateKey,
			String input_charset) {
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
					Base64.decode(privateKey));

			KeyFactory keyf = KeyFactory.getInstance("RSA");

			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			java.security.Signature signature = java.security.Signature
					.getInstance(SIGN_ALGORITHMS);

			signature.initSign(priKey);
			signature.update(content.getBytes(input_charset));

			byte[] signed = signature.sign();

			return Base64.encode(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * RSA验签名检查
	 * 
	 * @param content
	 *            待签名数据
	 * @param sign
	 *            签名值
	 * @param ali_public_key
	 *            支付宝公钥
	 * @param input_charset
	 *            编码格式
	 * @return 布尔值
	 */
	public static boolean verify(String content, String sign,
			String ali_public_key, String input_charset) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			byte[] encodedKey = Base64.decode(ali_public_key);
			PublicKey pubKey = keyFactory
					.generatePublic(new X509EncodedKeySpec(encodedKey));

			java.security.Signature signature = java.security.Signature
					.getInstance(SIGN_ALGORITHMS);

			signature.initVerify(pubKey);
			signature.update(content.getBytes(input_charset));

			boolean bverify = signature.verify(Base64.decode(sign));
			return bverify;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 解密
	 * 
	 * @param content
	 *            密文
	 * @param private_key
	 *            商户私钥
	 * @param input_charset
	 *            编码格式
	 * @return 解密后的字符串
	 */
	public static String decrypt(String content, String private_key,
			String input_charset) throws Exception {
		PrivateKey prikey = getPrivateKey(private_key);

		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, prikey);

		InputStream ins = new ByteArrayInputStream(Base64.decode(content));
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		// rsa解密的字节大小最多是128，将需要解密的内容，按128位拆开解密
		byte[] buf = new byte[128];
		int bufl;

		while ((bufl = ins.read(buf)) != -1) {
			byte[] block = null;

			if (buf.length == bufl) {
				block = buf;
			} else {
				block = new byte[bufl];
				for (int i = 0; i < bufl; i++) {
					block[i] = buf[i];
				}
			}

			writer.write(cipher.doFinal(block));
		}

		return new String(writer.toByteArray(), input_charset);
	}

	/**
	 * 得到私钥
	 * 
	 * @param key
	 *            密钥字符串（经过base64编码）
	 * @throws Exception
	 */
	public static PrivateKey getPrivateKey(String key) throws Exception {

		byte[] keyBytes;

		keyBytes = Base64.decode(key);

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

		return privateKey;
	}

	public static void main(String[] args) throws Exception {
		
		StringBuffer str= new StringBuffer();
//		sbr.append("");
//		sbr.append("payment_type=1&");
		
		String nameString = "discount=0.00&payment_type=1&subject=吉祥七宝台湾白珊瑚典藏套组|&trade_no=2014092756643304&buyer_email=898500456@qq.com&gmt_create=2014-09-27 12:08:26&notify_type=trade_status_sync&quantity=1&out_trade_no=DD140927100150&seller_id=2088511816887140&notify_time=2014-09-27 12:12:43&trade_status=TRADE_FINISHED&is_total_fee_adjust=N&total_fee=0.01&gmt_payment=2014-09-27 12:08:26&seller_email=weigongshe@52yungo.com&gmt_close=2014-09-27 12:08:26&price=0.01&buyer_id=2088102101262043&notify_id=93a221f29638befc0a4734617fa8818e28&use_coupon=N&EWJmDAGnVYfdb0Drw9THGDOzv8rzhFKbSnQTQkIPdxayZrmVZNnjpvqEytMCgQFlOUV1xFh3qSXu8AwJNJ1iVO8jBw90Y+BJl4uzrxAQq1DzkoW2iLp8tkXorf335HLxI2apZ9YdRxp8TSrsr3py6XvVy0pSmi/h4RIpUrraxb8=&RSA";
		
		List<String> list = new ArrayList<String>();
		list.add("discount=0.00&");
		list.add("payment_type=1&");
		list.add("subject=吉祥七宝台湾白珊瑚典藏套组|&");
		list.add("trade_no=2014092756643304&");
		list.add("buyer_email=898500456@qq.com&");
		list.add("gmt_create=2014-09-27 12:08:26&");
		list.add("notify_type=trade_status_sync&");
		list.add("quantity=1&");
		list.add("out_trade_no=DD140927100150&");
		list.add("seller_id=2088511816887140&");
		list.add("notify_time=2014-09-27 12:12:43&");
		list.add("trade_status=TRADE_FINISHED&");
		list.add("is_total_fee_adjust=N&");
		list.add("total_fee=0.01&");
		list.add("gmt_payment=2014-09-27 12:08:26&");
		list.add("seller_email=weigongshe@52yungo.com&");
		list.add("gmt_close=2014-09-27 12:08:26&");
		list.add("price=0.01&");
		list.add("buyer_id=2088102101262043&");
		list.add("notify_id=93a221f29638befc0a4734617fa8818e28&");
		list.add("use_coupon=N&EWJmDAGnVYfdb0Drw9THGDOzv8rzhFKbSnQTQkIPdxayZrmVZNnjpvqEytMCgQFlOUV1xFh3qSXu8AwJNJ1iVO8jBw90Y+BJl4uzrxAQq1DzkoW2iLp8tkXorf335HLxI2apZ9YdRxp8TSrsr3py6XvVy0pSmi/h4RIpUrraxb8=&RSA");
		
		Collections.sort(list);
		
		 for(String name : list){
			 if(!("sign_type".equals(name) || "sign".equals(name))){
					str.append(name);
			 }
		 }
		String aaa ="buyer_email=13311517791&buyer_id=2088512220735027&discount=0.00&gmt_close=2014-09-27 15:17:04&gmt_create=2014-09-27 15:17:03&gmt_payment=2014-09-27 15:17:04&is_total_fee_adjust=N&notify_id=269d469dfb86d118ade4a7c33fe7acaf24&notify_time=2014-09-27 15:17:04&notify_type=trade_status_sync&out_trade_no=DD140927100110&payment_type=1&price=0.01&quantity=1&seller_email=weigongshe@52yungo.com&seller_id=2088511816887140&subject=瑞普高级不锈钢全能锅尊享组|&total_fee=0.01&trade_no=2014092700948102&trade_status=TRADE_FINISHED&use_coupon=N";
		
		//System.out.println(URLEncoder.encode(aaa));
		//verify(aaa,"Ux+b7rryKGS0jKjSKga64p1UpnDOlAGLmop7KEK2Tiun/5qSEmPOG4pjbioxGl+4yQAue+c4rk2YIJaEM0QK+mKC2Dzwd8AhP0OZxMZ6rDhCuOatHJHFPVVrgNH0mEJaQlKC+HfmSvXsIocZ9CJasa+yylmddjc4DOaxcInDONk=", AlipayMoveConfig.ali_public_key, AlipayMoveConfig.input_charset);
		
		
		
		
		
		// getPrivateKey("3vofszh1fz31kq8qllgx6wv73r1a5lhf").toString();
		// sign("partner=2088701138547200&format=xml","MIIEoQIBAAKCAQEAog8SHj/C9xN3HYuiLdQqThPYSxZaxjKoSGxZqzA3w1n3FyXYEH7ZR5StrsxmAm95Pl9eYFfxxAvXlnAEr7QIHYKTIepUrgHuy4Dja9buN2BZ9IieRhLzjIcuqSkUh7I3AAE1DQvcX+M4J+i09l6dFF9yiLrhWAhU8Ry+xKKEhXo5p65mCFGs9iOlIdkJ8sT3nx4hYmar8wXkalnPy3ImQHFv5NUgbmBMJmVeuYpfCQNrzXA5Z+Va1A6Ve4pGHGnJ/+CSMhe7SUlVdwvdmakCjHWXGZD9EuCS5vcP/qI4C/H2LhDfGJjKF3IshG7aFetZyWX4QU5D9J10WsjZv0N86wIBIwKCAQAShWEoB0l7Uq6HCKTSCZ5vUrmiLnDGMazVE7J59uHNLtppC6OqHSAlcBPZdnIO6CsdEjar7MssWR/z74uBy26kSW/mnnAT4va4K/y73gyYnUwb8ls7NV2pqQylnkt9OPBXxZ+puDZxW8vnTcuJ3u1haf52Brqq+aNOwXTjRcYAoBKGGl6slcoIxTLUiBzhsFszw7h5LU8RqqyNdzOhXasMxReqvPvinja4N6o13O82KpD/D6QsWFXtjw1ydOlsslYOmH4crG3ous1IA47PUTdu1lkUyVsHiaeYXfk4QZHdHQcJPceJBakUSiqLnG+KN+ekjc6BfKwLUvf+/VDrcNgLAoGBANI7I2iac4fzdAmpbT5nSzdX189iUZ/MJIQGysB8ZDzLawbX0r6nQkF9dVWNI0MH4DFhe9jH4rx/ARf+cwwqVAAqPyg6midMnkUuTnBbjDjP7Ndv7C0lCGosuzhsKchO3BoO5SngPtlQ8mgS9B8XmUzWktJfTE0GTRHmwUR6yaybAoGBAMVXJEEHv3018h60xM7UsqJCVcPbyJhMS/qNvAuLGlDFSdn3DMXINavdCPfVGe7nLsW0K4Qb/a52acfTmD96/VjV6xp8mNJISIwSnkuoFiSytTQzwvNr465FBZLmOrNuV96OTyNJaGlU9tAr4esE6OIS+lb0/xWLOmh83xE4Xm3xAoGAGAbCN9cjJXrosKWmFcKpgqr0F7N3CvLCWDtKXyQotn2l1OV3K7tYB3wNaNzuFkoK/lRIqw+HoIOLGK9dmv2F8WPp9fgRnhdii5BDeo4ep2g4UyK75+b5pbv4I7SXD5P75biPN/xe9EPD7qMUldbPsQKUb9BgfdTVm6VX6pGwpgMCgYEAv7O80XU9rNVRmivyYoV6VHr0Opr9YL8lPImvW6uzKeQ5HOFc3WNnVn7yxN2kKefkSwbLL+CtSmRmwh4BnMCeVkwmNvyxtlTYwpW+WB+ge2vF+DJIWi5K484FaiF62jCtIVcLDFXwZlKJXIJmb0aZFiEQcbrL6QroSD7KELpqXCsCgYBV4VwN3IEJHWyfS7dcDWS5aIsE4E8s/knL+HXnFC21mAf2dBSjg/Q20o55QS+1AGeUOeNj/dcM7C79FFkGAXVS04MIugawQ2jar6HziMEFqOv7bYEb/an/jiPTmYTK+hOjd1rPseEjHfHCXne/7BSpxKxv7GEF1/LWxX+LiypmFw==","utf-8");

		// String
		// key="-----BEGIN RSA PRIVATE KEY-----MIIEoQIBAAKCAQEAog8SHj/C9xN3HYuiLdQqThPYSxZaxjKoSGxZqzA3w1n3FyXYEH7ZR5StrsxmAm95Pl9eYFfxxAvXlnAEr7QIHYKTIepUrgHuy4Dja9buN2BZ9IieRhLzjIcuqSkUh7I3AAE1DQvcX+M4J+i09l6dFF9yiLrhWAhU8Ry+xKKEhXo5p65mCFGs9iOlIdkJ8sT3nx4hYmar8wXkalnPy3ImQHFv5NUgbmBMJmVeuYpfCQNrzXA5Z+Va1A6Ve4pGHGnJ/+CSMhe7SUlVdwvdmakCjHWXGZD9EuCS5vcP/qI4C/H2LhDfGJjKF3IshG7aFetZyWX4QU5D9J10WsjZv0N86wIBIwKCAQAShWEoB0l7Uq6HCKTSCZ5vUrmiLnDGMazVE7J59uHNLtppC6OqHSAlcBPZdnIO6CsdEjar7MssWR/z74uBy26kSW/mnnAT4va4K/y73gyYnUwb8ls7NV2pqQylnkt9OPBXxZ+puDZxW8vnTcuJ3u1haf52Brqq+aNOwXTjRcYAoBKGGl6slcoIxTLUiBzhsFszw7h5LU8RqqyNdzOhXasMxReqvPvinja4N6o13O82KpD/D6QsWFXtjw1ydOlsslYOmH4crG3ous1IA47PUTdu1lkUyVsHiaeYXfk4QZHdHQcJPceJBakUSiqLnG+KN+ekjc6BfKwLUvf+/VDrcNgLAoGBANI7I2iac4fzdAmpbT5nSzdX189iUZ/MJIQGysB8ZDzLawbX0r6nQkF9dVWNI0MH4DFhe9jH4rx/ARf+cwwqVAAqPyg6midMnkUuTnBbjDjP7Ndv7C0lCGosuzhsKchO3BoO5SngPtlQ8mgS9B8XmUzWktJfTE0GTRHmwUR6yaybAoGBAMVXJEEHv3018h60xM7UsqJCVcPbyJhMS/qNvAuLGlDFSdn3DMXINavdCPfVGe7nLsW0K4Qb/a52acfTmD96/VjV6xp8mNJISIwSnkuoFiSytTQzwvNr465FBZLmOrNuV96OTyNJaGlU9tAr4esE6OIS+lb0/xWLOmh83xE4Xm3xAoGAGAbCN9cjJXrosKWmFcKpgqr0F7N3CvLCWDtKXyQotn2l1OV3K7tYB3wNaNzuFkoK/lRIqw+HoIOLGK9dmv2F8WPp9fgRnhdii5BDeo4ep2g4UyK75+b5pbv4I7SXD5P75biPN/xe9EPD7qMUldbPsQKUb9BgfdTVm6VX6pGwpgMCgYEAv7O80XU9rNVRmivyYoV6VHr0Opr9YL8lPImvW6uzKeQ5HOFc3WNnVn7yxN2kKefkSwbLL+CtSmRmwh4BnMCeVkwmNvyxtlTYwpW+WB+ge2vF+DJIWi5K484FaiF62jCtIVcLDFXwZlKJXIJmb0aZFiEQcbrL6QroSD7KELpqXCsCgYBV4VwN3IEJHWyfS7dcDWS5aIsE4E8s/knL+HXnFC21mAf2dBSjg/Q20o55QS+1AGeUOeNj/dcM7C79FFkGAXVS04MIugawQ2jar6HziMEFqOv7bYEb/an/jiPTmYTK+hOjd1rPseEjHfHCXne/7BSpxKxv7GEF1/LWxX+LiypmFw==-----END RSA PRIVATE KEY-----";
		/**
		String key = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMCKIVvh6FMB50R1mUBeKR05nFTW/bHTnaAaRfR2eQ0+C31YHuGG61estWB6XzFersHYKb18BdZLb/Ra+s4vQauRGFZiFfsqc7HKrRVCfYDaPs9u5LEDRr2975sAYZiKOffgcUgwcR9Oj0JaqWI91xL8Pnl3gyVtIsLf7iD7rhlXAgMBAAECgYAqyMb+5uU8RMkCQmuKjSHvt5SQmbGIKXD2WcA/wW/GzIm7EbDTBqsXMW6ggLDUhKiqtIEZ9QxLATpgfzMKTB/4QB0A3OJ6Uw9qpKdR8x7XMPRMehZnbw6JQ7ZwbGPjqkMN0U7MlqUM0e+ufekSjUBWmSPH63yJlZGMgl3LQvdE2QJBAOkv17uzcuoJ6pmGEzUjlbWhg0LYNisAa8iOUnUkrqzn/y7mtEx4p9zqTjRAu2yy0/ck2LpNY9GuMlg+NL5vS1sCQQDTYEbtWDE1pFaZPLCMkprLQLhT3eeLQzP7d60mfQW96W3QjprlipvHj4kDssX1/RnQleivNY/JfepFcelOzBa1AkAZ44HkCOw9J5SwLr57K9Q3MhNMnIyHAaj1vzdQYh4yfB9MqbhitRKN6EV+b6FfVAtMaP7W0DjA0sIsIdvhOKH5AkBft5BGuBIIlXN1jqrv7Q9VjOgraigIwxTOAcKR1Dl+Zy8IKxtvaFXkh1XnK9RC8Sr4bnngpWOIPZGRguTAfuClAkEAoMteToYMktB3/Skb+mkL0OZrU6GPdJ2HO9XHnYlXQNdpB3v28DTXJLDYEVAhUVZ5JScuoJWpBW2H4BCoG2mBXw==";

		//String key2 = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMCKIVvh6FMB50R1mUBeKR05nFTW/bHTnaAaRfR2eQ0+C31YHuGG61estWB6XzFersHYKb18BdZLb/Ra+s4vQauRGFZiFfsqc7HKrRVCfYDaPs9u5LEDRr2975sAYZiKOffgcUgwcR9Oj0JaqWI91xL8Pnl3gyVtIsLf7iD7rhlXAgMBAAECgYAqyMb+5uU8RMkCQmuKjSHvt5SQmbGIKXD2WcA/wW/GzIm7EbDTBqsXMW6ggLDUhKiqtIEZ9QxLATpgfzMKTB/4QB0A3OJ6Uw9qpKdR8x7XMPRMehZnbw6JQ7ZwbGPjqkMN0U7MlqUM0e+ufekSjUBWmSPH63yJlZGMgl3LQvdE2QJBAOkv17uzcuoJ6pmGEzUjlbWhg0LYNisAa8iOUnUkrqzn/y7mtEx4p9zqTjRAu2yy0/ck2LpNY9GuMlg+NL5vS1sCQQDTYEbtWDE1pFaZPLCMkprLQLhT3eeLQzP7d60mfQW96W3QjprlipvHj4kDssX1/RnQleivNY/JfepFcelOzBa1AkAZ44HkCOw9J5SwLr57K9Q3MhNMnIyHAaj1vzdQYh4yfB9MqbhitRKN6EV+b6FfVAtMaP7W0DjA0sIsIdvhOKH5AkBft5BGuBIIlXN1jqrv7Q9VjOgraigIwxTOAcKR1Dl+Zy8IKxtvaFXkh1XnK9RC8Sr4bnngpWOIPZGRguTAfuClAkEAoMteToYMktB3/Skb+mkL0OZrU6GPdJ2HO9XHnYlXQNdpB3v28DTXJLDYEVAhUVZ5JScuoJWpBW2H4BCoG2mBXw==";

		// String
		// key2="MIICXAIBAAKBgQDAiiFb4ehTAedEdZlAXikdOZxU1v2x052gGkX0dnkNPgt9WB7hhutXrLVgel8xXq7B2Cm9fAXWS2/0WvrOL0GrkRhWYhX7KnOxyq0VQn2A2j7PbuSxA0a9ve+bAGGYijn34HFIMHEfTo9CWqliPdcS/D55d4MlbSLC3+4g+64ZVwIDAQABAoGAKsjG/ublPETJAkJrio0h77eUkJmxiClw9lnAP8FvxsyJuxGw0warFzFuoICw1ISoqrSBGfUMSwE6YH8zCkwf+EAdANzielMPaqSnUfMe1zD0THoWZ28OiUO2cGxj46pDDdFOzJalDNHvrn3pEo1AVpkjx+t8iZWRjIJdy0L3RNkCQQDpL9e7s3LqCeqZhhM1I5W1oYNC2DYrAGvIjlJ1JK6s5/8u5rRMeKfc6k40QLtsstP3JNi6TWPRrjJYPjS+b0tbAkEA02BG7VgxNaRWmTywjJKay0C4U93ni0Mz+3etJn0Fvelt0I6a5Yqbx4+JA7LF9f0Z0JXorzWPyX3qRXHpTswWtQJAGeOB5AjsPSeUsC6+eyvUNzITTJyMhwGo9b83UGIeMnwfTKm4YrUSjehFfm+hX1QLTGj+1tA4wNLCLCHb4Tih+QJAX7eQRrgSCJVzdY6q7+0PVYzoK2ooCMMUzgHCkdQ5fmcvCCsbb2hV5IdV5yvUQvEq+G554KVjiD2RkYLkwH7gpQJBAKDLXk6GDJLQd/0pG/ppC9Dma1Ohj3SdhzvVx52JV0DXaQd79vA01ySw2BFQIVFWeSUnLqCVqQVth+AQqBtpgV8=";

		String orderInfo = "partner=\"2088511816887140\"&seller_id=\"weigongshe@52yungo.com\"&out_trade_no=\"08191bhkjk000\"&subject=\"《暗黑破 坏神 3:凯恩之书》\"&body=\"暴雪唯一官方授权中文版!玩家必藏!附赠暗黑精致手绘地图!绝不仅仅 是一本暗黑的故事或画册,而是一个栩栩如生的游戏再现。是游戏玩家珍藏的首选。 \"&total_fee=\"0.02\"&notify_url=\"http%3A%2F%2Fnotify.msp.hk%2Fnotify.htm\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&show_url=\"m.alipay.com\"&success=\"true\"";

		//orderInfo = "partner=\"2088511816887140\"&seller_id=\"weigongshe@52yungo.com\"&out_trade_no=\"0819145412-6177\"&subject=\"《暗黑破 坏神 3:凯恩之书》\"&body=\"暴雪唯一官方授权中文版!玩家必藏!附赠暗黑精致手绘地图!绝不仅仅 是一本暗黑的故事或画册,而是一个栩栩如生的游戏再现。是游戏玩家珍藏的首选。 \"&total_fee=\"0.02\"&notify_url=\"http%3A%2F%2Fnotify.msp.hk%2Fnotify.htm\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&show_url=\"m.alipay.com\"&success=\"true\"";
		
		String sReturn = sign(orderInfo, key, "UTF-8");

		String sOut = orderInfo + "&sign=\"" + URLEncoder.encode(sReturn)
				+ "\"&sign_type=\"RSA\"";

		System.out.print(sOut.replace("\"", "\\\""));

		*/
		
		/*
		 * byte[] keyBytes;
		 * 
		 * keyBytes = Base64.decode(key);
		 * 
		 * PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		 * 
		 * KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		 * 
		 * PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		 */

		// String valueStr = new
		// String("8qt1cg5ncj1i471lm6i3gb7zrxq33zmw".getBytes("ISO-8859-1"),
		// "gbk");
		// verify("1232","8qt1cg5ncj1i471lm6i3gb7zrxq33zmw",AlipayMoveConfig.input_charset,AlipayMoveConfig.input_charset);
	}
}
