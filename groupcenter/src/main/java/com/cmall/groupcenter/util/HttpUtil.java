package com.cmall.groupcenter.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.WebClientRequest;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapcom.topdo.TopConst;
import com.srnpr.zapcom.topdo.TopDir;


/**
 * 使用HttpClient的jar,发送http请求
 * 
 * @author zhanghao
 * 
 */
public class HttpUtil extends BaseClass{

	/**
	 * 发送HTTP post请求
	 * 
	 * @param url
	 *            请求的地址
	 * @param data
	 *            发送的数据(现阶段的数据格式为json格式的字符串)
	 * @param charsetName
	 *            字符集 默认UTF-8
	 * @return http响应返回的字符串
	 */
	public static String post(String url, String data, String charsetName) {

		HttpUtil hu = new HttpUtil();
		String result;
		try {
			result = hu.getHttps(url,data);
			return result;
		} catch (KeyManagementException | ParseException
				| NoSuchAlgorithmException | KeyStoreException
				| CertificateException | IOException e) {
			e.printStackTrace();
		}
		System.out.println("Data:"+data);
		return null;
	}

	/**
	 * 包装返回结果
	 * 
	 * @param callResult
	 * @return
	 */
	public static String createOutResult(String callResult) {
		StringBuffer reVol = new StringBuffer();

		reVol.append("{\"success\":false,\"message\":\"");
		reVol.append(callResult);
		reVol.append("\"}");
		return reVol.toString();
	}

	public static String xmlElements(String xmlDoc) {
        //创建一个新的字符串
        StringReader read = new StringReader(xmlDoc);
        //创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
        InputSource source = new InputSource(read);
        //创建一个新的SAXBuilder
        SAXBuilder sb = new SAXBuilder();
        String linkvalue = "";
        try {
            //通过输入源构造一个Document
            Document doc = sb.build(source);
            //取的根元素
            Element root = doc.getRootElement();
            System.out.println(root.getName());//输出根元素的名称（测试）
            //得到根元素所有子元素的集合
            List jiedian = root.getChildren();
            //获得XML中的命名空间（XML中未定义可不写）
            Namespace ns = root.getNamespace();
            Element et = null;            
            for(int i=0;i<jiedian.size();i++){
                et = (Element) jiedian.get(i);//循环依次得到子元素
                if(linkvalue.equals("")){
                	linkvalue = et.getText();
                }
                if(et.getAttribute("quality").getValue().equals("20")){
                	linkvalue = et.getText();
                }
               
                System.out.println(et.getText());
            }          
        } catch (JDOMException e) {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        } catch (IOException e) {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        }
        return linkvalue;
    }
	
	/**
     * post json数据
     * @param url
     * @param jsonStr
     * @return
     */
    public static String postJson(String url, String jsonStr) {
    	HttpUtil hu = new HttpUtil();
		String result;
		try {
			result = hu.getHttps(url,jsonStr);
			return result;
		} catch (KeyManagementException | ParseException
				| NoSuchAlgorithmException | KeyStoreException
				| CertificateException | IOException e) {
			e.printStackTrace();
		}
		System.out.println("Data:"+jsonStr);
		return null;
    }
    
    /**
	 * 调用处理逻辑 返回操作
	 * 
	 * @param sRequestString
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 */
	private String getHttps(String sUrl, String sRequestString)
			throws ParseException, IOException, KeyManagementException,
			NoSuchAlgorithmException, KeyStoreException, CertificateException {
		WebClientRequest webClientRequest = new WebClientRequest();

		String sDir = bConfig("groupcenter.homehas_key");

		if (StringUtils.isEmpty(sDir)) {
			TopDir topDir = new TopDir();
			sDir = topDir.upCustomPath("") + "tomcat.keystore";
		}

		webClientRequest.setFilePath(sDir);
		webClientRequest.setUrl(sUrl);

		HttpEntity httpEntity = new StringEntity(sRequestString,
				TopConst.CONST_BASE_ENCODING);

		webClientRequest.setConentType("application/json");

		webClientRequest
				.setPassword(bConfig("groupcenter.rsync_homehas_password"));

		bLogInfo(0, EntityUtils.toString(httpEntity));

		webClientRequest.setHttpEntity(httpEntity);

		String sResponseString = WebClientSupport.upHttpsPost(webClientRequest);

		return sResponseString;
	}
    private static String execute(CloseableHttpClient httpClient, HttpUriRequest httpGetOrPost) {
        String res = null;
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGetOrPost);
            HttpEntity entity = response.getEntity();
            res = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            doResponseClose(response);
        }
        return res;
    }
    
    private static void doResponseClose(CloseableHttpResponse response) {
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void doHttpClientClose(CloseableHttpClient httpClient) {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
