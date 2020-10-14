package com.cmall.groupcenter.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class HttpProxy {

  // 连接超时
  private static int connectTimeOut = 5000;

  // 读取数据超时
  private static int readTimeOut = 10000;

  // 请求编码
  private static String requestEncoding = "UTF-8";

  /**
   * <pre>
   * 发送带参数的GET的HTTP请求
   * </pre>
   * 
   * @param reqUrl
   *            HTTP请求URL
   * @param parameters
   *            参数映射表
   * @return HTTP响应的字符串
   */
  public static String doGet(String reqUrl, Map parameters) {
    HttpURLConnection url_con = null;
    String responseContent = null;
    try {
      StringBuffer params = new StringBuffer();
      for (Iterator iter = parameters.entrySet().iterator(); iter.hasNext();) {
        Entry element = (Entry) iter.next();
        params.append(element.getKey().toString());
        params.append("=");
        params.append(URLEncoder.encode(element.getValue().toString(), HttpProxy.requestEncoding));
        params.append("&");
      }

      if (params.length() > 0) {
        params = params.deleteCharAt(params.length() - 1);
      }

      URL url = new URL(reqUrl);
      url_con = (HttpURLConnection) url.openConnection();
      url_con.setRequestMethod("GET");
      // System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(TestHttpRequest.connectTimeOut));// （单位：毫秒）jdk1.4换成这个,连接超时
      // System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(TestHttpRequest.readTimeOut)); // （单位：毫秒）jdk1.4换成这个,读操作超时
      url_con.setConnectTimeout(getConnectTimeOut());	//（单位：毫秒）jdk 1.5换成这个,连接超时
      url_con.setReadTimeout(getReadTimeOut());		//（单位：毫秒）jdk 1.5换成这个,读操作超时
      url_con.setDoOutput(true);
      byte[] b = params.toString().getBytes();
      url_con.getOutputStream().write(b, 0, b.length);
      url_con.getOutputStream().flush();
      url_con.getOutputStream().close();

      InputStream in = url_con.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(in, getRequestEncoding()));
      String tempLine = rd.readLine();
      StringBuffer temp = new StringBuffer();
      String crlf = System.getProperty("line.separator");
      while (tempLine != null) {
        temp.append(tempLine);
        temp.append(crlf);
        tempLine = rd.readLine();
      }
      responseContent = temp.toString();
      rd.close();
      in.close();
    } catch (IOException e) {
      System.err.println("网络故障");
      e.printStackTrace();
    } finally {
      if (url_con != null) {
        url_con.disconnect();
      }
    }

    return responseContent;
  }

  /**
   * <pre>
   * 发送不带参数的GET的HTTP请求
   * </pre>
   * 
   * @param reqUrl
   *            HTTP请求URL
   * @return HTTP响应的字符串
   */
  public static String doGet(String reqUrl) {
    HttpURLConnection url_con = null;
    String responseContent = null;
    try {
      StringBuffer params = new StringBuffer();
      String queryUrl = reqUrl;

      URL url = new URL(queryUrl);
      url_con = (HttpURLConnection) url.openConnection();
      url_con.setRequestMethod("GET");
      // System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(TestHttpRequest.connectTimeOut));// （单位：毫秒）jdk1.4换成这个,连接超时
      // System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(TestHttpRequest.readTimeOut)); // （单位：毫秒）jdk1.4换成这个,读操作超时
      url_con.setConnectTimeout(getConnectTimeOut());		//（单位：毫秒）jdk 1.5换成这个,连接超时
      url_con.setReadTimeout(getReadTimeOut());			//（单位：毫秒）jdk 1.5换成这个,读操作超时
      url_con.setDoOutput(true);
      byte[] b = params.toString().getBytes();
      url_con.getOutputStream().write(b, 0, b.length);
      url_con.getOutputStream().flush();
      url_con.getOutputStream().close();
      InputStream in = url_con.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(in, getRequestEncoding()));
      String tempLine = rd.readLine();
      StringBuffer temp = new StringBuffer();
      String crlf = System.getProperty("line.separator");
      while (tempLine != null) {
        temp.append(tempLine);
        temp.append(crlf);
        tempLine = rd.readLine();
      }
      responseContent = temp.toString();
      rd.close();
      in.close();
    } catch (IOException e) {
      System.err.println("网络故障");
      e.printStackTrace();
    } finally {
      if (url_con != null) {
        url_con.disconnect();
      }
    }

    return responseContent;
  }

  /**
   * <pre>
   * 发送带参数的POST的HTTP请求
   * </pre>
   * 
   * @param reqUrl
   *            HTTP请求URL
   * @param parameters
   *            参数映射表
   * @return HTTP响应的字符串
   */
  public static String doPost(String reqUrl, Map parameters, String verifyString) {
    HttpURLConnection url_con = null;
    String responseContent = null;
    try {
      StringBuffer params = new StringBuffer();
      for (Iterator iter = parameters.entrySet().iterator(); iter.hasNext();) {
        Entry element = (Entry) iter.next();
        params.append(element.getKey().toString());
        params.append("=");
        params.append(URLEncoder.encode(element.getValue().toString(),
            HttpProxy.requestEncoding));
        params.append("&");
      }

      if (params.length() > 0) {
        params = params.deleteCharAt(params.length() - 1);
      }

      URL url = new URL(reqUrl);
      url_con = (HttpURLConnection) url.openConnection();
      url_con.setRequestProperty("Authorization", verifyString);
      url_con.setRequestMethod("POST");
      // System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(TestHttpRequest.connectTimeOut)); // （单位：毫秒）jdk1.4换成这个,连接超时
      // System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(TestHttpRequest.readTimeOut)); // （单位：毫秒）jdk1.4换成这个,读操作超时
      url_con.setConnectTimeout(getConnectTimeOut());	// （单位：毫秒）jdk1.5换成这个,连接超时
      url_con.setReadTimeout(getReadTimeOut());		// （单位：毫秒）jdk 1.5换成这个,读操作超时
      url_con.setDoOutput(true);
      byte[] b = params.toString().getBytes();
      url_con.getOutputStream().write(b, 0, b.length);
      url_con.getOutputStream().flush();
      url_con.getOutputStream().close();

      InputStream in = url_con.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(in, getRequestEncoding()));
      String tempLine = rd.readLine();
      StringBuffer tempStr = new StringBuffer();
      String crlf = System.getProperty("line.separator");
      while (tempLine != null) {
        tempStr.append(tempLine);
        tempStr.append(crlf);
        tempLine = rd.readLine();
      }
      responseContent = tempStr.toString();
      rd.close();
      in.close();
    } catch (IOException e) {
      System.err.println("网络故障");
      e.printStackTrace();
    } finally {
      if (url_con != null) {
        url_con.disconnect();
      }
    }
    return responseContent;
  }

  public static String doPost(String reqUrl, Map parameters) {
    HttpURLConnection url_con = null;
    String responseContent = null;
    try {
      StringBuffer params = new StringBuffer();
      for (Iterator iter = parameters.entrySet().iterator(); iter.hasNext();) {
        Entry element = (Entry) iter.next();
        params.append(element.getKey().toString());
        params.append("=");
        params.append(URLEncoder.encode(element.getValue().toString(), HttpProxy.requestEncoding));
        params.append("&");
      }

      if (params.length() > 0) {
        params = params.deleteCharAt(params.length() - 1);
      }

      URL url = new URL(reqUrl);
      url_con = (HttpURLConnection) url.openConnection();
      url_con.setRequestMethod("POST");
      // System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(TestHttpRequest.connectTimeOut));	// （单位：毫秒）jdk1.4换成这个,连接超时
      // System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(TestHttpRequest.readTimeOut)); // （单位：毫秒）jdk1.4换成这个,读操作超时
      url_con.setConnectTimeout(getConnectTimeOut());		// （单位：毫秒）jdk1.5换成这个,连接超时
      url_con.setReadTimeout(getConnectTimeOut());			// （单位：毫秒）jdk 1.5换成这个,读操作超时
      url_con.setDoOutput(true);
      byte[] b = params.toString().getBytes();
      url_con.getOutputStream().write(b, 0, b.length);
      url_con.getOutputStream().flush();
      url_con.getOutputStream().close();

      InputStream in = url_con.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(in, getRequestEncoding()));
      String tempLine = rd.readLine();
      StringBuffer tempStr = new StringBuffer();
      String crlf = System.getProperty("line.separator");
      while (tempLine != null) {
        tempStr.append(tempLine);
        tempStr.append(crlf);
        tempLine = rd.readLine();
      }
      responseContent = tempStr.toString();
      rd.close();
      in.close();
    } catch (IOException e) {
      System.err.println("网络故障");
      e.printStackTrace();
    } finally {
      if (url_con != null) {
        url_con.disconnect();
      }
    }
    return responseContent;
  }

  /**
   * @return 连接超时(毫秒)
   * @see com.cmall.groupcenter.util.HttpProxy.common.web.TestHttpRequest#connectTimeOut
   */
  public static int getConnectTimeOut() {
    return HttpProxy.connectTimeOut;
  }

  /**
   * @return 读取数据超时(毫秒)
   * @see com.cmall.groupcenter.util.HttpProxy.common.web.TestHttpRequest#readTimeOut
   */
  public static int getReadTimeOut() {
    return HttpProxy.readTimeOut;
  }

  /**
   * @return 请求编码
   * @see com.cmall.groupcenter.util.HttpProxy.common.web.TestHttpRequest#requestEncoding
   */
  public static String getRequestEncoding() {
    return requestEncoding;
  }

  /**
   * @param connectTimeOut
   *            连接超时(毫秒)
   * @see com.cmall.groupcenter.util.HttpProxy.common.web.TestHttpRequest#connectTimeOut
   */
  public static void setConnectTimeOut(int connectTimeOut) {
    HttpProxy.connectTimeOut = connectTimeOut;
  }

  /**
   * @param readTimeOut
   *            读取数据超时(毫秒)
   * @see com.cmall.groupcenter.util.HttpProxy.common.web.TestHttpRequest#readTimeOut
   */
  public static void setReadTimeOut(int readTimeOut) {
    HttpProxy.readTimeOut = readTimeOut;
  }

  /**
   * @param requestEncoding
   *            请求编码
   * @see com.cmall.groupcenter.util.HttpProxy.common.web.TestHttpRequest#requestEncoding
   */
  public static void setRequestEncoding(String requestEncoding) {
    HttpProxy.requestEncoding = requestEncoding;
  }

  /**
   * 测试语音验证码接口
   */
//  @Test
  public void test(){
	  /*Map map = new HashMap();
      map.put("account", "Z18610436326");//平台账号
      map.put("password", "955933");//平台密码
      map.put("template", "100096");//模板编号
      map.put("phone", "13552143112,18810908829");//phone
      map.put("var", "{\"code\":\"123456\"}");
      map.put("smstype", 2);//短信类型    1是普通验证码短信 2是语音验证码短信      3会员短信      4国际验证码短信

      String temp = HttpProxy.doGet("http://222.73.117.140:8044/mt", map);
      System.out.println("返回的消息是:" + temp);*/
	   
	  
	  
	  //http://222.73.117.158/msg/HttpBatchSendSM?account=yuyin-clcs-01&pswd=Tch123456&mobile=18610436326&msg=
	  /*Map map = new HashMap();
      map.put("account", "yuyin-clcs-01");//平台账号
      map.put("pswd", "Tch123456");//平台密码
      map.put("mobile", "13552143112,18810908829");//mobile 
      map.put("msg", "您的验证码是635986");
      map.put("needstatus", true);//短信类型    1是普通验证码短信 2是语音验证码短信      3会员短信      4国际验证码短信

      String temp = HttpProxy.doGet("http://222.73.117.158/msg/HttpBatchSendSM", map);
      System.out.println("返回的消息是:" + temp);*/
	  /**
	   * 
1234567890100

	   */
	  System.out.println("20110725160412,0\n1234567890100");
  }

}