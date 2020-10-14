package com.cmall.newscenter.beauty.api;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.membercenter.support.MemberAuthInfoSupport;
import com.cmall.newscenter.beauty.model.AddAddressInput;
import com.cmall.newscenter.beauty.model.AddAddressResult;
import com.cmall.newscenter.beauty.model.BeautyAddress;
import com.cmall.newscenter.service.MemberAuthInfoService;
import com.cmall.productcenter.common.DateUtil;
import com.srnpr.xmassystem.util.AppVersionUtils;
import com.srnpr.xmassystem.util.XSSUtils;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 惠美丽—新增收货地址Api（新增加的第一个地址强制设为默认地址）
 * 
 * @author yangrong date: 2014-08-20
 * @version1.0
 */
public class AddAddress extends RootApiForToken<AddAddressResult, AddAddressInput> {
	
	public AddAddressResult Process(AddAddressInput inputParam,
			MDataMap mRequestMap) {

		AddAddressResult result = new AddAddressResult();
		//从5.4.2版本开始，要使用四级编码
		String currentVersion = getApiClient().get("app_vision");
		if(AppVersionUtils.compareTo(currentVersion, "5.4.2") >= 0) {//540版本以后，判断区域编码是否为四级编码
			int checkCount = DbUp.upTable("sc_tmp").count("code", inputParam.getAreaCode(), "use_yn", "Y", "code_lvl", "4");
			if(checkCount <= 0) {
				result.setResultCode(934205191);
				result.setResultMessage(bInfo(934205191));
				return result;
			}
		}
		
		if(XSSUtils.hasXSS(inputParam.getName()) 
				|| XSSUtils.hasXSS(inputParam.getAddress()) 
				|| XSSUtils.hasXSS(inputParam.getPhone())
				|| XSSUtils.hasXSS(inputParam.getEmail())) {
			result.setResultCode(0);
			result.setResultMessage("包含非法字符请删除后重试！");
			return result;
		}
		
		int byteSize = 0;
		
		try {
			
			//String limit = bConfig("newscenter.address_limit");
			String limit = "60";
			
			byteSize = inputParam.getAddress().getBytes("GBK").length;
			
			if(byteSize > Integer.parseInt(limit)){
				
				result.inErrorMessage(934205155, limit);
				
			}
			
		} catch (UnsupportedEncodingException e) {
			
			result.setResultCode(0);
			
			result.setResultMessage(e.getMessage());
			
		}
		
		String address_Id = "";

		// 设置相关信息
		if (result.upFlagTrue()) {
			
			String systime = DateUtil.getSysDateTimeString();
			int count = DbUp.upTable("nc_address").count("address_code",getUserCode(),"app_code",getManageCode());
			
			if(StringUtils.isBlank(inputParam.getEmail())){
				
				String prex = WebHelper.upCode("TEST");
				
				String email = bInfo(934205154, prex);
				
				inputParam.setEmail(email);
				
			}
			
			if(count>=20){
				
				result.setResultCode(934205143);
				result.setResultMessage(bInfo(934205143));
				return result;
			}
			if (count == 0) {
				
				// 之前没添加过 直接设为默认地址
				MDataMap mInsertMap = new MDataMap();
				
				address_Id = WebHelper.upCode("DZ");
				
				mInsertMap.inAllValues("address_id", address_Id,"address_default", "1", "address_name",inputParam.getName(), "address_mobile",inputParam.getPhone(), "address_postalcode",inputParam.getPostcode(), "address_province",inputParam.getProvince(), "address_street",inputParam.getAddress(), "address_code", getUserCode(),"app_code", getManageCode(), "area_code",inputParam.getAreaCode(), "email",inputParam.getEmail(), "create_time", systime,"update_time", systime);
				
				try {
					DbUp.upTable("nc_address").dataInsert(mInsertMap);
					result.setAddressId(address_Id);
				} catch (Exception e) {
					e.printStackTrace();
					result.inErrorMessage(934205181);
					return result;
				}
				
				/*更新证件信息*/				
				new MemberAuthInfoService().saveMemberAuthInfo(inputParam, getUserCode(), address_Id);

			} else {
				// 之前添加过
				if (inputParam.getIsDefault().equals("1")) {
					
					MDataMap mInsertMap = new MDataMap();
					
					address_Id = WebHelper.upCode("DZ");
					
					mInsertMap.inAllValues("address_id",address_Id, "address_name",inputParam.getName(), "address_mobile",inputParam.getPhone(), "address_postalcode",inputParam.getPostcode(), "address_province",inputParam.getProvince(), "address_city", "","address_county", "", "address_street",inputParam.getAddress(), "address_code",getUserCode(), "app_code", getManageCode(),"area_code", inputParam.getAreaCode(),"address_default", inputParam.getIsDefault(),"email", inputParam.getEmail(), "create_time",systime, "update_time", systime);
					DbUp.upTable("nc_address").dataInsert(mInsertMap);
					result.setAddressId(address_Id);
					
					/*更新证件信息*/				
					new MemberAuthInfoService().saveMemberAuthInfo(inputParam, getUserCode(), address_Id);

					// 查出原来默认地址
					MDataMap mAddressMap = DbUp.upTable("nc_address").one("app_code", getManageCode(), "address_code",getUserCode(), "address_default", "1");

					if (mAddressMap != null) {

						/* 之前的换成非默认 */
						mAddressMap.put("address_default", "0");
						mAddressMap.put("update_time", systime);

						try {
							DbUp.upTable("nc_address").update(mAddressMap);
						} catch (Exception e) {
							e.printStackTrace();
							result.inErrorMessage(934205181);
							return result;
						}

					}
				} else {
					MDataMap mInsertMap = new MDataMap();
					
					address_Id = WebHelper.upCode("DZ");
					
					mInsertMap.inAllValues("address_id", address_Id, "address_default", "0","address_name", inputParam.getName(),"address_mobile", inputParam.getPhone(),"address_postalcode", inputParam.getPostcode(),"address_province", inputParam.getProvince(),"address_city", "", "address_county", "","address_street", inputParam.getAddress(),"address_code", getUserCode(), "app_code",getManageCode(), "area_code",inputParam.getAreaCode(), "email",inputParam.getEmail(), "create_time", systime,"update_time", systime);
					try {
						DbUp.upTable("nc_address").dataInsert(mInsertMap);
						result.setAddressId(address_Id);
					} catch (Exception e) {
						e.printStackTrace();
						result.inErrorMessage(934205181);
						return result;
					}
					/*更新证件信息*/				
					new MemberAuthInfoService().saveMemberAuthInfo(inputParam, getUserCode(), address_Id);

				}
			}

//			// 查询地址信息
//			MDataMap mWhereMap = new MDataMap();
//
//			mWhereMap.put("address_code", getUserCode());
//
//			mWhereMap.put("app_code", getManageCode());
//
//			MPageData mPageData = DataPaging.upPageData("nc_address", "", "",
//					mWhereMap, new PageOption());
//
//			if (mPageData != null) {
//
//				for (MDataMap mDataMap : mPageData.getListData()) {
//
//					BeautyAddress address = new BeautyAddress();			
//
//					address.setIsdefault(mDataMap.get("address_default"));
//					address.setPostcode(mDataMap.get("address_postalcode"));
//					address.setProvinces(mDataMap.get("address_province"));
//					address.setId(mDataMap.get("address_id"));
//					address.setName(mDataMap.get("address_name"));
//					address.setMobile(mDataMap.get("address_mobile"));
//					address.setStreet(mDataMap.get("address_street"));
//					address.setAreaCode(mDataMap.get("area_code"));
//					address.setEmail(mDataMap.get("email"));
//					
//					/*获取加密后的身份证信息*/
//					String idNumber = new MemberAuthInfoSupport().getAesIdNumber(getUserCode(), address.getId());
//					
//					address.setIdNumber(idNumber);
//
//					result.getAdress().add(address);
//				}
//			}
			
			Map<String, Object> addressMap = DbUp.upTable("nc_address").oneMapPriLib("address_id", address_Id);
			if(addressMap != null) {
				BeautyAddress address = new BeautyAddress();			

				address.setIsdefault(StringUtils.trimToEmpty((String)addressMap.get("address_default")));
				address.setPostcode(StringUtils.trimToEmpty((String)addressMap.get("address_postalcode")));
				address.setProvinces(StringUtils.trimToEmpty((String)addressMap.get("address_province")));
				address.setId(StringUtils.trimToEmpty((String)addressMap.get("address_id")));
				address.setName(StringUtils.trimToEmpty((String)addressMap.get("address_name")));
				address.setMobile(StringUtils.trimToEmpty((String)addressMap.get("address_mobile")));
				address.setStreet(StringUtils.trimToEmpty((String)addressMap.get("address_street")));
				address.setAreaCode(StringUtils.trimToEmpty((String)addressMap.get("area_code")));
				address.setEmail(StringUtils.trimToEmpty((String)addressMap.get("email")));
				
				if(StringUtils.isNotBlank(inputParam.getIdNumber())) {
					String idNumber = new MemberAuthInfoSupport().getAesIdNumber(inputParam.getIdNumber());
					address.setIdNumber(idNumber);
				}
				
				result.getAdress().add(address);
			}

		}
		return result;
	}

}
