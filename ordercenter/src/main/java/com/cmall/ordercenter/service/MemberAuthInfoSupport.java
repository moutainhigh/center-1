package com.cmall.ordercenter.service;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import com.cmall.systemcenter.common.DateUtil;
import com.cmall.systemcenter.util.AESUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 用户身份信息支持类
 * @author pang_jhui
 *
 */
public class MemberAuthInfoSupport extends BaseClass {
	
	/**
	 * 查询证件信息
	 * @param idCardType
	 * 		证件类型
	 * @param idCardNumber
	 * 		证件号码
	 * @return MDataMap
	 * 		证件信息
	 */
	public MDataMap getMemberAuthInfo(String memberCode, String address_id){
		
		return DbUp.upTable("mc_authenticationInfo").one("member_code", memberCode,"address_id",address_id);
		
	}
	
	/**
	 * 保存身份信息
	 * @param mDataMap
	 */
	public void insertMemberAuthInfo(MDataMap mDataMap){
		
		mDataMap.put("auth_code", WebHelper.upCode("aux"));
		
		mDataMap.put("create_time", DateUtil.toString(new Date(), DateUtil.DATE_FORMAT_DATETIME));
		
		mDataMap.put("update_time", DateUtil.toString(new Date(), DateUtil.DATE_FORMAT_DATETIME));
		
		DbUp.upTable("mc_authenticationInfo").dataInsert(mDataMap);
		
	}
	
	/**
	 * 更新身份信息
	 * @param mDataMap
	 */
	public void updateMemberAuthInfo(MDataMap mDataMap){
		
		MDataMap authInfo = getMemberAuthInfo(mDataMap.get("member_code"),mDataMap.get("address_id"));
		
		if(authInfo != null){
			
			copyDataMap(mDataMap, authInfo, "zid","uid","auth_code","member_code");
			
			authInfo.put("update_time", DateUtil.toString(new Date(), DateUtil.DATE_FORMAT_DATETIME));
			
			DbUp.upTable("mc_authenticationInfo").update(authInfo);
			
		}else{
			
			insertMemberAuthInfo(mDataMap);
			
		}
		
	}
	
	/**
	 * 数据复制
	 * @param sourceDataMap
	 * 		源map
	 * @param targetDataMap
	 * 		目的map
	 * @param ignoreKey
	 * 		忽略复制的key
	 */
	public void copyDataMap(MDataMap sourceDataMap, MDataMap targetDataMap, String... ignoreKey){
		
		List<String> ignoreList = (ignoreKey != null) ? Arrays.asList(ignoreKey) : null;
		
		Iterator<String> keys = sourceDataMap.keySet().iterator();
		
		while (keys.hasNext()) {
			
			String key = keys.next();
			
			if(ignoreList != null && ignoreList.contains(key)){
				
				continue;
				
			}
			
			targetDataMap.put(key, sourceDataMap.get(key));
			
		}
		
	}
	
	/**
	 * 将身份证信息进行AES加密
	 * @param userCode
	 * 		用户编号
	 * @return String
	 * 		身份证信息
	 */
	public String getAesIdNumber(String userCode, String address_id){
		
		MDataMap mDataMap = getMemberAuthInfo(userCode,address_id);
		
		if(mDataMap != null){
			
			if(StringUtils.isNotBlank(mDataMap.get("idcard_number"))){
				
				AESUtil aesUtil = new AESUtil();
				
				aesUtil.initialize();
				
				return aesUtil.encrypt(mDataMap.get("idcard_number"));
				
			}
			
		}
		
		return "";
		
	}
	
	/**
	 * 获取通关状态
	 * @param idNumber
	 * @return
	 */
	public String getCustomStatus(String idNumber){
		
		String customStatus = "";
		
		MDataMap mWhereMap = new MDataMap();
		
		mWhereMap.put("idcard_number", idNumber);
		
		List<MDataMap> list = DbUp.upTable("mc_authenticationInfo").queryAll("customs_status", "", "", mWhereMap);
		
		if(list != null && !list.isEmpty()){
			
			customStatus = list.get(0).get("customs_status");
			
		}
		
		
		return customStatus;
		
		
	}
	
	/**
	 * 解密身份证号码
	 * @param idNumber
	 * 		解密身份证号码
	 * @return
	 */
	public String deIdNumber(String idNumber) {

		String idNumberStr = "";
		

		if (StringUtils.isNotBlank(idNumber)) {

			AESUtil aesUtil = new AESUtil();

			aesUtil.initialize();

			idNumberStr = aesUtil.decrypt(idNumber);

		}

		return idNumberStr;

	}
	
	/**
	 * 解密身份证号码
	 * @param idNumber
	 * 		解密身份证号码
	 * @return
	 */
	public String enIdNumber(String idNumber) {

		String idNumberStr = "";
		

		if (StringUtils.isNotBlank(idNumber)) {

			AESUtil aesUtil = new AESUtil();

			aesUtil.initialize();

			idNumberStr = aesUtil.encrypt(idNumber);

		}

		return idNumberStr;

	}
	

}
