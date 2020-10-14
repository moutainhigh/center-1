package com.cmall.groupcenter.weixin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.cmall.groupcenter.weixin.model.Article;
import com.cmall.groupcenter.weixin.model.TextMessage;
import com.cmall.groupcenter.weixin.model.UserBindInfo;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class WeiXinUtil extends BaseClass {

	private static Logger log = Logger.getLogger(WeiXinUtil.class);

	/**
	 * 发起https请求并获取结果
	 * 
	 * @param requestUrl
	 * @param requestMethod
	 * @param outputStr
	 * @return
	 */
	public static JSONObject httpsRequest(String requestUrl,
			String requestMethod, String outputStr) {
		JSONObject jsonObject = null;
		StringBuffer buffer = new StringBuffer();
		log.fatal("requestUrl=" + requestUrl + ",requestMethod="
				+ requestMethod + ",outputStr=" + outputStr);
		try {
			log.fatal(requestUrl);
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url
					.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);

			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			httpUrlConn.setRequestMethod(requestMethod);

			if ("GET".equalsIgnoreCase(requestMethod))
				httpUrlConn.connect();

			// 当有数据需要提交时
			if (null != outputStr) {
				OutputStream outputStream = httpUrlConn.getOutputStream();
				// 注意编码格式，防止中文乱码
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}

			// 将返回的输入流转换成字符串
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();
			jsonObject = new JSONObject(buffer.toString()); //.fromObject(buffer.toString());
		} catch (ConnectException ce) {
			log.error("Weixin server connection timed out.");
		} catch (Exception e) {
			log.error("https request error:{}", e);
		}
		return jsonObject;
	}
	
	public boolean checkSignature(String signature, String timestamp,
			String nonce) {
		String[] arr = new String[] {bConfig("groupcenter.appToken"), timestamp, nonce };  
        // 将token、timestamp、nonce三个参数进行字典序排序  
        Arrays.sort(arr);  
        StringBuilder content = new StringBuilder();  
        for (int i = 0; i < arr.length; i++) {  
            content.append(arr[i]);  
        }  
        MessageDigest md = null;  
        String tmpStr = null;  
  
        try {  
            md = MessageDigest.getInstance("SHA-1");  
            // 将三个参数字符串拼接成一个字符串进行sha1加密  
            byte[] digest = md.digest(content.toString().getBytes());  
            tmpStr = byteToStr(digest);  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
  
        content = null;  
        // 将sha1加密后的字符串可与signature对比，标识该请求来源于微信  
        return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;  
	}
	
	/** 
     * 将字节数组转换为十六进制字符串 
     *  
     * @param byteArray 
     * @return 
     */  
    private static String byteToStr(byte[] byteArray) {  
        String strDigest = "";  
        for (int i = 0; i < byteArray.length; i++) {  
            strDigest += byteToHexStr(byteArray[i]);  
        }  
        return strDigest;  
    }  
  
    /** 
     * 将字节转换为十六进制字符串 
     *  
     * @param mByte 
     * @return 
     */  
    private static String byteToHexStr(byte mByte) {  
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };  
        char[] tempArr = new char[2];  
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];  
        tempArr[1] = Digit[mByte & 0X0F];  
  
        String s = new String(tempArr);  
        return s;  
    }

	public  boolean sendMessage(String openId, String content,
			String access_token) {

		String jsonMsg = WeiXinUtil.makeTextCustomMessage(openId, content);
		return sendCustomMessage(jsonMsg);

	}

	/**
	 * 组装文本客服消息
	 */
	public static String makeTextCustomMessage(String openId, String content) {
		// 对消息内容中的双引号转义
		content = content.replace("\"", "\\\"");
		String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"text\",\"text\":{\"content\":\"%s\"}}";
		return String.format(jsonMsg, openId, content);
	}

	public static String makeArticleMessage(String openId, Article arc) {
		String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"news\",\"news\":{\"articles\": [{\"title\":\"%s\",\"description\":\"%s\",\"url\":\"%s\",\"picurl\":\"%s\"}]}}";
		// System.out.println(jsonMsg.toString());
		return String.format(jsonMsg, openId, arc.getTitle(),
				arc.getDescription(), arc.getUrl(), arc.getPicUrl());
	}

	/**
	 * 发送客服消息
	 */

	public boolean sendCustomMessage(String jsonMsg) {
		String accessToken=findAccessToken();
		boolean result = false;
		// 拼接请求地址
		String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken);
		// 发送客服消息
		JSONObject jsonObject = WeiXinUtil.httpsRequest(requestUrl, "POST",
				jsonMsg);

		if (null != jsonObject) {
			int errorCode = jsonObject.getInt("errcode");
			String errorMsg = jsonObject.getString("errmsg");
			if (0 == errorCode) {
				result = true;
			}

		}
		return result;

	}

	// 获取access_token的接口地址（GET） 限200（次/天）
	public final static String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

	/**
	 * 获取access_token，业务模块禁止直接调用，必须通过ConsultService接口获取。
	 * 
	 * @param appid
	 *            凭证
	 * @param appsecret
	 *            密钥
	 * @return
	 */
	public String getAccessToken() {
		String accessToken = null;
		String requestUrl = access_token_url.replace("APPID", bConfig("groupcenter.appId"))
				.replace("APPSECRET", bConfig("groupcenter.appSecret"));
		JSONObject jsonObject = httpsRequest(requestUrl, "GET", null);
		// 如果请求成功
		if (null != jsonObject) {
			try {
				accessToken = jsonObject.getString("access_token");
			} catch (JSONException e) {
				accessToken = null;
				// 获取token失败
				log.error("获取token失败 errcode:{} errmsg:{}");
			}
		}
		return accessToken;
	}

	private static String oauth2Url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";

	public String getOAuthURL(String appUrl, String state) {
		String appUri =URLEncode(appUrl);
		String retUrl = oauth2Url;
		return retUrl.replace("APPID", bConfig("groupcenter.appId"))
				.replace("REDIRECT_URI", appUri).replace("STATE", state);
	}


	/**
	 * 通过网页授权获取用户信息
	 * 
	 * @param accessToken
	 * @param openId
	 * @return
	 */
	public  UserBindInfo getUserInfo(String openId) {
		UserBindInfo user = null;
		String accessToken=findAccessToken();
		// 拼接请求地址
		String requestUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken).replace(
				"OPENID", openId);
		JSONObject jsonObject = httpsRequest(requestUrl, "GET", null);
		if (null != jsonObject) {
			try {
				user = new UserBindInfo();
				user.setOpenId(jsonObject.getString("openid"));
				user.setNickName(jsonObject.getString("nickname"));
				user.setSex(jsonObject.getInt("sex"));
				user.setCity(jsonObject.getString("city"));
				user.setCountry(jsonObject.getString("country"));
				user.setProvince(jsonObject.getString("province"));
				user.setHeadImgUrl(jsonObject.getString("headimgurl"));
			} catch (Exception e) {
				user = null;
				int errorCode = jsonObject.getInt("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				log.fatal("获取网页授权凭证失败  errcode:{" + errorCode + "} errmsg:{"
						+ errorMsg + "}");

			}
		}
		return user;
	}

	static final String OAUTH2_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";

	public String replaceToOAuthURL(String url) {
		String tmpUrl = URLEncode(url);
		return OAUTH2_URL.replace("APPID", bConfig("groupcenter.appId"))
				.replace("REDIRECT_URI", tmpUrl)
				.replace("SCOPE", "snsapi_base");
	}

	public String replaceToOAuthURLNotEncode(String url) {
		return OAUTH2_URL.replace("APPID", bConfig("groupcenter.appId"))
				.replace("REDIRECT_URI", url).replace("SCOPE", "snsapi_base");
	}
	
	public static String URLEncode(String data) {
		URLCodec codec = new URLCodec("UTF-8");
		try {
			return codec.encode(data);
		} catch (EncoderException e) {
			e.printStackTrace();
		}
		return "";
	}

//	public static String TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
//
//	// 获取jsapi_ticket（微信JS接口的临时票据）
//	public static String getTicket() {
//		//TODO
//		String requestUrl = TICKET_URL.replace("ACCESS_TOKEN",
//				"");
//		JSONObject jsonObject = httpsRequest(requestUrl, "GET", null);
//		String ticket = null;
//		if (null != jsonObject) {
//			try {
//				ticket = jsonObject.getString("ticket");
//			} catch (Exception e) {
//				ticket = null;
//				int errorCode = jsonObject.getInt("errcode");
//				String errorMsg = jsonObject.getString("errmsg");
//				log.fatal("获取JS_API ticket失败  errcode:{" + errorCode
//						+ "} errmsg:{" + errorMsg + "}");
//
//			}
//		}
//		return ticket;
//	}

	//生成jsapi签名
	public static Map<String, String> sign(String jsapi_ticket, String url) {
		Map<String, String> ret = new HashMap<String, String>();
		String nonce_str = createNonceStr();
		String timestamp = createTimeStamp();
		String string1;
		String signature = "";

		// 注意这里参数名必须全部小写，且必须有序
		string1 = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonce_str
				+ "&timestamp=" + timestamp + "&url=" + url;

		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(string1.getBytes("UTF-8"));
			signature = byteToHex(crypt.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		ret.put("url", url);
		ret.put("jsapi_ticket", jsapi_ticket);
		ret.put("nonceStr", nonce_str);
		ret.put("timestamp", timestamp);
		ret.put("signature", signature);

		return ret;
	}

	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	public static String createNonceStr() {
		return UUID.randomUUID().toString();
	}

	public static String createTimeStamp() {
		return Long.toString(System.currentTimeMillis() / 1000);
	}
	
	/*
	 <xml>
	<ToUserName><![CDATA[toUser]]></ToUserName>
	<FromUserName><![CDATA[fromUser]]></FromUserName>
	<CreateTime>12345678</CreateTime>
	<MsgType><![CDATA[text]]></MsgType>
	<Content><![CDATA[你好]]></Content>
	</xml>
	 */
	private static String TEXT_TO_XML_TEMPLATE = "<xml><ToUserName><![CDATA[%s]]></ToUserName><FromUserName><![CDATA[%s]]></FromUserName>" +
	"<CreateTime>%s</CreateTime><MsgType><![CDATA[%s]]></MsgType>	<Content><![CDATA[%s]]></Content></xml>";
	/**
	 * 组装文本XML消息
	 */
	public static String textToXml(String fromOpenId, String toOpenId, String content)
	{
		int time = (int)(System.currentTimeMillis() / 1000);
		return String.format(TEXT_TO_XML_TEMPLATE, toOpenId,fromOpenId,  ""+time, content );		
	}
	
	/*
	 <xml>	 
	<ToUserName><![CDATA[toUser]]></ToUserName>
	<FromUserName><![CDATA[fromUser]]></FromUserName>
	<CreateTime>12345678</CreateTime>
	<MsgType><![CDATA[news]]></MsgType>
	<ArticleCount>2</ArticleCount>
	<Articles>
	<item>
	<Title><![CDATA[title1]]></Title> 
	<Description><![CDATA[description1]]></Description>
	<PicUrl><![CDATA[picurl]]></PicUrl>
	<Url><![CDATA[url]]></Url>
	</item>
	<item>
	<Title><![CDATA[title]]></Title>
	<Description><![CDATA[description]]></Description>
	<PicUrl><![CDATA[picurl]]></PicUrl>
	<Url><![CDATA[url]]></Url>
	</item>
	</Articles>
	</xml>
	*/
	
	private static String ARTICLE_TO_XML_TEMPLATE = "<item><Title><![CDATA[%s]]></Title><Description><![CDATA[%s]]></Description>	<PicUrl><![CDATA[%s]]></PicUrl>	<Url><![CDATA[%s]]></Url></item>";
	
	private static String ARTICLES_TO_XML_TEMPLATE = "<xml><ToUserName><![CDATA[%s]]></ToUserName><FromUserName><![CDATA[%s]]></FromUserName>" +
			"<CreateTime>%s</CreateTime><MsgType><![CDATA[%s]]></MsgType><ArticleCount>%d</ArticleCount><Articles>%s</Articles></xml>";
	/**
	 * 组装图文XML消息
	 */
	public static String articleToXml(String fromOpenId, String toOpenId, Article article)
	{
		int time = (int)(System.currentTimeMillis() / 1000);
		String sa = String.format(ARTICLE_TO_XML_TEMPLATE, article.getTitle(), article.getDescription(), article.getPicUrl(), article.getUrl());
		return String.format(ARTICLES_TO_XML_TEMPLATE,toOpenId, fromOpenId,  ""+time, 1, sa);
	}
	
	/**
	 * 组装多图文XML消息
	 */
	public static String articlesToXml(String fromOpenId, String toOpenId, List<Article> articles)
	{
		StringBuffer sbf = new StringBuffer();
		int time = (int)(System.currentTimeMillis() / 1000);
		for(Article article : articles)
		{
			sbf.append( String.format(ARTICLE_TO_XML_TEMPLATE, article.getTitle(), article.getDescription(), article.getPicUrl(), article.getUrl()) );
		}
		return String.format(ARTICLES_TO_XML_TEMPLATE,toOpenId, fromOpenId,  ""+time, articles.size(), sbf.toString());
	}
	

	// private static final String news_message_article
	// ="{\"title\":\"%s\",\"description\":\"%s\",\"url\":\"%s\",\"picurl\":\"%s\"}";
	// private static final String news_message_template =
	// "{\"touser\":\"%s\",\"msgtype\":\"news\",\"news\":{\"articles\": []}}";

	private static final String news_message_article = "{\"title\":\"%s\",\"description\":\"%s\",\"url\":\"%s\",\"picurl\":\"%s\"}";
	private static final String news_message_template = "{\"touser\":\"%s\",\"msgtype\":\"news\",\"news\":{\"articles\": [%s]}}";

	public static String articleToJson(String openId, Article article) {
		String jsonMsg = String.format(news_message_template, openId,
				article2str(article));
		return jsonMsg;
	}

	public static String articlesToJson(String openId,
			List<Article> articles) {
		StringBuffer sb = new StringBuffer();
		for (int idx = 0; idx < articles.size(); idx++) {
			sb.append(article2str(articles.get(idx)));
			if (idx != articles.size() - 1) {
				sb.append(",");
			}
		}
		return String.format(news_message_template, openId, sb.toString());
	}

	private static String article2str(Article article) {
		return String
				.format(news_message_article, article.getTitle(),
						article.getDescription(), article.getUrl(),
						article.getPicUrl());
	}
	
	
	//获取数据库中的accessToken
	public  String findAccessToken(){
		MDataMap tokenMap=DbUp.upTable("gc_weixin_token").one("token_name","access_token");
		if(tokenMap!=null&&tokenMap.size()>0){
			Date updateTime=DateUtil.toDate(tokenMap.get("update_time"),DateUtil.DATE_FORMAT_DATETIME);
			updateTime=DateUtil.addMinute(updateTime, 120);
			if(updateTime.getTime()>new Date().getTime()){
				return tokenMap.get("token_value");
			}
		}
		updateAccessToken();
		return findAccessToken();
		
	}
	
	//更新数据库中的accessToken
	public void updateAccessToken(){
		MDataMap mDataMap =DbUp.upTable("gc_weixin_token").one("token_name","access_token");
		String accessToken=getAccessToken();
		if(mDataMap!=null&&mDataMap.size()>0){
			
			mDataMap.put("token_value", accessToken);
			mDataMap.put("update_time", DateUtil.getNowTime());
			DbUp.upTable("gc_weixin_token").update(mDataMap);
		}else{
			DbUp.upTable("gc_weixin_token").
			insert("token_name","access_token",
				"token_value",accessToken,
				"update_time",DateUtil.getNowTime());
		}
	}
	
	
	/**
	 * 直接返回文本数据的方法工具
	 * @author lipengfei
	 * @date 2015-5-21
	 * @param fromUserName
	 * @param toUserName
	 * @param message
	 * @return
	 */
	public static String toResponseTextMessage(String fromUserName,
			String toUserName,String message){
		
		String respMessage = null;
		TextMessage textMessage = new TextMessage();
		
		textMessage.setContent(message);
		textMessage.setToUserName(fromUserName);
		textMessage.setFromUserName(toUserName);
		textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
		textMessage.setCreateTime(new Date().getTime());
		respMessage = MessageUtil.textMessageToXml(textMessage);
		
		return respMessage;
	}

}
