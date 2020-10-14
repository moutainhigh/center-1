package com.cmall.systemcenter.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.systemcenter.common.DateUtil;
import com.cmall.systemcenter.model.AppNavigation;
import com.cmall.systemcenter.model.ClientInfo;
import com.cmall.systemcenter.util.AESUtil;
import com.cmall.systemcenter.util.StringUtility;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
/**
 *app启动页处理service 
 */
public class StartPageService extends BaseClass {

	//手持设备需要ke的集合
	private final static Map<String,MDataMap> DEVICE_KEYS  = new HashMap<String,MDataMap>();
	private final static Map<String,MDataMap> DEVICE_IOS_KEYS  = new HashMap<String,MDataMap>();
	private final static String MANAGE_CODE_FOR_HJY = "SI2003";
	private final static String MANAGE_CODE_FOR_SPG = "SI3003";
	public final static String ANDROID = "android";
	public final static String IOS = "ios";
	
	public MDataMap getPics(MDataMap in) {
		MDataMap reMap = new MDataMap();
		String now=DateUtil.getSysDateTimeString();
		MDataMap quMap = DbUp.upTable("nc_startpage").oneWhere("", "", "start_time<='"+now+"' and end_time>='"+now+"' and app_code=:app_code ", "app_code",in.get("sellerCode"));
		if(quMap!=null&&!quMap.isEmpty()){
			if(quMap.containsKey("pic_id")&&in.get("picNm")!=null&&!"".equals(in.get("picNm"))&&in.get("picNm").equals(quMap.get("pic_id"))){//相等
				reMap.put("picType", "0");//不更新
			}else if(quMap.containsKey("pic_id")&&!(quMap.get("pic_id").equals(in.get("picNm")))) {//不相等
				reMap.put("picType", "1");//更新
			}
			if("4497471600210003".equals(quMap.get("showmore_linktype"))){//分类搜索
				List<String> categoryCodeArr = new ArrayList<String>();
				categoryCodeArr.add(quMap.get("showmore_linkvalue"));
				CategoryService hcService = new CategoryService();
				MDataMap categoryNameMap = hcService.getCategoryName(categoryCodeArr,in.get("sellerCode"));
				quMap.put("showmore_linkvalue", categoryNameMap.get(quMap.get("showmore_linkvalue")));
			}
			reMap.put("webViewType", quMap.get("webViewType"));
			reMap.put("picNm", quMap.get("pic_id"));
			reMap.put("picUrl", quMap.get("photo_url"));
			//新增加的字段
			reMap.put("yn_jump_button", quMap.get("yn_jump_button"));
			reMap.put("showmore_linktype", quMap.get("showmore_linktype"));
			reMap.put("showmore_linkvalue", quMap.get("showmore_linkvalue"));
			reMap.put("button_type", quMap.get("button_type"));
			reMap.put("button_text", quMap.get("button_text"));
			if(StringUtils.isNotEmpty(quMap.get("button_color"))){
				reMap.put("button_color", "#"+quMap.get("button_color"));
			}
			if(StringUtils.isNotEmpty(quMap.get("button_background"))){
				reMap.put("button_background", "#"+quMap.get("button_background"));
			}
			reMap.put("residence_time", quMap.get("residence_time"));
			reMap.put("yn_countdown", quMap.get("yn_countdown"));
			reMap.put("button_pic", quMap.get("button_pic"));
		}else {
			reMap.put("picType", "2");//删除
			reMap.put("picNm", "");
			reMap.put("picUrl", "");
		}
		MDataMap dataMap = new MDataMap();//操作流水表
		dataMap.put("sqNum", in.get("sqNum"));
		dataMap.put("push_type", in.get("pushType"));
		
		if(StringUtils.isNotBlank(in.get("pushToken")) && in.get("pushToken").length() >30){
			dataMap.put("push_token", in.get("pushToken"));
		}
		if(StringUtils.isNotBlank(in.get("buyerCode"))){
			dataMap.put("buyer_code", in.get("buyerCode"));
		}
		dataMap.put("update_time", FormatHelper.upDateTime());
		dataMap.put("seller_code", in.get("sellerCode"));
		dataMap.put("flag", "1");
		if(StringUtils.isBlank(in.get("sqNum"))){
			reMap.put("sqNum", WebHelper.upCode("LSH"));//插入流水信息
			dataMap.put("sqNum", reMap.get("sqNum"));
			dataMap.put("create_time", FormatHelper.upDateTime());
			DbUp.upTable("lc_startPageLS").dataInsert(dataMap);
		}else{
			MDataMap oo = DbUp.upTable("lc_startPageLS").one("sqNum",in.get("sqNum"));
			if(null==oo||oo.isEmpty()){
				reMap.put("sqNum", WebHelper.upCode("LSH"));//插入流水信息
				dataMap.put("sqNum", reMap.get("sqNum"));
				dataMap.put("create_time", FormatHelper.upDateTime());
				DbUp.upTable("lc_startPageLS").dataInsert(dataMap);
			}else {
				DbUp.upTable("lc_startPageLS").dataUpdate(dataMap, "", "sqNum");
				reMap.put("sqNum", dataMap.get("sqNum"));
			}
		}
		return reMap;
	}
	public void saveClient(ClientInfo ci,String sellerCode,String buyerCode,String sqNum) {
		try {
			MDataMap clientMap = new MDataMap();
			clientMap.put("order_code", sqNum);
			MDataMap one = DbUp.upTable("lc_client_info").one("order_code",sqNum);
			if(StringUtility.isNotNull(ci.getApp_vision())){
				clientMap.put("version", ci.getApp_vision());
			}
			if(StringUtility.isNotNull(ci.getModel())){
				clientMap.put("model", ci.getModel());
			}
			if(StringUtility.isNotNull(ci.getUniqid())){
				clientMap.put("uniqid", ci.getUniqid());
			}
			if(StringUtility.isNotNull(ci.getMac())){
				clientMap.put("mac", ci.getMac());
			}
			if(StringUtility.isNotNull(ci.getOs())){
				clientMap.put("os", ci.getOs());
			}
			if(StringUtility.isNotNull(ci.getFrom())){
				clientMap.put("from_code", ci.getFrom());
			}
			if(StringUtility.isNotNull(ci.getScreen())){
				clientMap.put("screen", ci.getScreen());
			}
			if(StringUtility.isNotNull(ci.getOp())){
				clientMap.put("op", ci.getOp());
			}
			if(StringUtility.isNotNull(ci.getProduct())){
				clientMap.put("product", ci.getProduct());
			}
			if(StringUtility.isNotNull(ci.getNet_type())){
				clientMap.put("net_type", ci.getNet_type());
			}
			if(StringUtility.isNotNull(ci.getOs_info())){
				clientMap.put("os_info", ci.getOs_info());
			}
			if(StringUtility.isNotNull(ci.getIdfa())){
				clientMap.put("idfa", ci.getIdfa());
			}
			clientMap.put("seller_code", sellerCode);
			clientMap.put("create_user", buyerCode);
			clientMap.put("create_time", FormatHelper.upDateTime());
			//不论用户是否第一次登录均将 用户登录数据插入数据库lc_client_info_every_record
			DbUp.upTable("lc_client_info_every_record").dataInsert(clientMap);
			if(one!=null&&!one.isEmpty()){
				DbUp.upTable("lc_client_info").dataUpdate(clientMap, "", "order_code");
			}else {
				DbUp.upTable("lc_client_info").dataInsert(clientMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 *根据流水号根据流水信息是否可用,memberCode为空时更新为不可用，memberCode不为空时，更新memberCode
	 * lsCode 不能为空
	 * memberCode 用户编号
	 * 
	 */
	public boolean updateLsh(String lsCode,String memberCode){
		boolean flag = false;
		if(lsCode==null||"".equals(lsCode)){
			return flag;
		}
		if(memberCode==null||"".equals(memberCode)){//更新为不可用
			DbUp.upTable("lc_startPageLS").dataUpdate(new MDataMap("sqNum",lsCode,"flag","0"), "flag", "sqNum");
			flag=true;
		}else {//更新新的用户编号
			DbUp.upTable("lc_startPageLS").dataUpdate(new MDataMap("sqNum",lsCode,"buyer_code",memberCode,"flag","1"), "buyer_code,flag", "sqNum");
			flag=true;
		}
		return flag;
	}
	
	/**
	 *根据流水号更新登录用户编号 
	 *@param lsCode 不能为空
	 *@param memberCode 用户编号
	 */
	public boolean updateBuyerByLsh(String lsCode,String memberCode){
		boolean flag = false;
		if(lsCode==null||"".equals(lsCode)){
			return flag;
		}
		if(memberCode==null||"".equals(memberCode)){
			flag=false;
		}else {//更新新的用户编号
			DbUp.upTable("lc_startPageLS").dataUpdate(new MDataMap("sqNum",lsCode,"buyer_code",memberCode), "buyer_code", "sqNum");
			flag=true;
		}
		return flag;
	}
	
	public MDataMap getKeys(String manageCode,String clientType){
		MDataMap result = new MDataMap();
		if(ANDROID.equals(clientType)) {//android 
			
			if(null == DEVICE_KEYS.get(manageCode)){
				synchronized (DEVICE_KEYS) {
					MDataMap map = new MDataMap();
					if(MANAGE_CODE_FOR_HJY.equals(manageCode)){
//						if(ANDROID.equals(clientType)) {//android
//							map.put( "nbsAppAgentKey","a4eacb59afe34e7e93a2db8ab3ec501f");
//						} else if(IOS.equals(clientType)){//ios
//							map.put( "nbsAppAgentKey","799f92ff95f5428e8a85a6bd73d39bad");
//						}
						map.put("iosnbsAppAgentKey", "9525afc83f7f433da4d60b4ef51f593e"); 
						map.put("androidnbsAppAgentKey", "a4eacb59afe34e7e93a2db8ab3ec501f"); 
						
						map.put( "wechatAppId","wx5003e049845e69c1");
						map.put( "wechatAppSecret","5fc56a09f9477542509eb3480e6912d0");
//						map.put( "qqAppId","101102901");
//						map.put( "qqAppKey","aa3425a666e1e2a37519ea0a4c9b704e");
						map.put( "baiduPushKeyAndroid","WqqTlxYlXGZNcDKFfx7HNzSy");
						map.put( "baiduPushKeyIos","ycitrPALgGw2EGygpDc6Vhwn");
						map.put( "umKeyAndroid","532ba12256240b2cdf06b350");
						map.put( "umKeyIos","532aabd356240bcb6a00cf4c");
						
						map.put( "qqAppId","101407867"); 
						map.put( "qqAppKey","82d412f54a0d49974459c54967dc3ec5"); 
						//新增sina key 
						map.put("sinaAppKey", "211216192"); 
						map.put( "sinaAppSecret","014c00cd16d9dd1aff8d7413113f6f11"); 
						
						
					}else if(MANAGE_CODE_FOR_SPG.equals(manageCode)){
						map.put( "nbsAppAgentKey","e25a219001ca4a6d852b3bab0faa217c");
						map.put( "wechatAppId","wxbbc10d89787316f9");
						map.put( "wechatAppSecret","638bad122109f1385ad21ba798cfc6ad");
						map.put( "qqAppId","1104758106");
						map.put( "qqAppKey","7OAL9JG0isSfMbL3");
						map.put( "baiduPushKeyAndroidId",bConfig("systemcenter.baiduPushKeyAndroidId"));
						map.put( "baiduPushKeyAndroidKey",bConfig("systemcenter.baiduPushKeyAndroidKey"));
						map.put( "baiduPushKeyIosKey",bConfig("systemcenter.baiduPushKeyIosKey"));
						map.put( "baiduPushKeyAndroidSecret",bConfig("systemcenter.baiduPushKeyAndroidSecret"));
						map.put( "umKeyAndroid","55a6068767e58e8d13000b3a");
						map.put( "umKeyIos","55a6063967e58e81a3001f15");
						map.put( "weiboAndroidKey","3318298957");
						map.put( "weiboAndroidSecret","739ec28986994cfde58ed88578a22807");
					}
					DEVICE_KEYS.put(manageCode, map);
				}
			}
			result = DEVICE_KEYS.get(manageCode);
			
			
		} else if(IOS.equals(clientType)){//ios 
			if(null == DEVICE_IOS_KEYS.get(manageCode)){
				synchronized (DEVICE_IOS_KEYS) {
					MDataMap map = new MDataMap();
					if(MANAGE_CODE_FOR_HJY.equals(manageCode)){
//						if(ANDROID.equals(clientType)) {//android
//							map.put( "nbsAppAgentKey","a4eacb59afe34e7e93a2db8ab3ec501f");
//						} else if(IOS.equals(clientType)){//ios
//							map.put( "nbsAppAgentKey","799f92ff95f5428e8a85a6bd73d39bad");
//						}
						map.put("iosnbsAppAgentKey", "9525afc83f7f433da4d60b4ef51f593e"); 
						map.put("androidnbsAppAgentKey", "a4eacb59afe34e7e93a2db8ab3ec501f"); 
						
						map.put( "wechatAppId","wx5003e049845e69c1");
						map.put( "wechatAppSecret","5fc56a09f9477542509eb3480e6912d0");
//						map.put( "qqAppId","101102901");
//						map.put( "qqAppKey","aa3425a666e1e2a37519ea0a4c9b704e");
						map.put( "baiduPushKeyAndroid","WqqTlxYlXGZNcDKFfx7HNzSy");
						map.put( "baiduPushKeyIos","ycitrPALgGw2EGygpDc6Vhwn");
						map.put( "umKeyAndroid","532ba12256240b2cdf06b350");
						map.put( "umKeyIos","532aabd356240bcb6a00cf4c");
						
						map.put( "qqAppId","101407833"); 
						map.put( "qqAppKey","7bcbe0e0bd9063ad8548f9c2fa583e5c"); 
						//新增sina key 
						map.put("sinaAppKey", "211216192"); 
						map.put("sinaAppSecret","014c00cd16d9dd1aff8d7413113f6f11"); 
						
						
					}else if(MANAGE_CODE_FOR_SPG.equals(manageCode)){
						map.put( "nbsAppAgentKey","e25a219001ca4a6d852b3bab0faa217c");
						map.put( "wechatAppId","wxbbc10d89787316f9");
						map.put( "wechatAppSecret","638bad122109f1385ad21ba798cfc6ad");
						map.put( "qqAppId","1104758106");
						map.put( "qqAppKey","7OAL9JG0isSfMbL3");
						map.put( "baiduPushKeyAndroidId",bConfig("systemcenter.baiduPushKeyAndroidId"));
						map.put( "baiduPushKeyAndroidKey",bConfig("systemcenter.baiduPushKeyAndroidKey"));
						map.put( "baiduPushKeyIosKey",bConfig("systemcenter.baiduPushKeyIosKey"));
						map.put( "baiduPushKeyAndroidSecret",bConfig("systemcenter.baiduPushKeyAndroidSecret"));
						map.put( "umKeyAndroid","55a6068767e58e8d13000b3a");
						map.put( "umKeyIos","55a6063967e58e81a3001f15");
						map.put( "weiboAndroidKey","3318298957");
						map.put( "weiboAndroidSecret","739ec28986994cfde58ed88578a22807");
					}
					DEVICE_IOS_KEYS.put(manageCode, map);
				}
			}
			result = DEVICE_IOS_KEYS.get(manageCode);
		} 
		
		return result;
		
		
	}
	
	/**
	 *加密 
	 * 
	 */
	public MDataMap getKeysSecret(String manageCode,String clientType){
		MDataMap result = new MDataMap();
		try {
			MDataMap map = getKeys(manageCode,clientType);
			Iterator<String> iterator = map.keySet().iterator(); 
			AESUtil util = new AESUtil();
			util.initialize();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				String value = util.encrypt(map.get(key));
				 result.put(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 *保存用户位置信息 
	 */
	public void saveUserLocationInfor(MDataMap map){
		try {
			DbUp.upTable("lc_userlocationInfo").dataInsert(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
