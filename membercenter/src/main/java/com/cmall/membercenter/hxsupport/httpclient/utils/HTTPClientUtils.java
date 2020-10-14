package com.cmall.membercenter.hxsupport.httpclient.utils;

import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.cmall.membercenter.hxsupport.httpclient.vo.Credential;
import com.cmall.membercenter.hxsupport.httpclient.vo.Token;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * HTTPClient 工具类
 * 
 * @author Lynch 2014-09-15
 *
 */
@SuppressWarnings("all")
public class HTTPClientUtils {

	private static final JsonNodeFactory factory = new JsonNodeFactory(false);

	/**
	 * Send SSL Request
	 * 
	 * @return
	 */
	public static ObjectNode sendHTTPPostRequest(URL url, Credential credential, Object dataBody) {

		HttpClient httpClient = getClient(true);

		ObjectNode resObjectNode = factory.objectNode();

		try {

			HttpResponse response = null;

			HttpPost httpPost = new HttpPost(url.toURI());

			if (credential != null) {
				Token.applyAuthentication(httpPost, credential);
			}
			httpPost.setEntity(new StringEntity(dataBody.toString(), "UTF-8"));

			response = httpClient.execute(httpPost);
			
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				String responseContent = EntityUtils.toString(entity, "UTF-8");
				EntityUtils.consume(entity);

				ObjectMapper mapper = new ObjectMapper();
				JsonFactory factory = mapper.getJsonFactory();
				JsonParser jp = factory.createJsonParser(responseContent);

				resObjectNode = mapper.readTree(jp);
				resObjectNode.put("statusCode", response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}

		return resObjectNode;
	}

	/**
	 * Create a httpClient instance
	 * 
	 * @param isSSL
	 * @return HttpClient instance
	 */
	public static HttpClient getClient(boolean isSSL) {

		HttpClient httpClient = new DefaultHttpClient();
		if (isSSL) {
			X509TrustManager xtm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			try {
				SSLContext ctx = SSLContext.getInstance("TLS");

				ctx.init(null, new TrustManager[] { xtm }, null);

				SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);

				httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));

			} catch (Exception e) {
				throw new RuntimeException();
			}
		}

		return httpClient;
	}

	/**
	 * Check illegal String
	 * 
	 * @param regex
	 * @param str
	 * @return
	 */
	public static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.lookingAt();
	}
}
