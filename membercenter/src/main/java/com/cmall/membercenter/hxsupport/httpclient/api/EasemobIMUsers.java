package com.cmall.membercenter.hxsupport.httpclient.api;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmall.membercenter.hxsupport.httpclient.utils.HTTPClientUtils;
import com.cmall.membercenter.hxsupport.httpclient.vo.ClientSecretCredential;
import com.cmall.membercenter.hxsupport.httpclient.vo.Credential;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.srnpr.zapcom.baseclass.BaseClass;

/**
 * REST API Demo :用户体系集成 REST API HttpClient4.3实现
 * 
 * Doc URL: http://www.easemob.com/docs/rest/userapi
 * 
 */
public class EasemobIMUsers extends BaseClass {

	private static final Logger LOGGER = LoggerFactory.getLogger(EasemobIMUsers.class);
	private static final JsonNodeFactory factory = new JsonNodeFactory(false);

    public static void main(String[] args) throws MalformedURLException {
    	/*EasemobIMUsers users = new EasemobIMUsers();
    	users.registerUser("LHY", "123456");*/
    	URL url = new URL("https://a1.easemob.com/ihesen-appid/testappdemo/token");
    	System.out.println(url);
    }
    /**
     * 注册IM用户[单个]
     * @throws MalformedURLException 
     */
    public String registerUser(String username, String password) throws MalformedURLException {
    	URL USERS_URL = new URL(bConfig("membercenter.hx_register_token"));
    	Credential credential = new ClientSecretCredential(bConfig("membercenter.app_client_id"), bConfig("membercenter.app_client_secret"), USERS_URL);
    	ObjectNode datanode = JsonNodeFactory.instance.objectNode();
        datanode.put("username", username);
        datanode.put("password", password);
        ObjectNode createNewIMUserSingleNode = createNewIMUserSingle(datanode, credential);
        return createNewIMUserSingleNode.toString();
    }

    /**
	 * 注册IM用户[单个]
	 * 
	 * 给指定Constants.APPKEY创建一个新的用户
	 * 
	 * @param dataNode
	 * @return
	 */
	public ObjectNode createNewIMUserSingle(ObjectNode dataNode, Credential credential) {

		ObjectNode objectNode = factory.objectNode();

		// check Constants.APPKEY format
		if (!HTTPClientUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", bConfig("membercenter.appkey"))) {
			LOGGER.error("Bad format of Constants.APPKEY: " + bConfig("membercenter.appkey"));

			objectNode.put("message", "Bad format of Constants.APPKEY");

			return objectNode;
		}

		objectNode.removeAll();

		// check properties that must be provided
		if (null != dataNode && !dataNode.has("username")) {
			LOGGER.error("Property that named username must be provided .");

			objectNode.put("message", "Property that named username must be provided .");

			return objectNode;
		}
		if (null != dataNode && !dataNode.has("password")) {
			LOGGER.error("Property that named password must be provided .");

			objectNode.put("message", "Property that named password must be provided .");

			return objectNode;
		}

		try {
			URL USERS_URL = new URL(bConfig("membercenter.hx_register_users"));
		    objectNode = HTTPClientUtils.sendHTTPPostRequest(USERS_URL, credential, dataNode);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}
}