package com.cmall.newscenter.service;

import org.apache.commons.lang.StringUtils;

import com.cmall.membercenter.support.MemberAuthInfoSupport;
import com.cmall.newscenter.beauty.model.AddAddressInput;
import com.cmall.newscenter.beauty.model.AddressUpdateInput;
import com.cmall.systemcenter.util.AESUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;

/**
 * 保存身份信息业务实现
 * @author pang_jhui
 *
 */
public class MemberAuthInfoService extends BaseClass {
	
	/**
	 *  保存身份信息
	 * @param input
	 * 		输入参数
	 * @param userCode
	 * 		用户编码
	 * @param address_id
	 * 		地址编号
	 */
	public void saveMemberAuthInfo(AddAddressInput input, String userCode, String address_id){
		
		MDataMap mDataMap = new MDataMap();
		
		mDataMap.put("member_code", userCode);
		
		mDataMap.put("true_name", input.getName());
		/*默认身份证*/
		mDataMap.put("idcard_type", "4497465200090001");	
		
		mDataMap.put("idcard_number", deIdNumber(input.getIdNumber()));
		
		mDataMap.put("phone_number", input.getPhone());
		
		mDataMap.put("email", input.getEmail());
		
		mDataMap.put("address", input.getProvince()+input.getAddress());
		
		mDataMap.put("address_id", address_id);
		
		new MemberAuthInfoSupport().updateMemberAuthInfo(mDataMap);
		
	}
	
	/**
	 *  保存身份信息
	 * @param input
	 * 		输入参数
	 * @param userCode
	 * 		用户编码
	 * @param address_id
	 * 		地址编码
	 */
	public void saveMemberAuthInfo(AddressUpdateInput input, String userCode,String address_id){
		
		MDataMap mDataMap = new MDataMap();
		
		mDataMap.put("member_code", userCode);
		
		mDataMap.put("true_name", input.getName());
		/*默认身份证*/
		mDataMap.put("idcard_type", "4497465200090001");
		
		mDataMap.put("idcard_number", deIdNumber(input.getIdNumber()));
		
		mDataMap.put("phone_number", input.getMobile());
		
		mDataMap.put("email", input.getEmail());
		
		mDataMap.put("address", input.getProvinces()+input.getStreet());
		
		mDataMap.put("address_id", address_id);
		
		new MemberAuthInfoSupport().updateMemberAuthInfo(mDataMap);
		
	}
	
	/**
	 * 获取身份证号码
	 * @param userCode
	 * 		用户编号
	 * @param address_id
	 * 		地址编码
	 * @return 身份证号码
	 */
	public String getIdNumber(String userCode,String address_id){
		
		MDataMap mDataMap = new MemberAuthInfoSupport().getMemberAuthInfo(userCode,address_id);
		
		String idNumber = "";
		
		if(mDataMap != null){
			
			idNumber = mDataMap.get("idcard_number");
			
		}
		
		return idNumber;
		
	}
	
	/**
	 * 加密身份证
	 * @param number
	 * 		证件号码
	 * @return 证件号码
	 */
	public String enIdNumber(String number){
		
		String idNumber = "";
		
		if(StringUtils.isNotBlank(number)){
			
			AESUtil aesUtil = new AESUtil();
			
			aesUtil.initialize();
			
			idNumber = aesUtil.encrypt(number);
			
		}
		
		return idNumber;
		
	}
	
	/**
	 * 解密身份证
	 * @param number
	 * 		证件号码
	 * @return 解密后的证件号码
	 */
	public String deIdNumber(String number){
		
		String idNumber = "";
		
		if(StringUtils.isNotBlank(number)){
			
			AESUtil aesUtil = new AESUtil();
			
			aesUtil.initialize();
			
			idNumber = aesUtil.decrypt(number);
			
		}
		
		return idNumber;
		
	}

}
