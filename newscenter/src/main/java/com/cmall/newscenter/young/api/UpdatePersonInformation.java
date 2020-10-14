package com.cmall.newscenter.young.api;

import java.util.List;
import java.util.Map;

import com.cmall.newscenter.young.model.UpdatePersonInformationInput;
import com.cmall.newscenter.young.model.UpdatePersonInformationResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 小时代—修改个人资料(昵称唯一&&敏感词过滤)Api
 * 
 * @author yangrong date: 2015-2-3
 * @version1.0
 */
public class UpdatePersonInformation extends RootApiForToken<UpdatePersonInformationResult, UpdatePersonInformationInput> {

	public UpdatePersonInformationResult Process(UpdatePersonInformationInput inputParam, MDataMap mRequestMap) {
		
		UpdatePersonInformationResult result = new UpdatePersonInformationResult();
		  
		boolean flag = true; // 昵称重复标识

		boolean logo = true; // 敏感词标识

		// 设置相关信息
		if (result.upFlagTrue()) {

			// 查出个人资料
			MDataMap minfoMap = DbUp.upTable("mc_extend_info_star").one("member_code", getUserCode(), "app_code", getManageCode());

			// 查出所有的用户 来比较用户名是否重复
			String sql = "SELECT nickname from mc_extend_info_star where app_code='SI2007'";
			List<Map<String, Object>> list = DbUp.upTable("mc_extend_info_star").dataSqlList(sql, null);
			
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).get("nickname").equals(inputParam.getNickname())&& !inputParam.getNickname().equals(minfoMap.get("nickname").toString())) {

						flag = false;

					}
				}
			}
			// 查出敏感词库
			String datawhere = "SELECT * from nc_sensitive_word";
			List<Map<String, Object>> sensitiveList = DbUp.upTable("nc_sensitive_word").dataSqlList(datawhere, null);

			if (sensitiveList != null && sensitiveList.size() > 0) {
				for (int i = 0; i < sensitiveList.size(); i++) {
					// 包含敏感词不让修改
					if (inputParam.getNickname().contains(sensitiveList.get(i).get("sensitive_word").toString())) {

						logo = false;

					}
				}
			}
			// 用户名重复不允许修改
			if (flag) {

				if (logo) {

					// 修改
					minfoMap.put("member_avatar", inputParam.getAvatar());

					minfoMap.put("member_sex", inputParam.getSex());

					minfoMap.put("nickname", inputParam.getNickname());

					minfoMap.put("birthday", inputParam.getBirthday());

					minfoMap.put("area_code", inputParam.getArea());

					DbUp.upTable("mc_extend_info_star").update(minfoMap);
				} else {
					result.setResultCode(3);
					result.setResultMessage("该昵称包含敏感词，换一个新的哦~");
				}

			} else {
				result.setResultCode(2);
				result.setResultMessage("该昵称已被使用哦~");
			}
			// 查出默认地址
			MDataMap mAddressMap = DbUp.upTable("nc_address").one("app_code",getManageCode(), "address_code", getUserCode(),"address_default", "1");

			// 判断原来是否有默认地址
			if (mAddressMap != null) {
				// 判断是否传入地址信息
				if (inputParam.getAdress().getId().equals("") || inputParam.getAdress().getId() == null) {

					/* 之前的换成非默认 */
					mAddressMap.put("address_default", "0");

					DbUp.upTable("nc_address").update(mAddressMap);

				} else {

					// 不是默认
					if (!mAddressMap.get("address_id").equals(inputParam.getAdress().getId())) {

						/* 之前的换成非默认 */
						mAddressMap.put("address_default", "0");

						DbUp.upTable("nc_address").update(mAddressMap);

						// 现在的新地址设为默认
						MDataMap map = DbUp.upTable("nc_address").one("app_code", getManageCode(), "address_code",getUserCode(), "address_id",inputParam.getAdress().getId());

						map.put("address_default", "1");

						/* 更新为默认地址 */
						DbUp.upTable("nc_address").update(map);
					}
				}

			} else {

				if (inputParam.getAdress().getId().equals("")|| inputParam.getAdress().getId() == null) {

				} else {

					// 现在的新地址设为默认
					MDataMap map = DbUp.upTable("nc_address").one("app_code",getManageCode(), "address_code", getUserCode(),"address_id", inputParam.getAdress().getId());

					map.put("address_default", "1");

					/* 更新为默认地址 */
					DbUp.upTable("nc_address").update(map);

				}

			}

			// 查出用户 信息
			MDataMap mUserMap = DbUp.upTable("mc_extend_info_star").one("member_code", getUserCode(), "app_code", getManageCode());

			result.getUserInfo().setUserid(getUserCode());
			result.getUserInfo().setAvatar(mUserMap.get("member_avatar"));
			result.getUserInfo().setNickname(mUserMap.get("nickname"));
			result.getUserInfo().setPhone(mUserMap.get("mobile_phone"));
			result.getUserInfo().setSkin_type(mUserMap.get("skin_type"));
			result.getUserInfo().setArea_code(mUserMap.get("area_code"));
			result.getUserInfo().setBirthday(mUserMap.get("birthday"));
			if(!"".equals(mUserMap.get("birthday"))){
				/*年代
				 */
				if(Integer.valueOf(mUserMap.get("birthday").substring(3,4).toString())<5){
					
					result.getUserInfo().setCentury(mUserMap.get("birthday").substring(2,3)+"0");
					
				}else {
					
					result.getUserInfo().setCentury(mUserMap.get("birthday").substring(2,3)+"5");
				}	
					
			}

		}
		return result;
	}

}
