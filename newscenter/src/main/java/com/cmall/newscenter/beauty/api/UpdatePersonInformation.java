package com.cmall.newscenter.beauty.api;

import java.util.List;
import java.util.Map;

import com.cmall.newscenter.beauty.model.UpdatePersonInformationInput;
import com.cmall.newscenter.beauty.model.UpdatePersonInformationResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 惠美丽—修改个人资料(昵称唯一&&敏感词过滤)Api
 * 
 * @author yangrong date: 2014-08-20
 * @version1.0
 */
public class UpdatePersonInformation extends RootApiForToken<UpdatePersonInformationResult, UpdatePersonInformationInput> {

	public UpdatePersonInformationResult Process(
			UpdatePersonInformationInput inputParam, MDataMap mRequestMap) {
		
		UpdatePersonInformationResult result = new UpdatePersonInformationResult();
		
		if(!inputParam.getHopeful().equals("")){
			
			String[] list = inputParam.getHopeful().split(",");
			
			for (int i = 0; i < list.length; i++) {
				
				if(!(list[i].equals("449746660001")||list[i].equals("449746660002")||list[i].equals("449746660003")||list[i].equals("449746660004")||list[i].equals("449746660005")||list[i].equals("449746660006")||list[i].equals("449746660007"))){
				
					int count = DbUp.upTable("nc_skin_hopeful").count("hopeful_code",list[i]);
					
					if(count==0){
						result.setResultCode(934205139);
						result.setResultMessage(bInfo(934205139));
					}
				}
			}
		}
		
		if(!inputParam.getSkin_type().equals("")&&!(inputParam.getSkin_type().equals("449746650005")||inputParam.getSkin_type().equals("449746650004")||inputParam.getSkin_type().equals("449746650003")||inputParam.getSkin_type().equals("449746650002")||inputParam.getSkin_type().equals("449746650001"))){
			
			int count = DbUp.upTable("nc_skin_type").count("skin_code",inputParam.getSkin_type());
			
			if(count==0){
				result.setResultCode(934205140);
				result.setResultMessage(bInfo(934205140));
			}
		}

		boolean flag = true; // 昵称重复标识

		boolean logo = true; // 敏感词标识

		// 设置相关信息
		if (result.upFlagTrue()) {

			// 查出个人资料
			MDataMap minfoMap = DbUp.upTable("mc_extend_info_star").one("member_code", getUserCode(), "app_code", getManageCode());

			// 查出所有的用户 来比较用户名是否重复
			String sql = "SELECT nickname from mc_extend_info_star where app_code='"+getManageCode()+"'";
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

					minfoMap.put("skin_type", inputParam.getSkin_type());

					minfoMap.put("hopeful", inputParam.getHopeful());
					
					minfoMap.put("birthday", inputParam.getBirthday());
					
					minfoMap.put("area_code", inputParam.getArea_code());

					DbUp.upTable("mc_extend_info_star").update(minfoMap);
				} else {
					result.setResultCode(934205138);
					result.setResultMessage(bInfo(934205138));
				}

			} else {
				result.setResultCode(934205137);
				result.setResultMessage(bInfo(934205137));
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
