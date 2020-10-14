package com.cmall.groupcenter.userinfo.api;




import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.userinfo.model.UserInfoResult;
import com.cmall.groupcenter.userinfo.model.UserInfoResult.UserInfo;
import com.cmall.membercenter.helper.NickNameHelper;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 微工社获取个人信息
 * 
 * @author chenxk
 *
 */
public class GetUserInfoForGroupApi extends
    RootApiForToken<UserInfoResult, RootInput> {

	public UserInfoResult Process(RootInput inputParam, MDataMap mRequestMap) {
		
		UserInfoResult userInfoResult = new UserInfoResult();
		if (userInfoResult.upFlagTrue()) {
			// 查出用户 信息
			MDataMap mUserMap = DbUp.upTable("mc_extend_info_groupcenter").one("member_code", getUserCode(), "app_code", getManageCode());
			UserInfo userInfo = userInfoResult.new UserInfo();
			//2015-12-17老版本兼容 潘薇
			userInfo.setGender("请选择性别");
			if(mUserMap != null){
				
				userInfo.setBirthday(mUserMap.get("birthday"));
				//修改  fengl2015-12-7
				//null != mUserMap.get("gender") && mUserMap.get("gender").length()>0 ? ("4497465100010002".equals(mUserMap.get("gender")) ? "4497465100010002" : mUserMap.get("gender")) : "4497465100010003"
				if(null != mUserMap.get("gender") && mUserMap.get("gender").length()>0 ){
					if(("男".equals(mUserMap.get("gender")))||("4497465100010002".equals(mUserMap.get("gender")))){
						userInfo.setGender("4497465100010002");
					}else if(("女".equals(mUserMap.get("gender")))||("4497465100010003".equals(mUserMap.get("gender")))){
						userInfo.setGender("4497465100010003");
				    }else{
				    	userInfo.setGender("请选择性别");
				    }
				}else{
					//userInfo.setGender("4497465100010001");
					//2015-12-17老版本兼容 潘薇
					userInfo.setGender("请选择性别");
				}
				
				userInfo.setHeadIconUrl(mUserMap.get("head_icon_url"));
				userInfo.setMemberCode(mUserMap.get("member_code"));
				userInfo.setMemberName(mUserMap.get("member_name"));
				/*userInfo.setNickName(mUserMap.get("nickname"));
				if(StringUtils.isEmpty(userInfo.getNickName())){
					userInfo.setNickName("");
				}*/
				userInfo.setRegion(mUserMap.get("region"));
			}else{
				userInfo.setMemberCode(getUserCode());
			}
			Map<String, String> map = new HashMap<String, String>();
		    map.put("member_code", getUserCode());
			userInfo.setNickName(NickNameHelper.getNickName(map)); 
			String sLockUid = WebHelper.addLock(getUserCode(), 500);
			if(!StringUtils.isEmpty(sLockUid)){
				//获取融云token
				String rongyunToken = this.upRongYunToken(this.getUserCode(),this.getManageCode());
				userInfo.setRongyunTonken(null == rongyunToken ? "" : rongyunToken);
			}else {
				try {
					Thread.sleep(2000);
					//融云token
					String rongyunToken = this.upRongYunToken(this.getUserCode(),this.getManageCode());
					userInfo.setRongyunTonken(null == rongyunToken ? "" : rongyunToken);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			WebHelper.unLock(sLockUid);
			
			userInfo.setQrCodeUrl(String.format(this.bConfig("groupcenter.qrCode_view_url"), userInfo.getMemberCode()));
			userInfoResult.setUserInfo(userInfo);
			//System.out.println("rongyun:"+userInfo.getRongyunTonken());
		}
		return userInfoResult;
		
	}
	
	/**
	 * 获取融云token
	 * @param memberCode
	 * @param sManageCode
	 * @return
	 */
	public String upRongYunToken(String memberCode,String sManageCode){
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
			   /*String name="";
			   String portraitUri="";
		       //查询个人头像
		       String headIconSql = "select  head_icon_url,nickname from mc_extend_info_groupcenter where member_code ='"+memberCode+"' and app_code='"+sManageCode+"'";
		       Map<String, Object> headIconMap = DbUp.upTable("mc_extend_info_groupcenter").dataSqlOne(headIconSql,null);
		       if(headIconMap!=null && headIconMap.get("nickname")!=null){
		    	   name=String.valueOf(headIconMap.get("nickname"));
		    	   portraitUri = String.valueOf(headIconMap.get("head_icon_url"));
		       } else {
			       headIconSql = "select  headerImageUrl,nickName from mc_weixin_binding where member_code ='"+memberCode+"' and manage_code='"+sManageCode+"'";
			       headIconMap = DbUp.upTable("mc_weixin_binding").dataSqlOne(headIconSql,null);
			       if(headIconMap!=null && headIconMap.get("nickName")!=null){
			    	   name=String.valueOf(headIconMap.get("nickName"));
			    	   portraitUri = String.valueOf(headIconMap.get("headerImageUrl"));
			       }
		       }*/
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
