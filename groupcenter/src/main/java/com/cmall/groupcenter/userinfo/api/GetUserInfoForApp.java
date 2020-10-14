package com.cmall.groupcenter.userinfo.api;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.userinfo.model.UserInfoInput;
import com.cmall.groupcenter.userinfo.model.UserInfoResult;
import com.cmall.groupcenter.userinfo.model.UserInfoResult.UserInfo;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webdo.WebTemp;

public class GetUserInfoForApp extends RootApiForToken<UserInfoResult, RootInput>{

	public UserInfoResult Process(RootInput inputParam, MDataMap mRequestMap) {
UserInfoResult userInfoResult = new UserInfoResult();
		String headIconUrl="";
		if (userInfoResult.upFlagTrue()) {
			// 查出用户 信息
			MDataMap mUserMap = DbUp.upTable("mc_extend_info_groupcenter").one("member_code", getUserCode(), "app_code", getManageCode());

			//为了获取账号的创建时间
			MDataMap mcLoginInfoMap = DbUp.upTable("mc_login_info").one("member_code",getUserCode());

			UserInfo userInfo = null;
			if(mUserMap != null){
				userInfo = userInfoResult.new UserInfo();
				userInfo.setBirthday(mUserMap.get("birthday"));
				userInfo.setGender(GetGender(mUserMap.get("gender")));
				userInfo.setHeadIconUrl(mUserMap.get("head_icon_url"));
				userInfo.setMemberCode(mUserMap.get("member_code"));
				userInfo.setMemberName(mUserMap.get("member_name"));
				userInfo.setNickName(mUserMap.get("nickname"));
				userInfo.setCreateTime(mcLoginInfoMap.get("create_time"));
				if(StringUtils.isEmpty(userInfo.getNickName())){
					userInfo.setNickName(getDefaultNickName());
				}
				userInfo.setRegion(mUserMap.get("region"));
				headIconUrl=userInfo.getHeadIconUrl();
			}else{
				userInfo = userInfoResult.new UserInfo();
				userInfo.setCreateTime(mcLoginInfoMap.get("create_time"));
				String nickName = getDefaultNickName();
				if(!StringUtils.isEmpty(nickName)){
					userInfo.setNickName(nickName);
					userInfo.setGender("4497465100010003");
				}
			}
			MDataMap userMap = DbUp.upTable("mc_login_info").one("member_code", getUserCode(), "manage_code", getManageCode());
			if(userMap!=null){
				userInfo.setQrCodeUrl(FormatHelper.formatString(bConfig("groupcenter.twoAppUrl"),userMap.get("login_name"),WebTemp.upTempDataOne("za_apiauthorize", "api_key", "manage_code",getManageCode())));
				userInfo.setQrCodeFlowUrl(FormatHelper.formatString(bConfig("groupcenter.twoFlowUrl"),encodeMobile(userMap.get("login_name")),WebTemp.upTempDataOne("za_apiauthorize", "api_key", "manage_code",getManageCode()),getUserCode())+"&type=two&headIconUrl="+headIconUrl);
			}
			userInfoResult.setUserInfo(userInfo);
			
		}
		return userInfoResult;
		
	}
	
	//手机加密
	 public String encodeMobile(String mobile) {
	    	if(mobile==null || mobile.length()==0){
	    		return "";
	    	}
	    	String[] encryptArr = new String[]{"j","c","a","b","u","i","p","o","y","q","w","x","m"};
	    	String encodeMobile = "";
			for(int i=0;i<mobile.length();i++){
				for(int j=0;j<encryptArr.length;j++){
					encodeMobile +=encryptArr[Integer.parseInt(mobile.substring(i,i+1))];
						break;
				}
			}
			return encodeMobile;
	    }
	
	private String getDefaultNickName(){
		MDataMap mUserMap = DbUp.upTable("mc_login_info").one("member_code", getUserCode(), "manage_code", getManageCode());
		if(mUserMap != null){
			String loginName = mUserMap.get("login_name");
			return loginName.substring(0, 3) + "****" + loginName.substring(7);
		}
		return "";
	}
	
	//修改人wangzx  因安卓那边，传递过来的是中文暂时那边改不了，所以在此做了过滤匹配
	public String GetGender(String gender){
		if(gender==null || gender.length()==0 || gender.equals("女")){
			return "4497465100010003";
		}else
		if(gender.equals("男")){
			return "4497465100010002";
		}
		else {
			return gender;
		}
	}
	
	
}
