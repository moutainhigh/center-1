package com.cmall.newscenter.beauty.api;

import com.cmall.newscenter.beauty.model.AddressUpdateResult;
import com.cmall.newscenter.service.MemberAuthInfoService;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;

import com.cmall.newscenter.beauty.model.AddressUpdateInput;
import com.cmall.newscenter.util.DateUtil;
import com.srnpr.xmassystem.load.LoadOrderAddress;
import com.srnpr.xmassystem.util.AppVersionUtils;
import com.srnpr.xmassystem.util.XSSUtils;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 惠美丽—修改收货地址Api（不传"是否默认"字段，默认不修改）
 * 
 * @author yangrong date: 2014-08-20
 * @version1.0
 */
public class AddressUpdateApi extends RootApiForToken<AddressUpdateResult, AddressUpdateInput> {
	
	public AddressUpdateResult Process(AddressUpdateInput inputParam,
			MDataMap mRequestMap) {
		
		AddressUpdateResult result = new AddressUpdateResult();
		
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
				|| XSSUtils.hasXSS(inputParam.getStreet()) 
				|| XSSUtils.hasXSS(inputParam.getMobile())
				|| XSSUtils.hasXSS(inputParam.getEmail())) {
			result.setResultCode(0);
			result.setResultMessage("包含非法字符请删除后重试！");
			return result;
		}
		
		int byteSize = 0;
		
		try {
			
			//String limit = bConfig("newscenter.address_limit");
			String limit = "60";
			
			byteSize = inputParam.getStreet().getBytes("GBK").length;
			
			if(byteSize > Integer.parseInt(limit)){
				
				result.inErrorMessage(934205155, limit);
				
			}
			
		} catch (UnsupportedEncodingException e) {
			
			result.setResultCode(0);
			
			result.setResultMessage(e.getMessage());
			
		}
		
		// 设置相关信息
		if (result.upFlagTrue()) {
		
			//地址资料                           
			MDataMap mAddressMap = DbUp.upTable("nc_address").one("app_code",getManageCode(),"address_code",getUserCode(),"address_id",inputParam.getId());
			
			//修改
			if(!inputParam.getName().equals("")){
				
				mAddressMap.put("address_name", inputParam.getName());
			}
			if(!inputParam.getMobile().equals("")){
				
				mAddressMap.put("address_mobile",inputParam.getMobile());
			}
				
			mAddressMap.put("address_postalcode",inputParam.getPostcode());
			
			if(!inputParam.getProvinces().equals("")){
				
				mAddressMap.put("address_province",inputParam.getProvinces());
			}
			if(!inputParam.getStreet().equals("")){
				
				mAddressMap.put("address_street",inputParam.getStreet());
			}
			if(!inputParam.getAreaCode().equals("")){
				
				mAddressMap.put("area_code",inputParam.getAreaCode());
			}
			
			mAddressMap.put("email",inputParam.getEmail());
			
			if( "1".equals(inputParam.getIsdefault())) { 
				// 查出原来默认地址
				MDataMap oldmAddressMap = DbUp.upTable("nc_address").one("app_code",getManageCode(), "address_code", getUserCode(),"address_default", "1");
				if (oldmAddressMap != null) { 
					
					/* 之前的换成非默认 */
					oldmAddressMap.put("address_default", "0");
					oldmAddressMap.put("update_time", DateUtil.getSysDateTimeString());
					
					try {
						DbUp.upTable("nc_address").update(oldmAddressMap);
					} catch (Exception e) {
						e.printStackTrace();
						result.inErrorMessage(934205181);
						return result;
					}

				} 
				
				mAddressMap.put("address_default",inputParam.getIsdefault());
			}
			
			mAddressMap.put("update_time", DateUtil.getSysDateTimeString());
			
			try {
				DbUp.upTable("nc_address").update(mAddressMap);
			} catch (Exception e) {
				e.printStackTrace();
				result.inErrorMessage(934205181);
				return result;
			}
			
			if(StringUtils.isNotBlank(inputParam.getIdNumber()) && StringUtils.isBlank(inputParam.getEmail())){
				
				String prex = WebHelper.upCode("TEST");
				
				String email = bInfo(934205154, prex);
				
				inputParam.setEmail(email);
				
			}
			
			new MemberAuthInfoService().saveMemberAuthInfo(inputParam, getUserCode(), mAddressMap.get("address_id"));
			if(mAddressMap!=null&&mAddressMap.containsKey("address_id")&&StringUtils.isNotBlank(mAddressMap.get("address_id"))){
				new LoadOrderAddress().deleteInfoByCode(mAddressMap.get("address_id"));
			}
		}
		return result;
	}
	
}
