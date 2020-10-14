package com.cmall.newscenter.beauty.api;

import org.apache.commons.lang.StringUtils;

import com.cmall.newscenter.beauty.model.PersonCenterInput;
import com.cmall.newscenter.beauty.model.PersonCenterResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 惠美丽—个人中心Api
 * 
 * @author yangrong date: 2014-08-20
 * @version1.0
 */
public class PersonCenter extends
		RootApiForToken<PersonCenterResult, PersonCenterInput> {

	public PersonCenterResult Process(PersonCenterInput inputParam,
			MDataMap mRequestMap) {

		PersonCenterResult result = new PersonCenterResult();

		// 设置相关信息
		if (result.upFlagTrue()) {

			// 查出用户 信息
			MDataMap mUserMap = DbUp.upTable("mc_extend_info_star").one(
					"member_code", getUserCode(), "app_code", getManageCode());

			if (mUserMap != null) {

				result.setAvatar(mUserMap.get("member_avatar"));

				if(!"".equals(mUserMap.get("birthday"))){
					/*年代
					 */
					if(Integer.valueOf(mUserMap.get("birthday").substring(3,4).toString())<5){
						
						result.setCentury(mUserMap.get("birthday").substring(2, 3)+"0");
						
					}else {
						
						result.setCentury(mUserMap.get("birthday").substring(2, 3)+"5");
					}	
						
					}
				
				result.setNickname(mUserMap.get("nickname"));
				
				result.setSex(mUserMap.get("member_sex"));
				
				if(StringUtils.isEmpty(result.getSex())){
					
					result.setSex("4497465100010003");
				}
				
				if (StringUtils.isEmpty(result.getNickname())) {

					String sLoginName = getOauthInfo().getLoginName();

					result.setNickname(StringUtils.substring(sLoginName, 0, 3)
							+ "*****"
							+ StringUtils.substring(sLoginName, 8, 11));
				}

				result.setSkin_type(mUserMap.get("skin_type"));
			}
		}
		return result;
	}

}
