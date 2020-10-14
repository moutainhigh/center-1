package com.cmall.groupcenter.voice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * @author panwei
 */
public class VoiceSender extends BaseClass{
	
	private CloseableHttpClient client;
	private String account;
	private String password;
	private static final String SEND_URL="http://222.73.117.138:7891/mt";
	private static final String QUERY_URL="http://222.73.117.138:7891/bi";
	private static final String INTERNATIONAL_URL="http://222.73.117.140:8044/mt";

	public VoiceSender(){
		this.account =bConfig("groupcenter.wgs_voice_code_account");
		this.password =bConfig("groupcenter.wgs_voice_code_pswd");
		client = HttpClients.createDefault();
	}
	
	/**
	 * 发送国内短信
	 * @param phone
	 * @param content
	 * @return
	 */
	public String sendVoiceMessage(String phone, String content){
		CloseableHttpResponse response = null;
		String resultCode = null;
		try {
			//发送短信
			response = sendMessage(phone,content);
			if(response != null && response.getStatusLine().getStatusCode()==200){
				String result=EntityUtils.toString(response.getEntity());
				JSONObject json=new JSONObject();
				json=json.parseObject(result);
				Boolean bl=(Boolean)json.get("success");
				
				MDataMap voiceMessage=new MDataMap();
				
				voiceMessage.put("msg_receive", phone);
				voiceMessage.put("send_time", FormatHelper.upDateTime());
				voiceMessage.put("create_time", FormatHelper.upDateTime());
				voiceMessage.put("msg_content", content);
				voiceMessage.put("result", result);
				
				if(bl){
					String msgid=(String)json.get("id");
					voiceMessage.put("msg_id", msgid);
					resultCode="1";
				}else{
					String r=(String)json.get("r");
					resultCode=r;
				}
				//报错发送日志
				DbUp.upTable("gc_voice_message").dataInsert(voiceMessage);
			}else{
				resultCode="0";
			}
	
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		close();
		return resultCode;
	}


	private CloseableHttpResponse sendMessage(String phone, String content) {
		String encodedContent = null;
		try {
			encodedContent = URLEncoder.encode(content, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		StringBuffer strBuf = new StringBuffer(SEND_URL);
		strBuf.append("?un=").append(account);
		strBuf.append("&pw=").append(password);
		strBuf.append("&da=").append(phone);
		strBuf.append("&sm=").append(encodedContent);
		strBuf.append("&dc=15&rd=1&rf=2&tf=3");
		HttpGet get = new HttpGet( strBuf.toString() );
		
		try {
			return client.execute(get);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查询账户余额
	 * @return
	 */
	public CloseableHttpResponse queryBalance() {
		StringBuffer strBuf = new StringBuffer(QUERY_URL);
		strBuf.append("?un=").append(account);
		strBuf.append("&pw=").append(password);
		strBuf.append("&rf=2");
		HttpGet get = new HttpGet( strBuf.toString() );
		
		try {
			return client.execute(get);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 发送国际短信
	 * @param phone
	 * @param content
	 * @return
	 */
	public CloseableHttpResponse sendInternationalMessage(String phone, String content) {
		String encodedContent = null;
		try {
			encodedContent = URLEncoder.encode(content, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		StringBuffer strBuf = new StringBuffer(INTERNATIONAL_URL);
		strBuf.append("?un=").append(account);
		strBuf.append("&pw=").append(password);
		strBuf.append("&da=").append(phone);
		strBuf.append("&sm=").append(encodedContent);
		strBuf.append("&dc=15&rd=1&rf=2&tf=3");
		HttpGet get = new HttpGet( strBuf.toString() );
		
		try {
			return client.execute(get);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void close() {
		if(client != null){
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
