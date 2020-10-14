package com.cmall.groupcenter.func;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;

public class JaxbUtil extends BaseClass {

	public static String maptoXml1(List<Map> maps) {
		StringBuffer sb = new StringBuffer();
		sb.append("<info>");

		for (Map map : maps) {
			sb.append("<orderInfo>");
			for (Object dataKey1 : map.keySet()) {
				if (map.get(dataKey1) instanceof List) {

				} else {

					sb.append("<").append(dataKey1).append(">")
							.append(map.get(dataKey1)).append("</")
							.append(dataKey1).append(">");
				}

			}

			sb.append("</orderInfo>");
		}

		for (Map map : maps) {

			for (Object dataKey : map.keySet()) {
				if (map.get(dataKey) instanceof List) {
					sb.append("<product_info>");
					if (map.get(dataKey) != null) {
						for (Object procuctMap : (List) map.get(dataKey)) {
							sb.append("<product_item>");
							Map map1 = (Map) procuctMap;
							for (Object dataKey1 : map1.keySet()) {
								sb.append("<").append(dataKey1).append(">")
										.append(map1.get(dataKey1))
										.append("</").append(dataKey1)
										.append(">");
							}
							sb.append("</product_item>");

						}
					}
					sb.append("</product_info>");

				}

			}
			break;
		}

		sb.append("</info>");

		return sb.toString();
	}

	public static String maptoXml(Map map) {
		StringBuffer sb = new StringBuffer();
		sb.append("<info>");

		sb.append("<orderInfo>");
		for (Object dataKey1 : map.keySet()) {
			if (map.get(dataKey1) instanceof List) {

			} else {

				sb.append("<").append(dataKey1).append(">")
						.append(map.get(dataKey1)).append("</")
						.append(dataKey1).append(">");
			}

		}

		sb.append("</orderInfo>");

		for (Object dataKey : map.keySet()) {
			if (map.get(dataKey) instanceof List) {
				sb.append("<product_info>");
				if (map.get(dataKey) != null) {
					for (Object procuctMap : (List) map.get(dataKey)) {
						sb.append("<product_item>");
						Map map1 = (Map) procuctMap;
						for (Object dataKey1 : map1.keySet()) {
							sb.append("<").append(dataKey1).append(">")
									.append(map1.get(dataKey1)).append("</")
									.append(dataKey1).append(">");
						}
						sb.append("</product_item>");

					}
				}
				sb.append("</product_info>");

			}

		}

		sb.append("</info>");

		return sb.toString();
	}

	public static String doc2String(Document document) {
		String s = "";
		try {
			// 使用输出流来进行转化
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			// 使用UTF-8编码
			OutputFormat format = new OutputFormat("   ", true, "UTF-8");
			XMLWriter writer = new XMLWriter(out, format);
			writer.write(document);
			s = out.toString("UTF-8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return s;
	}

	/**
	 * 
	 * URL加密
	 * 
	 * @throws UnsupportedEncodingException
	 */

	public static String encodeUri(String s)
			throws UnsupportedEncodingException {
		String str = "";
		str = URLEncoder.encode(s, "UTF-8");
		return str;
	}

	/**
	 * 
	 * 新的md5签名，首尾放secret。
	 * 
	 * @param secret
	 *            分配给您的APP_SECRET
	 */

	public static String md5Signature(
			LinkedHashMap<String, String> apiparamsMap, String secret) {

		String result = null;

		StringBuffer orgin = getBeforeSign(apiparamsMap, new StringBuffer(
				secret));

		if (orgin == null)

			return result;

		// orgin.append(secret);

		try {

			MessageDigest md = MessageDigest.getInstance("MD5");

			result = byte2hex(md.digest(orgin.toString().getBytes("utf-8")));

		} catch (Exception e) {

			throw new java.lang.RuntimeException("sign error !");

		}

		return result;

	}

	/**
	 * 
	 * 二行制转字符串
	 */

	private static String byte2hex(byte[] b) {

		StringBuffer hs = new StringBuffer();

		String stmp = "";

		for (int n = 0; n < b.length; n++) {

			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));

			if (stmp.length() == 1)

				hs.append("0").append(stmp);

			else

				hs.append(stmp);

		}

		return hs.toString().toUpperCase();

	}

	/**
	 * 
	 * 添加参数的封装方法
	 */

	private static StringBuffer getBeforeSign(
			LinkedHashMap<String, String> apiparamsMap, StringBuffer orgin) {

		if (apiparamsMap == null)

			return null;

		Map<String, String> treeMap = new TreeMap<String, String>();

		treeMap.putAll(apiparamsMap);

		Iterator<String> iter = treeMap.keySet().iterator();
		while (iter.hasNext()) {

			String name = (String) iter.next();

			orgin.append(name).append(apiparamsMap.get(name));

		}
		return orgin;

	}

	/** 连接到TOP服务器并获取数据 */

	public static String getResult(String urlStr, String content) {

		URL url = null;

		HttpURLConnection connection = null;

		try {

			url = new URL(urlStr);

			connection = (HttpURLConnection) url.openConnection();

			connection.setDoOutput(true);

			connection.setDoInput(true);

			connection.setRequestMethod("POST");

			connection.setUseCaches(false);

			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			connection.connect();

			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream());

			out.write(content.getBytes("utf-8"));

			out.flush();

			out.close();

			BufferedReader reader =

			new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "utf-8"));

			StringBuffer buffer = new StringBuffer();

			String line = "";

			while ((line = reader.readLine()) != null) {

				buffer.append(line);

			}

			reader.close();

			return buffer.toString();

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			if (connection != null) {

				connection.disconnect();

			}

		}

		return null;

	}

	public List<String> sing(String shopId, OrderInformation order) {

		String orderListXML = "";

		String sign = "";

		List<String> mapList = new ArrayList<String>();

		MDataMap map = new MDataMap();

		LinkedHashMap<String, String> apiparamsMap = new LinkedHashMap<String, String>();

		apiparamsMap.put("dbhost", bConfig("groupcenter.dbhost"));// 添加请求参数——主帐号

		apiparamsMap.put("appkey", bConfig("groupcenter.appkey"));// 添加请求参数——appkey

		apiparamsMap.put("method", "edbTradeAdd");// 添加请求参数——接口名称

		apiparamsMap.put("format", bConfig("groupcenter.format"));// 添加请求参数——返回格式

		apiparamsMap.put("fields", bConfig("groupcenter.fields"));

		apiparamsMap.put("v", bConfig("groupcenter.v"));// 添加请求参数——版本号（目前只提供2.0版本）

		apiparamsMap.put("slencry", "0");// 添加请求参数——返回结果是否加密（0，为不加密 ，1.加密）

		apiparamsMap.put("ip", bConfig("groupcenter.ip"));// 添加请求参数——IP地址

		apiparamsMap.put("appscret", bConfig("groupcenter.secret"));// 添加请求参数——appscret

		apiparamsMap.put("token", bConfig("groupcenter.token"));// 添加请求参数——token

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String timestamp = sdf.format(new Date());

		apiparamsMap.put("timestamp",
				timestamp.replace("-", "").replace(":", "").replace(" ", "")
						.substring(0, 12));// 添加请求参数——时间戳

		try {

			Map<String, Object> xml = bean2Map(order);

			orderListXML = JaxbUtil.maptoXml(xml);

			apiparamsMap.put("xmlValues", orderListXML);

			sign = JaxbUtil.md5Signature(apiparamsMap,
					bConfig("groupcenter.appkey"));

			mapList.add(sign);

			mapList.add(apiparamsMap.get("timestamp"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mapList;
	}

	@SuppressWarnings({ "unused", "unchecked" })
	private static Map<String, Object> bean2Map(Object obj) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		if (obj != null) {
			Class c = obj.getClass();
			Field[] fs = c.getDeclaredFields();
			for (Field field : fs) {
				String fieldName = field.getName();
				String getname = "get"
						+ fieldName.substring(0, 1).toUpperCase()
						+ fieldName.substring(1);
				Object value = c.getMethod(getname, null).invoke(obj, null);

				if (value instanceof Collection) {
					List<Object> list = new ArrayList<Object>();
					for (Object o : (Collection) value) {
						list.add(bean2Map(o));
					}
					value = list;
				}

				map.put(fieldName, value);
			}
		}
		return map;
	}

}
