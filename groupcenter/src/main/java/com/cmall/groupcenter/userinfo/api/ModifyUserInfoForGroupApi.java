package com.cmall.groupcenter.userinfo.api;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.userinfo.model.UserInfoInput;
import com.cmall.groupcenter.userinfo.model.UserInfoResult;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 微工社修改用户信息
 * 
 * @author chenxk
 *
 */
public class ModifyUserInfoForGroupApi extends
	RootApiForToken<UserInfoResult, UserInfoInput> {

	public UserInfoResult Process(
			UserInfoInput inputParam, MDataMap mRequestMap) {
		
		UserInfoResult userInfoResult = new UserInfoResult();
		
		MDataMap memberInfo = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode());
		if(memberInfo == null){
			userInfoResult.inErrorMessage(915805334);
		}
		// 锁定账户编号
		String sLockCode = WebHelper.addLock(100, memberInfo.get("account_code"));
		
		if(!StringUtils.isEmpty(sLockCode)){
			// 查出敏感词库
			String datawhere = "SELECT sensitive_word from nc_sensitive_word";
			List<Map<String, Object>> sensitiveList = DbUp.upTable("nc_sensitive_word").dataSqlList(datawhere, null);

			if (sensitiveList != null && sensitiveList.size() > 0) {
				for (int i = 0; i < sensitiveList.size(); i++) {
					// 包含敏感词不让修改
					if (inputParam.getNickName().contains(sensitiveList.get(i).get("sensitive_word").toString())) {
						userInfoResult.inErrorMessage(918505177);
					}
				}
			}
			if(userInfoResult.upFlagTrue()){
				MDataMap userMapDataMap = DbUp.upTable("mc_login_info").one("member_code",getUserCode());
				if(null != userMapDataMap && !StringUtils.isEmpty(inputParam.getNickName())){
					String nickNameString = userMapDataMap.get("login_name").toString().substring(0, 3)+userMapDataMap.get("login_name").toString().substring(7);
					if(nickNameString.equals(inputParam.getNickName())){
						inputParam.setNickName(userMapDataMap.get("login_name").toString().substring(0, 3)+"****"+userMapDataMap.get("login_name").toString().substring(7));
					}
				}
				//验证昵称是否存在
				if(!StringUtils.isEmpty(inputParam.getNickName())){
					String uqiqueSql="select uid from mc_extend_info_groupcenter where  member_code!=:member_code and nickname=:nickname ";
					List<Map<String, Object>> nikenameList = DbUp.upTable("mc_extend_info_groupcenter").dataSqlList(uqiqueSql, new MDataMap("member_code",getUserCode(),"nickname",inputParam.getNickName()));
			        if(nikenameList!=null && nikenameList.size()>0){
						userInfoResult.inErrorMessage(918505176);
			        }
				} 
				if(userInfoResult.upFlagTrue()){
					String sql="select uid from mc_extend_info_groupcenter where member_code=:member_code";
					List<Map<String, Object>> infoList = DbUp.upTable("mc_extend_info_groupcenter").dataSqlList(sql, new MDataMap("member_code",getUserCode()));
					filterGender(inputParam);
					if(infoList == null || infoList.size() == 0){
						//insert
						DbUp.upTable("mc_extend_info_groupcenter").insert("uid",WebHelper.upUuid(),
								"member_code",getUserCode(),
								"head_icon_url",inputParam.getHeadIconUrl(),
								"nickname",inputParam.getNickName(),
								"birthday",inputParam.getBirthday(),
								"gender",inputParam.getGender(),
								"status","449746600001",
								"create_time",DateHelper.upNow(),
								"last_update_time",DateHelper.upNow(),
								"region",inputParam.getRegion(),
								"mobile",this.getOauthInfo().getLoginName(),
								"app_code",getManageCode());
					}else{
						MDataMap mDataMap = new MDataMap();
						String updateField = "";
						mDataMap.put("uid", (String)infoList.get(0).get("uid"));
						if(!StringUtils.isEmpty(inputParam.getHeadIconUrl())){
							mDataMap.put("head_icon_url", inputParam.getHeadIconUrl());
							updateField +="head_icon_url,";
						}
						if(!StringUtils.isEmpty(inputParam.getNickName())){
							mDataMap.put("nickname", inputParam.getNickName());
							updateField +="nickname,";
						} 
						
						if(!StringUtils.isEmpty(inputParam.getBirthday())){
							mDataMap.put("birthday", inputParam.getBirthday());
							updateField +="birthday,";
						}
						if(!StringUtils.isEmpty(inputParam.getGender())){
							mDataMap.put("gender", inputParam.getGender());
							updateField +="gender,";
						}
						
						if(!StringUtils.isEmpty(inputParam.getRegion())){
							mDataMap.put("region", inputParam.getRegion());
							updateField +="region";
						}
						if(updateField.length() > 0){
							if(",".equals(updateField.substring(updateField.length()-1,updateField.length()))){
								updateField = updateField.substring(0,updateField.length()-1);
							}
							//update
							DbUp.upTable("mc_extend_info_groupcenter").dataUpdate(mDataMap, 
									updateField, 
									"uid");
						}
					}
				}
			}
			// 解鎖
			WebHelper.unLock(sLockCode);
		}
		return userInfoResult;
	}
	
	//修改人wagnzx  因安卓那边，传递过来的是中文暂时那边改不了，所以在此做了过滤匹配
	public void filterGender(UserInfoInput inputParam){
		//2015-12-17 panwei
		if(inputParam.getGender().equals("男")||inputParam.getGender().equals("4497465100010002")){
			inputParam.setGender("4497465100010002");
		}else 
		if(inputParam.getGender().equals("女")||inputParam.getGender().equals("4497465100010003")){
			inputParam.setGender("4497465100010003");
		}else{
			inputParam.setGender("4497465100010001");
		}
	}
	
}
