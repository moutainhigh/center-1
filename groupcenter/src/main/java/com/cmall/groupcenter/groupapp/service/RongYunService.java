package com.cmall.groupcenter.groupapp.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.groupapp.model.GetFriendInformationInfoResult;
import com.cmall.groupcenter.groupapp.model.Person;
import com.cmall.groupcenter.groupapp.model.RongYunSingleChatBean;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.service.GroupCommonService;
import com.cmall.groupcenter.util.StringHelper;
import com.cmall.membercenter.helper.NickNameHelper;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.webdo.WebTemp;

public class RongYunService extends BaseClass{
	
	
	
	/**
	 * 获取融云token
	 * @param memberCode
	 * @param sManageCode
	 * @return
	 */
	public String upRongYunToken(String memberCode){
	       String  tokenSql= "select  rong_yun_token from mc_member_rongyun where member_code =:member_code";
	       Map<String, Object> headIconMap = DbUp.upTable("mc_member_rongyun").dataSqlOne(tokenSql,new MDataMap("member_code",memberCode));
	       String rongyunToken="";
	       if(headIconMap==null){
	    	   String appkey = bConfig("membercenter.rongyun_app_key"); 
			   String appSerct=bConfig("membercenter.rongyun_app_secret"); 
			   String rongyunDomain=bConfig("membercenter.rongyun_domain");

		       String nonce=String.valueOf(new Random().nextInt());
		       long millis = System.currentTimeMillis();
		       //http请求头信息
		       MDataMap httpHeaderDataMap = new MDataMap();
		       httpHeaderDataMap.put("App-Key", appkey);
		       httpHeaderDataMap.put("Nonce", nonce);
		       httpHeaderDataMap.put("Timestamp",String.valueOf(millis) );
			   String signature =this.Encrypt(appSerct+nonce+millis);
			   httpHeaderDataMap.put("Signature",signature );
			   //表单信息
			   MDataMap map = new MDataMap();
			 
			   map.put("userId", memberCode);
			   map.put("name", "");
			   map.put("portraitUri", "");
			   String resultJson="";
			   try {
				   resultJson = WebClientSupport.upPost(rongyunDomain+"/user/getToken.json", map,httpHeaderDataMap);
				} catch (Exception e) {
					e.printStackTrace();
					resultJson = "请求接口发生异常："+e.getMessage();
				}
			   //写入logs
			   //if(null != DbUp.upTable("lc_rongyun_logs")){
				   DbUp.upTable("lc_rongyun_logs").dataInsert(new MDataMap("member_code",memberCode,"rongyun_response_result",resultJson,"create_time",DateHelper.upNow()));
			   //}
			   JsonHelper<Map<String,String>> jH = new JsonHelper<Map<String,String>>();
			   Map<String,String> rongyunJsonObj = jH.StringToObj(resultJson, new HashMap<String,String>());
			   if(rongyunJsonObj.get("token")==null){
				   return "";
			   }
			   DbUp.upTable("mc_member_rongyun").dataInsert(new MDataMap("member_code",memberCode,"rong_yun_token",rongyunJsonObj.get("token"),"create_time",DateHelper.upNow()));
			   rongyunToken = rongyunJsonObj.get("token");
		   } else {
			   	//融云token
				rongyunToken=String.valueOf(headIconMap.get("rong_yun_token"));
		   }
		   return rongyunToken;
	}
	
	/**
	 * 单聊消息
	 * @param bean
	 * @return
	 */
	public RootResultWeb singleChatMessageSend(RongYunSingleChatBean bean){
		RootResultWeb result = new RootResultWeb();
		if(StringUtils.isBlank(bean.getFromUserId()) || StringUtils.isBlank(bean.getToUserId()) ||  StringUtils.isBlank(bean.getObjectName())||  StringUtils.isBlank(bean.getContent())) 
		{
			result.inErrorMessage(918570009);
			return result;
		}
		
//		System.out.println("bean.getFromUserId():" +bean.getFromUserId());
//		System.out.println("bean.toUserId():" +bean.getToUserId());
		
		boolean backToken=true;
		if("".equals(upRongYunToken(bean.getFromUserId()))||null==upRongYunToken(bean.getFromUserId())){
			backToken=false;
		}
		if("".equals(upRongYunToken(bean.getToUserId()))||null==upRongYunToken(bean.getFromUserId())){
			backToken=false;
		}
        if(backToken){
     	   //表单信息
     	   MDataMap paramsMap = new MDataMap();
     	   paramsMap.put("fromUserId", bean.getFromUserId());
     	   paramsMap.put("toUserId",   bean.getToUserId());
     	   paramsMap.put("objectName", bean.getObjectName());//"RC:TxtMsg"
     	   paramsMap.put("content", bean.getContent() );//"{\"content\":"+bean.getContent()+",\"extra\":\"helloExtra\"}"
     	   paramsMap.put("pushContent", bean.getContent());
     	   paramsMap.put("pushData", bean.getPushData());//"{\"pushData\":\"hello\"}"
     	   paramsMap.put("count", String.valueOf(bean.getCount()));
     	   
     	   Map<String,String> returnMap= callRongYunApi(bean.getFromUserId(), paramsMap,"/message/private/publish.json");
     	   if(returnMap==null ){
     		   result.inErrorMessage(918570010);
     		   return result;
     	   }
        }else{
        	result.inErrorMessage(918570011);
        	return result;
        }
		   return result;
	}
	
	
	public Map<String,String>   callRongYunApi(String memberCode,  MDataMap paramsMap,String method){
		   String resultJson="";
		   int callBackType=0;
		   String appkey = bConfig("membercenter.rongyun_app_key"); 
		   String appSerct=bConfig("membercenter.rongyun_app_secret"); 
		   String rongyunDomain=bConfig("membercenter.rongyun_domain");
		   

	       String nonce=String.valueOf(new Random().nextInt());
	       long millis = System.currentTimeMillis();
	       //http请求头信息
	       MDataMap httpHeaderDataMap = new MDataMap();
	       httpHeaderDataMap.put("App-Key", appkey);
	       httpHeaderDataMap.put("Nonce", nonce);
	       httpHeaderDataMap.put("Timestamp",String.valueOf(millis) );
		   String signature =this.Encrypt(appSerct+nonce+millis);
		   httpHeaderDataMap.put("Signature",signature );
		   
		 try {
			   resultJson = WebClientSupport.upPost(rongyunDomain+method, paramsMap,httpHeaderDataMap);
			   callBackType=1;
		 } catch (Exception e) {
				e.printStackTrace();
				resultJson = "请求接口发生异常："+e.getMessage();
			}
//		   System.out.println("resultJson:"+resultJson);
		   DbUp.upTable("lc_rongyun_logs").dataInsert(new MDataMap("member_code",memberCode,"rongyun_response_result",resultJson,"create_time",DateHelper.upNow()));
		   if(callBackType==0 || resultJson.indexOf("200")==-1){
			   return null;
		   }
		   JsonHelper<Map<String,String>> jH = new JsonHelper<Map<String,String>>();
		   Map<String,String> responeMap = jH.StringToObj(resultJson, new HashMap<String,String>());
		   return responeMap;
	}
	
	/**
	 * sha-1 加密
	 * @param strSrc
	 * @return
	 */
	public String Encrypt(String strSrc) {
		MessageDigest md = null;
		String strDes = null;

		byte[] bt = strSrc.getBytes();
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(bt);
			strDes = Hex.encodeHexString(md.digest());
		} catch (NoSuchAlgorithmException e) {
			//System.out.println("Invalid algorithm.");
			return null;
		}
		return strDes;
	}
}
