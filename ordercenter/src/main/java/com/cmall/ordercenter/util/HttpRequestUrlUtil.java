package com.cmall.ordercenter.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

import com.cmall.ordercenter.alipay.wechat.ClientRequestHandler;
/**
 * 请求Http URL
 * @author wz
 *
 */
public class HttpRequestUrlUtil {
	/**
	 * 通过流的形式传递参数
	 * @param urlString   需要请求的URL
	 * @param map   携带的参数
	 * @return
	 */
	public String requestURL(String urlString, Map map){
		try {
			ClientRequestHandler clientRequestHandler = new ClientRequestHandler();
			
			URL url = new URL(urlString);  
			URLConnection con = url.openConnection();  
			con.setDoOutput(true);  
			con.setRequestProperty("Pragma:", "no-cache");  
			con.setRequestProperty("Cache-Control", "no-cache");  
			con.setRequestProperty("Content-Type", "text/xml");  

			OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());      
			String xmlInfo = clientRequestHandler.getXmlBody(map);
			//String xmlInfo = "<xml><appid>wx2421b1c4370ec43b</appid><attach>支付测试</attach><body>JSAPI支付测试</body><mch_id>10000100</mch_id><nonce_str>1add1a30ac87aa2db72f57a2375d8fec</nonce_str><notify_url>http://wxpay.weixin.qq.com/pub_v2/pay/notify.v2.php</notify_url><openid>oUpF8uMuAJO_M2pxb1Q9zNjWeS6o</openid><out_trade_no>1415659990</out_trade_no><spbill_create_ip>14.23.150.211</spbill_create_ip><total_fee>1</total_fee><trade_type>JSAPI</trade_type><sign>0CB01533B8C1EF103065174F50BCA001</sign></xml>";
			//System.out.println(xmlInfo);  
			//out.write(new String(xmlInfo.getBytes("ISO-8859-1")));
			out.write(xmlInfo);  
			out.flush();  
			out.close();  
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream())); 
			String linenew = "";
			String line = null;  
			//微信返回信息
			for (line = br.readLine(); line != null; line = br.readLine()) {  
				linenew = linenew + line;
			}
			return linenew;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}
	/**
	 * 通过https://api.weixin.qq.com/sns/oauth2/access_token?aaa=123&bbb=321方式请求连接，并传递参数
	 * @param url
	 * @return
	 */
	public static String readContentFromGet(String url) {
		try {
			// 拼凑get请求的URL字串，使用URLEncoder.encode对特殊和不可见字符进行编码
			String getURL = url;
			URL getUrl = new URL(getURL);
			// 根据拼凑的URL，打开连接，URL.openConnection函数会根据URL的类型，
			// 返回不同的URLConnection子类的对象，这里URL是一个http，因此实际返回的是HttpURLConnection
			HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
			// 进行连接，但是实际上get request要在下一句的connection.getInputStream()函数中才会真正发到
			// 服务器
			connection.connect();
			// 取得输入流，并使用Reader读取
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));// 设置编码,否则中文乱码
			//System.out.println("=============================");
			//System.out.println("Contents of get request");
			//System.out.println("=============================");
			String lines;
			StringBuffer sb = new StringBuffer();
			while ((lines = reader.readLine()) != null) {
//				lines = new String(lines.getBytes(), "utf-8");
				sb.append(lines);
//				System.out.println(lines);
			}
//			String lines = reader.readLine();
			reader.close();
			// 断开连接
			connection.disconnect();
			//System.out.println(sb.toString());
			return sb.toString();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void main(String[] args) throws Exception {
		readContentFromGet("https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx7c73f526ee2324e8&secret=bfe578412da6850d98c2defb555cc2a6&code=00159d0a5a36c0eef80766f002fb759s&grant_type=authorization_code");
	}
}
