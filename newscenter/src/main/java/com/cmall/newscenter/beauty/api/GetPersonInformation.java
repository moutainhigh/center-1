package com.cmall.newscenter.beauty.api;

import com.cmall.newscenter.beauty.model.BeautyAddress;
import com.cmall.newscenter.beauty.model.GetPersonInformationInput;
import com.cmall.newscenter.beauty.model.GetPersonInformationResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 惠美丽—获取个人资料Api
 * 
 * @author yangrong date: 2014-08-20
 * @version1.0
 */
public class GetPersonInformation extends RootApiForToken<GetPersonInformationResult, GetPersonInformationInput> {

	public GetPersonInformationResult Process(GetPersonInformationInput inputParam, MDataMap mRequestMap) {

		GetPersonInformationResult result = new GetPersonInformationResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

			// 查出用户 信息
			MDataMap mUserMap = DbUp.upTable("mc_extend_info_star").one("member_code", getUserCode(), "app_code", getManageCode());

			if (mUserMap != null) {

				result.setAvatar(mUserMap.get("member_avatar"));

				result.setNickname(mUserMap.get("nickname"));

				result.setSex(mUserMap.get("member_sex"));
				
				result.setArea_code(mUserMap.get("area_code"));
				
				result.setBirthday(mUserMap.get("birthday"));
				
				if(!"".equals(mUserMap.get("birthday"))){
				/*年代
				 */
				if(Integer.valueOf(mUserMap.get("birthday").substring(3,4).toString())<5){
					
					result.setCentury(mUserMap.get("birthday").substring(2, 3)+"0");
					
				}else {
					
					result.setCentury(mUserMap.get("birthday").substring(2, 3)+"5");
				}	
					
				}

				result.setSkin_type(mUserMap.get("skin_type"));

				MDataMap skinMap = DbUp.upTable("nc_skin_type").one("skin_code", mUserMap.get("skin_type"));

				if (skinMap != null) {

					result.setSkintype_name(skinMap.get("skin_type"));
				}
 
				result.setHopeful(mUserMap.get("hopeful"));

				// 查询地址信息
				MDataMap mWhereMap = new MDataMap();

				mWhereMap.put("address_code", getUserCode());

				mWhereMap.put("app_code", getManageCode());

				MPageData mPageData = DataPaging.upPageData("nc_address", "","", mWhereMap, new PageOption());

				if (mPageData != null) {

					for (MDataMap mDataMap : mPageData.getListData()) {
						// 默认的地址
						if (mDataMap.get("address_default").equals("1")) {

							BeautyAddress address = new BeautyAddress();

							address.setId(mDataMap.get("address_id"));
							address.setName(mDataMap.get("address_name"));
							address.setMobile(mDataMap.get("address_mobile"));
							address.setProvinces(mDataMap.get("address_province"));
							address.setStreet(mDataMap.get("address_street"));
							address.setIsdefault(mDataMap.get("address_default"));
							address.setPostcode(mDataMap.get("address_postalcode"));
							address.setAreaCode(mDataMap.get("area_code"));

							result.setAdress(address);
						}

					}

				}

			}

		}
		return result;
	}

}